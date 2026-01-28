package com.quiz.darkhold.team.service;

import com.quiz.darkhold.game.model.ScoreResult;
import com.quiz.darkhold.preview.entity.CurrentGameSession;
import com.quiz.darkhold.preview.repository.CurrentGameSessionRepository;
import com.quiz.darkhold.team.dto.TeamScoreResult;
import com.quiz.darkhold.team.model.TeamAssignmentMethod;
import com.quiz.darkhold.team.model.TeamConfig;
import com.quiz.darkhold.team.model.TeamInfo;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final CurrentGameSessionRepository currentGameRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    private static final String[] TEAM_COLORS = {"red", "blue", "green", "yellow", "purple", "orange"};
    private static final String TEAM_NAME_PREFIX = "Team ";

    public TeamService(final CurrentGameSessionRepository currentGameRepository) {
        this.currentGameRepository = currentGameRepository;
    }

    public void createTeams(final String pin, final TeamConfig config) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<TeamInfo> teams = new ArrayList<>();
        int teamCount = Math.min(config.getTeamCount(), TEAM_COLORS.length);

        for (int i = 0; i < teamCount; i++) {
            String color = TEAM_COLORS[i];
            String name = config.getTeamNames().getOrDefault(color, TEAM_NAME_PREFIX + capitalize(color));
            TeamInfo team = new TeamInfo(name, color);
            teams.add(team);
        }

        // Store the assignment method for auto-assigning players when they join
        session.setTeamAssignmentMethod(config.getAssignmentMethod());
        saveTeamsToSession(session, teams);
    }

    public TeamAssignmentMethod getAssignmentMethod(final String pin) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        TeamAssignmentMethod method = session.getTeamAssignmentMethod();
        return method != null ? method : TeamAssignmentMethod.BALANCED;
    }

    public void setAssignmentMethod(final String pin, final TeamAssignmentMethod method) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        session.setTeamAssignmentMethod(method);
        currentGameRepository.save(session);
    }

    public void assignPlayerToTeam(final String pin, final String username, final String teamName) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<TeamInfo> teams = getTeamsFromSession(session);
        Map<String, String> playerTeams = session.getPlayerTeamsMap();

        String oldTeam = playerTeams.get(username);
        if (oldTeam != null) {
            teams.stream()
                    .filter(t -> t.getName().equals(oldTeam))
                    .findFirst()
                    .ifPresent(t -> t.removeMember(username));
        }

        teams.stream()
                .filter(t -> t.getName().equals(teamName))
                .findFirst()
                .ifPresent(t -> t.addMember(username));

        playerTeams.put(username, teamName);

        saveTeamsToSession(session, teams);
        session.setPlayerTeamsMap(playerTeams);
        currentGameRepository.save(session);
    }

    public void autoAssignPlayer(final String pin, final String username, final TeamAssignmentMethod method) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<TeamInfo> teams = getTeamsFromSession(session);
        if (teams.isEmpty()) {
            throw new IllegalStateException("No teams configured for this game");
        }

        TeamInfo targetTeam;
        if (method == TeamAssignmentMethod.RANDOM) {
            targetTeam = teams.get(random.nextInt(teams.size()));
        } else {
            targetTeam = teams.stream()
                    .min(Comparator.comparingInt(TeamInfo::getMemberCount))
                    .orElseThrow();
        }

        assignPlayerToTeam(pin, username, targetTeam.getName());
    }

    public Map<String, Integer> calculateTeamScores(final String pin) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Map<String, Integer> playerScores = session.getScoresMap();
        if (playerScores == null) {
            playerScores = new HashMap<>();
        }

        Map<String, String> playerTeams = session.getPlayerTeamsMap();
        Map<String, Integer> teamScores = new HashMap<>();

        for (Map.Entry<String, Integer> entry : playerScores.entrySet()) {
            String player = entry.getKey();
            Integer score = entry.getValue();
            String team = playerTeams.get(player);

            if (team != null && !player.equals(session.getModerator())) {
                teamScores.merge(team, score, Integer::sum);
            }
        }

        return teamScores;
    }

    public List<TeamScoreResult> getTeamScoreResults(final String pin, final List<ScoreResult> playerResults) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<TeamInfo> teams = getTeamsFromSession(session);
        Map<String, String> playerTeams = session.getPlayerTeamsMap();
        Map<String, Integer> teamScores = calculateTeamScores(pin);

        List<TeamScoreResult> teamResults = new ArrayList<>();

        for (TeamInfo team : teams) {
            TeamScoreResult result = new TeamScoreResult();
            result.setTeamName(team.getName());
            result.setColor(team.getColor());
            result.setTotalScore(teamScores.getOrDefault(team.getName(), 0));

            List<ScoreResult> teamPlayers = playerResults.stream()
                    .filter(p -> team.getName().equals(playerTeams.get(p.getUsername())))
                    .collect(Collectors.toList());

            result.setIndividualScores(teamPlayers);
            teamResults.add(result);
        }

        teamResults.sort(Comparator.comparing(TeamScoreResult::getTotalScore).reversed());

        for (int i = 0; i < teamResults.size(); i++) {
            teamResults.get(i).setRank(i + 1);
        }

        return teamResults;
    }

    public List<TeamInfo> getTeams(final String pin) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        return getTeamsFromSession(session);
    }

    public String getPlayerTeam(final String pin, final String username) {
        CurrentGameSession session = currentGameRepository.findByPin(pin)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        return session.getPlayerTeamsMap().get(username);
    }

    public boolean isTeamMode(final String pin) {
        CurrentGameSession session = currentGameRepository.findByPin(pin).orElse(null);
        if (session == null) {
            return false;
        }
        List<TeamInfo> teams = getTeamsFromSession(session);
        return teams != null && !teams.isEmpty();
    }

    private List<TeamInfo> getTeamsFromSession(final CurrentGameSession session) {
        String teamsJson = session.getTeamsJson();
        if (teamsJson == null || teamsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(teamsJson, new TypeReference<List<TeamInfo>>() { });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveTeamsToSession(final CurrentGameSession session, final List<TeamInfo> teams) {
        try {
            String teamsJson = objectMapper.writeValueAsString(teams);
            session.setTeamsJson(teamsJson);
            currentGameRepository.save(session);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save teams", e);
        }
    }

    private String capitalize(final String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
