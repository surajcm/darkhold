package com.quiz.darkhold.team.controller;

import com.quiz.darkhold.team.model.TeamAssignmentMethod;
import com.quiz.darkhold.team.model.TeamConfig;
import com.quiz.darkhold.team.model.TeamInfo;
import com.quiz.darkhold.team.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(final TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createTeams(@RequestBody final Map<String, Object> request) {
        try {
            String pin = (String) request.get("pin");
            Integer teamCount = (Integer) request.get("teamCount");
            String assignmentMethodStr = (String) request.get("assignmentMethod");

            TeamConfig config = new TeamConfig();
            config.setTeamCount(teamCount);
            config.setAssignmentMethod(TeamAssignmentMethod.valueOf(assignmentMethodStr));

            @SuppressWarnings("unchecked")
            Map<String, String> teamNames = (Map<String, String>) request.get("teamNames");
            if (teamNames != null) {
                config.setTeamNames(teamNames);
            }

            teamService.createTeams(pin, config);
            return ResponseEntity.ok("Teams created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating teams: " + e.getMessage());
        }
    }

    @PostMapping("/assign")
    @ResponseBody
    public ResponseEntity<String> assignPlayerToTeam(@RequestBody final Map<String, String> request) {
        try {
            String pin = request.get("pin");
            String username = request.get("username");
            String teamName = request.get("teamName");

            teamService.assignPlayerToTeam(pin, username, teamName);
            return ResponseEntity.ok("Player assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error assigning player: " + e.getMessage());
        }
    }

    @GetMapping("/list/{pin}")
    @ResponseBody
    public ResponseEntity<List<TeamInfo>> getTeams(@PathVariable final String pin) {
        try {
            List<TeamInfo> teams = teamService.getTeams(pin);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/scores/{pin}")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getTeamScores(@PathVariable final String pin) {
        try {
            Map<String, Integer> scores = teamService.calculateTeamScores(pin);
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/player/{pin}")
    @ResponseBody
    public ResponseEntity<String> getPlayerTeam(
            @PathVariable final String pin,
            @RequestParam final String username) {
        try {
            String team = teamService.getPlayerTeam(pin, username);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
