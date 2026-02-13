package com.quiz.darkhold.score.controller;

import com.quiz.darkhold.game.model.ScoreResult;
import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.team.dto.TeamScoreResult;
import com.quiz.darkhold.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for ScoreController.
 * Tests scoreboard display and WebSocket score updates.
 */
@WebMvcTest(ScoreController.class)
class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @MockBean
    private TeamService teamService;

    private Map<String, Integer> currentScores;
    private Map<String, Integer> previousScores;
    private final String testPin = "12345";

    @BeforeEach
    void setUp() {
        // Setup current scores
        currentScores = new HashMap<>();
        currentScores.put("player1", 1000);
        currentScores.put("player2", 800);
        currentScores.put("player3", 600);

        // Setup previous scores
        previousScores = new HashMap<>();
        previousScores.put("player1", 800);
        previousScores.put("player2", 600);
        previousScores.put("player3", 400);
    }

    // ==================== /scoreboard Endpoint Tests ====================

    @Test
    @WithMockUser
    void testScoreCheck_Success() throws Exception {
        when(gameService.getCurrentScore()).thenReturn(currentScores);
        when(gameService.getPreviousScores()).thenReturn(previousScores);
        when(gameService.getStreak(anyString())).thenReturn(3);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"))
                .andExpect(model().attributeExists("score"))
                .andExpect(model().attributeExists("scoreResults"))
                .andExpect(model().attributeExists("quizPin"))
                .andExpect(model().attribute("quizPin", testPin))
                .andExpect(model().attributeExists("isTeamMode"))
                .andExpect(model().attribute("isTeamMode", false));

        verify(gameService).getCurrentScore();
        verify(gameService).getPreviousScores();
        verify(teamService).isTeamMode(testPin);
    }

    @Test
    @WithMockUser
    void testScoreCheck_WithTeamMode() throws Exception {
        List<TeamScoreResult> teamScores = new ArrayList<>();
        TeamScoreResult team1 = new TeamScoreResult();
        team1.setTeamName("Team Red");
        team1.setTotalScore(2000);
        team1.setMemberCount(3);
        teamScores.add(team1);

        when(gameService.getCurrentScore()).thenReturn(currentScores);
        when(gameService.getPreviousScores()).thenReturn(previousScores);
        when(gameService.getStreak(anyString())).thenReturn(2);
        when(teamService.isTeamMode(testPin)).thenReturn(true);
        when(teamService.getTeamScoreResults(eq(testPin), anyList())).thenReturn(teamScores);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"))
                .andExpect(model().attributeExists("isTeamMode"))
                .andExpect(model().attribute("isTeamMode", true))
                .andExpect(model().attributeExists("teamScores"))
                .andExpect(model().attribute("teamScores", hasSize(1)));

        verify(teamService).isTeamMode(testPin);
        verify(teamService).getTeamScoreResults(eq(testPin), anyList());
    }

    @Test
    @WithMockUser
    void testScoreCheck_EmptyScores() throws Exception {
        Map<String, Integer> emptyScores = new HashMap<>();
        when(gameService.getCurrentScore()).thenReturn(emptyScores);
        when(gameService.getPreviousScores()).thenReturn(emptyScores);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"))
                .andExpect(model().attributeExists("scoreResults"));
    }

    @Test
    @WithMockUser
    void testScoreCheck_WithNullPin() throws Exception {
        when(gameService.getCurrentScore()).thenReturn(currentScores);
        when(gameService.getPreviousScores()).thenReturn(previousScores);
        when(gameService.getStreak(anyString())).thenReturn(1);
        when(teamService.isTeamMode(null)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"))
                .andExpect(model().attributeExists("score"))
                .andExpect(model().attributeExists("scoreResults"));
    }

    @Test
    @WithMockUser
    void testScoreCheck_SinglePlayer() throws Exception {
        Map<String, Integer> singleScore = new HashMap<>();
        singleScore.put("player1", 1500);

        Map<String, Integer> singlePrevScore = new HashMap<>();
        singlePrevScore.put("player1", 1000);

        when(gameService.getCurrentScore()).thenReturn(singleScore);
        when(gameService.getPreviousScores()).thenReturn(singlePrevScore);
        when(gameService.getStreak("player1")).thenReturn(5);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"));

        verify(gameService).getStreak("player1");
    }

    @Test
    @WithMockUser
    void testScoreCheck_MultiplePlayersWithTiedScores() throws Exception {
        Map<String, Integer> tiedScores = new HashMap<>();
        tiedScores.put("player1", 1000);
        tiedScores.put("player2", 1000);
        tiedScores.put("player3", 800);

        when(gameService.getCurrentScore()).thenReturn(tiedScores);
        when(gameService.getPreviousScores()).thenReturn(previousScores);
        when(gameService.getStreak(anyString())).thenReturn(2);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"))
                .andExpect(model().attributeExists("scoreResults"));
    }

    @Test
    @WithMockUser
    void testScoreCheck_NewPlayerInCurrentRound() throws Exception {
        currentScores.put("player4", 500);

        when(gameService.getCurrentScore()).thenReturn(currentScores);
        when(gameService.getPreviousScores()).thenReturn(previousScores);
        when(gameService.getStreak(anyString())).thenReturn(1);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"));

        verify(gameService).getStreak("player4");
    }

    @Test
    @WithMockUser
    void testScoreCheck_LargeNumberOfPlayers() throws Exception {
        Map<String, Integer> largeScoreMap = new HashMap<>();
        Map<String, Integer> largePrevMap = new HashMap<>();

        for (int i = 1; i <= 50; i++) {
            largeScoreMap.put("player" + i, 1000 - (i * 10));
            largePrevMap.put("player" + i, 800 - (i * 10));
        }

        when(gameService.getCurrentScore()).thenReturn(largeScoreMap);
        when(gameService.getPreviousScores()).thenReturn(largePrevMap);
        when(gameService.getStreak(anyString())).thenReturn(3);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"));
    }

    @Test
    void testScoreCheck_NotAuthenticated() throws Exception {
        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void testScoreCheck_NegativeScores() throws Exception {
        Map<String, Integer> negativeScores = new HashMap<>();
        negativeScores.put("player1", -100);
        negativeScores.put("player2", 500);

        when(gameService.getCurrentScore()).thenReturn(negativeScores);
        when(gameService.getPreviousScores()).thenReturn(new HashMap<>());
        when(gameService.getStreak(anyString())).thenReturn(0);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"));
    }

    @Test
    @WithMockUser
    void testScoreCheck_ZeroStreak() throws Exception {
        when(gameService.getCurrentScore()).thenReturn(currentScores);
        when(gameService.getPreviousScores()).thenReturn(previousScores);
        when(gameService.getStreak(anyString())).thenReturn(0);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"));

        verify(gameService).getStreak("player1");
        verify(gameService).getStreak("player2");
        verify(gameService).getStreak("player3");
    }

    @Test
    @WithMockUser
    void testScoreCheck_HighStreak() throws Exception {
        when(gameService.getCurrentScore()).thenReturn(currentScores);
        when(gameService.getPreviousScores()).thenReturn(previousScores);
        when(gameService.getStreak(anyString())).thenReturn(10);
        when(teamService.isTeamMode(testPin)).thenReturn(false);

        mockMvc.perform(post("/scoreboard")
                        .with(csrf())
                        .param("quizPin", testPin))
                .andExpect(status().isOk())
                .andExpect(view().name("scoreboard"));
    }

    // Note: WebSocket @MessageMapping methods cannot be tested with MockMvc
    // They require WebSocket integration testing which would be implemented separately
    // The scoresFetch() method would need @SpringBootTest with WebSocket client
}
