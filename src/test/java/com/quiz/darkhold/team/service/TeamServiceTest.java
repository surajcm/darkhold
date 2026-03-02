package com.quiz.darkhold.team.service;

import com.quiz.darkhold.game.model.ScoreResult;
import com.quiz.darkhold.preview.entity.CurrentGameSession;
import com.quiz.darkhold.preview.repository.CurrentGameSessionRepository;
import com.quiz.darkhold.team.dto.TeamScoreResult;
import com.quiz.darkhold.team.model.TeamAssignmentMethod;
import com.quiz.darkhold.team.model.TeamConfig;
import com.quiz.darkhold.team.model.TeamInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService Tests")
class TeamServiceTest {

    @Mock
    private CurrentGameSessionRepository currentGameRepository;

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(currentGameRepository);
    }

    @Nested
    @DisplayName("createTeams tests")
    class CreateTeamsTests {

        @Test
        @DisplayName("Should create correct number of teams")
        void shouldCreateCorrectNumberOfTeams() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));
            when(currentGameRepository.save(any())).thenReturn(session);

            TeamConfig config = new TeamConfig(3, TeamAssignmentMethod.BALANCED);
            teamService.createTeams("12345", config);

            ArgumentCaptor<CurrentGameSession> captor = ArgumentCaptor.forClass(CurrentGameSession.class);
            verify(currentGameRepository).save(captor.capture());
            assertNotNull(captor.getValue().getTeamsJson());
        }

        @Test
        @DisplayName("Should use default team names when not customized")
        void shouldUseDefaultTeamNamesWhenNotCustomized() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));
            when(currentGameRepository.save(any())).thenReturn(session);

            TeamConfig config = new TeamConfig(2, TeamAssignmentMethod.BALANCED);
            teamService.createTeams("12345", config);

            List<TeamInfo> teams = teamService.getTeams("12345");
            assertEquals(2, teams.size());
        }

        @Test
        @DisplayName("Should cap teams at TEAM_COLORS length")
        void shouldCapTeamsAtTeamColorsLength() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));
            when(currentGameRepository.save(any())).thenReturn(session);

            TeamConfig config = new TeamConfig(100, TeamAssignmentMethod.BALANCED);
            teamService.createTeams("12345", config);

            List<TeamInfo> teams = teamService.getTeams("12345");
            assertEquals(6, teams.size());
        }

        @Test
        @DisplayName("Should throw when game not found")
        void shouldThrowWhenGameNotFound() {
            when(currentGameRepository.findByPin("99999")).thenReturn(Optional.empty());

            TeamConfig config = new TeamConfig(2, TeamAssignmentMethod.BALANCED);
            assertThrows(IllegalArgumentException.class,
                    () -> teamService.createTeams("99999", config));
        }
    }

    @Nested
    @DisplayName("getAssignmentMethod tests")
    class GetAssignmentMethodTests {

        @Test
        @DisplayName("Should return method from session")
        void shouldReturnMethodFromSession() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            session.setTeamAssignmentMethod(TeamAssignmentMethod.RANDOM);
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            TeamAssignmentMethod method = teamService.getAssignmentMethod("12345");

            assertEquals(TeamAssignmentMethod.RANDOM, method);
        }

        @Test
        @DisplayName("Should default to BALANCED when null")
        void shouldDefaultToBalancedWhenNull() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            TeamAssignmentMethod method = teamService.getAssignmentMethod("12345");

            assertEquals(TeamAssignmentMethod.BALANCED, method);
        }

        @Test
        @DisplayName("Should throw on game not found")
        void shouldThrowOnGameNotFound() {
            when(currentGameRepository.findByPin("99999")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> teamService.getAssignmentMethod("99999"));
        }
    }

    @Nested
    @DisplayName("setAssignmentMethod tests")
    class SetAssignmentMethodTests {

        @Test
        @DisplayName("Should update and save session")
        void shouldUpdateAndSaveSession() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            teamService.setAssignmentMethod("12345", TeamAssignmentMethod.RANDOM);

            verify(currentGameRepository).save(session);
            assertEquals(TeamAssignmentMethod.RANDOM, session.getTeamAssignmentMethod());
        }
    }

    @Nested
    @DisplayName("assignPlayerToTeam tests")
    class AssignPlayerToTeamTests {

        @Test
        @DisplayName("Should add player to target team")
        void shouldAddPlayerToTargetTeam() {
            CurrentGameSession session = createSessionWithTeams("12345");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));
            when(currentGameRepository.save(any())).thenReturn(session);

            teamService.assignPlayerToTeam("12345", "player1", "Team Red");

            assertEquals("Team Red", teamService.getPlayerTeam("12345", "player1"));
        }

        @Test
        @DisplayName("Should remove player from old team on reassignment")
        void shouldRemoveFromOldTeamOnReassignment() {
            CurrentGameSession session = createSessionWithTeams("12345");
            Map<String, String> playerTeams = new HashMap<>();
            playerTeams.put("player1", "Team Red");
            session.setPlayerTeamsMap(playerTeams);
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));
            when(currentGameRepository.save(any())).thenReturn(session);

            teamService.assignPlayerToTeam("12345", "player1", "Team Blue");

            assertEquals("Team Blue", teamService.getPlayerTeam("12345", "player1"));
        }
    }

    @Nested
    @DisplayName("calculateTeamScores tests")
    class CalculateTeamScoresTests {

        @Test
        @DisplayName("Should aggregate player scores by team")
        void shouldAggregatePlayerScoresByTeam() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            Map<String, Integer> scores = new HashMap<>();
            scores.put("player1", 500);
            scores.put("player2", 300);
            session.setScoresMap(scores);
            Map<String, String> playerTeams = new HashMap<>();
            playerTeams.put("player1", "Team Red");
            playerTeams.put("player2", "Team Red");
            session.setPlayerTeamsMap(playerTeams);
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            Map<String, Integer> teamScores = teamService.calculateTeamScores("12345");

            assertEquals(800, teamScores.get("Team Red"));
        }

        @Test
        @DisplayName("Should exclude moderator from team scores")
        void shouldExcludeModeratorFromTeamScores() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            Map<String, Integer> scores = new HashMap<>();
            scores.put("host", 1000);
            scores.put("player1", 500);
            session.setScoresMap(scores);
            Map<String, String> playerTeams = new HashMap<>();
            playerTeams.put("host", "Team Red");
            playerTeams.put("player1", "Team Red");
            session.setPlayerTeamsMap(playerTeams);
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            Map<String, Integer> teamScores = teamService.calculateTeamScores("12345");

            assertEquals(500, teamScores.get("Team Red"));
        }

        @Test
        @DisplayName("Should handle null player scores")
        void shouldHandleNullPlayerScores() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            Map<String, Integer> teamScores = teamService.calculateTeamScores("12345");

            assertTrue(teamScores.isEmpty());
        }
    }

    @Nested
    @DisplayName("getTeamScoreResults tests")
    class GetTeamScoreResultsTests {

        @Test
        @DisplayName("Should return sorted results by score descending")
        void shouldReturnSortedResultsByScoreDesc() {
            CurrentGameSession session = createSessionWithTeams("12345");
            Map<String, Integer> scores = new HashMap<>();
            scores.put("player1", 300);
            scores.put("player2", 500);
            session.setScoresMap(scores);
            Map<String, String> playerTeams = new HashMap<>();
            playerTeams.put("player1", "Team Red");
            playerTeams.put("player2", "Team Blue");
            session.setPlayerTeamsMap(playerTeams);
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            ScoreResult sr1 = new ScoreResult();
            sr1.setUsername("player1");
            ScoreResult sr2 = new ScoreResult();
            sr2.setUsername("player2");

            List<TeamScoreResult> results = teamService.getTeamScoreResults("12345", List.of(sr1, sr2));

            assertEquals(2, results.size());
            assertEquals(1, results.get(0).getRank());
            assertEquals(2, results.get(1).getRank());
            assertTrue(results.get(0).getTotalScore() >= results.get(1).getTotalScore());
        }
    }

    @Nested
    @DisplayName("getTeams tests")
    class GetTeamsTests {

        @Test
        @DisplayName("Should return teams from session")
        void shouldReturnTeamsFromSession() {
            CurrentGameSession session = createSessionWithTeams("12345");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            List<TeamInfo> teams = teamService.getTeams("12345");

            assertEquals(2, teams.size());
        }

        @Test
        @DisplayName("Should return empty list for no teams")
        void shouldReturnEmptyListForNoTeams() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            List<TeamInfo> teams = teamService.getTeams("12345");

            assertTrue(teams.isEmpty());
        }
    }

    @Nested
    @DisplayName("getPlayerTeam tests")
    class GetPlayerTeamTests {

        @Test
        @DisplayName("Should return team name for player")
        void shouldReturnTeamNameForPlayer() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            Map<String, String> playerTeams = new HashMap<>();
            playerTeams.put("player1", "Team Red");
            session.setPlayerTeamsMap(playerTeams);
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            String team = teamService.getPlayerTeam("12345", "player1");

            assertEquals("Team Red", team);
        }

        @Test
        @DisplayName("Should return null for unknown player")
        void shouldReturnNullForUnknownPlayer() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            String team = teamService.getPlayerTeam("12345", "unknown");

            assertNull(team);
        }
    }

    @Nested
    @DisplayName("isTeamMode tests")
    class IsTeamModeTests {

        @Test
        @DisplayName("Should return false for null session")
        void shouldReturnFalseForNullSession() {
            when(currentGameRepository.findByPin("99999")).thenReturn(Optional.empty());

            assertFalse(teamService.isTeamMode("99999"));
        }

        @Test
        @DisplayName("Should return false when no teams")
        void shouldReturnFalseWhenNoTeams() {
            CurrentGameSession session = new CurrentGameSession("12345", "host");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            assertFalse(teamService.isTeamMode("12345"));
        }

        @Test
        @DisplayName("Should return true when teams exist")
        void shouldReturnTrueWhenTeamsExist() {
            CurrentGameSession session = createSessionWithTeams("12345");
            when(currentGameRepository.findByPin("12345")).thenReturn(Optional.of(session));

            assertTrue(teamService.isTeamMode("12345"));
        }
    }

    // Helper methods

    private CurrentGameSession createSessionWithTeams(final String pin) {
        CurrentGameSession session = new CurrentGameSession(pin, "host");
        String teamsJson = "[{\"name\":\"Team Red\",\"color\":\"red\",\"members\":[],\"score\":0},"
                + "{\"name\":\"Team Blue\",\"color\":\"blue\",\"members\":[],\"score\":0}]";
        session.setTeamsJson(teamsJson);
        return session;
    }
}
