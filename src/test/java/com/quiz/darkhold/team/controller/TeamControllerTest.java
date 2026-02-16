package com.quiz.darkhold.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.darkhold.team.model.TeamConfig;
import com.quiz.darkhold.team.model.TeamInfo;
import com.quiz.darkhold.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for TeamController.
 * Tests all REST endpoints for team management.
 */
class TeamControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TeamService teamService;
    private TeamController teamController;

    private final String testPin = "12345";

    @BeforeEach
    void setUp() {
        // Mock all dependencies
        teamService = mock(TeamService.class);
        objectMapper = new ObjectMapper();

        // Create controller with mocked dependencies
        teamController = new TeamController(teamService);

        // Build standalone MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
    }

    // ==================== POST /team/create Tests ====================

    @Test
    void testCreateTeams_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("pin", testPin);
        request.put("teamCount", 3);
        request.put("assignmentMethod", "RANDOM");

        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        mockMvc.perform(post("/team/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Teams created successfully")));

        verify(teamService).createTeams(eq(testPin), any(TeamConfig.class));
    }

    @Test
    void testCreateTeams_WithCustomNames() throws Exception {
        Map<String, String> teamNames = new HashMap<>();
        teamNames.put("1", "Team Red");
        teamNames.put("2", "Team Blue");
        teamNames.put("3", "Team Green");
        Map<String, Object> request = Map.of("pin", testPin, "teamCount", 3,
                "assignmentMethod", "MANUAL", "teamNames", teamNames);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));
        mockMvc.perform(post("/team/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", is("Teams created successfully")));
    }

    @Test
    void testCreateTeams_ServiceException() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("pin", testPin);
        request.put("teamCount", 3);
        request.put("assignmentMethod", "RANDOM");

        doThrow(new RuntimeException("Database error"))
                .when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        mockMvc.perform(post("/team/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Error creating teams: Database error")));
    }

    @Test
    void testCreateTeams_InvalidAssignmentMethod() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("pin", testPin);
        request.put("teamCount", 3);
        request.put("assignmentMethod", "INVALID");

        mockMvc.perform(post("/team/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== POST /team/assign Tests ====================

    @Test
    void testAssignPlayerToTeam_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("pin", testPin);
        request.put("username", "player1");
        request.put("teamName", "Team Red");

        doNothing().when(teamService).assignPlayerToTeam(testPin, "player1", "Team Red");

        mockMvc.perform(post("/team/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Player assigned successfully")));

        verify(teamService).assignPlayerToTeam(testPin, "player1", "Team Red");
    }

    @Test
    void testAssignPlayerToTeam_ServiceException() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("pin", testPin);
        request.put("username", "player1");
        request.put("teamName", "NonExistentTeam");

        doThrow(new RuntimeException("Team not found"))
                .when(teamService).assignPlayerToTeam(anyString(), anyString(), anyString());

        mockMvc.perform(post("/team/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Error assigning player: Team not found")));
    }

    // ==================== GET /team/list/{pin} Tests ====================

    @Test
    void testGetTeams_Success() throws Exception {
        List<TeamInfo> teams = List.of(createTeamInfo("Team Red", 3),
                createTeamInfo("Team Blue", 2));
        when(teamService.getTeams(testPin)).thenReturn(teams);
        mockMvc.perform(get("/team/list/" + testPin))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Team Red")))
                .andExpect(jsonPath("$[0].members", hasSize(3)))
                .andExpect(jsonPath("$[1].name", is("Team Blue")))
                .andExpect(jsonPath("$[1].members", hasSize(2)));
        verify(teamService).getTeams(testPin);
    }

    private TeamInfo createTeamInfo(final String name, final int memberCount) {
        TeamInfo team = new TeamInfo();
        team.setName(name);
        for (int i = 1; i <= memberCount; i++) {
            team.addMember("player" + i);
        }
        return team;
    }

    @Test
    void testGetTeams_EmptyList() throws Exception {
        when(teamService.getTeams(testPin)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/team/list/" + testPin)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetTeams_ServiceException() throws Exception {
        when(teamService.getTeams(testPin)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/team/list/" + testPin)
                        )
                .andExpect(status().isBadRequest());
    }

    // ==================== GET /team/scores/{pin} Tests ====================

    @Test
    void testGetTeamScores_Success() throws Exception {
        Map<String, Integer> teamScores = new HashMap<>();
        teamScores.put("Team Red", 2500);
        teamScores.put("Team Blue", 2000);
        teamScores.put("Team Green", 1800);

        when(teamService.calculateTeamScores(testPin)).thenReturn(teamScores);

        mockMvc.perform(get("/team/scores/" + testPin)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Team Red']", is(2500)))
                .andExpect(jsonPath("$.['Team Blue']", is(2000)))
                .andExpect(jsonPath("$.['Team Green']", is(1800)));

        verify(teamService).calculateTeamScores(testPin);
    }

    @Test
    void testGetTeamScores_EmptyScores() throws Exception {
        when(teamService.calculateTeamScores(testPin)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/team/scores/" + testPin)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetTeamScores_ServiceException() throws Exception {
        when(teamService.calculateTeamScores(testPin))
                .thenThrow(new RuntimeException("Score calculation error"));

        mockMvc.perform(get("/team/scores/" + testPin)
                        )
                .andExpect(status().isBadRequest());
    }

    // ==================== GET /team/player/{pin} Tests ====================

    @Test
    void testGetPlayerTeam_Success() throws Exception {
        when(teamService.getPlayerTeam(testPin, "player1")).thenReturn("Team Red");

        mockMvc.perform(get("/team/player/" + testPin)
                        .param("username", "player1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Team Red")));

        verify(teamService).getPlayerTeam(testPin, "player1");
    }

    @Test
    void testGetPlayerTeam_PlayerNotInTeam() throws Exception {
        when(teamService.getPlayerTeam(testPin, "player999")).thenReturn(null);

        mockMvc.perform(get("/team/player/" + testPin)
                        .param("username", "player999"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPlayerTeam_ServiceException() throws Exception {
        when(teamService.getPlayerTeam(testPin, "player1"))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/team/player/" + testPin)
                        .param("username", "player1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTeams_NotAuthenticated() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("pin", testPin);
        request.put("teamCount", 3);
        request.put("assignmentMethod", "RANDOM");

        mockMvc.perform(post("/team/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
