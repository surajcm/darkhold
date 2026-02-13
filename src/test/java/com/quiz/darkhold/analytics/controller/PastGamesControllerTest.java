package com.quiz.darkhold.analytics.controller;

import com.quiz.darkhold.analytics.entity.GameResult;
import com.quiz.darkhold.analytics.entity.ParticipantResult;
import com.quiz.darkhold.analytics.entity.QuestionResult;
import com.quiz.darkhold.analytics.service.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for PastGamesController.
 * Tests all endpoints for viewing past games and exporting results.
 */
@WebMvcTest(PastGamesController.class)
class PastGamesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultService resultService;

    private GameResult mockGameResult;
    private List<GameResult> mockPastGames;

    @BeforeEach
    void setUp() {
        // Setup mock game result
        mockGameResult = new GameResult();
        mockGameResult.setId(1L);
        mockGameResult.setPin("12345");
        mockGameResult.setChallengeName("Test Quiz");
        mockGameResult.setModerator("moderator@test.com");
        mockGameResult.setGameMode("MULTIPLAYER");
        mockGameResult.setCompletedAt(LocalDateTime.now());
        mockGameResult.setDurationMinutes(15);
        mockGameResult.setTotalQuestions(10);
        mockGameResult.setParticipantCount(5);

        // Add participant results
        List<ParticipantResult> participantResults = new ArrayList<>();
        ParticipantResult pr1 = new ParticipantResult();
        pr1.setFinalRank(1);
        pr1.setUsername("player1");
        pr1.setFinalScore(8500);
        pr1.setCorrectAnswers(9);
        pr1.setIncorrectAnswers(1);
        pr1.setMaxStreak(5);
        pr1.setAccuracyPercentage(90.0);
        participantResults.add(pr1);
        mockGameResult.setParticipantResults(participantResults);

        // Add question results
        List<QuestionResult> questionResults = new ArrayList<>();
        QuestionResult qr1 = new QuestionResult();
        qr1.setQuestionNumber(1);
        qr1.setQuestionText("What is 2+2?");
        qr1.setQuestionType("MULTIPLE_CHOICE");
        qr1.setCorrectCount(4);
        qr1.setIncorrectCount(1);
        qr1.setTimeoutCount(0);
        qr1.setSuccessRatePercentage(80.0);
        qr1.setDifficultyLevel("EASY");
        questionResults.add(qr1);
        mockGameResult.setQuestionResults(questionResults);

        // Setup mock past games list
        mockPastGames = new ArrayList<>();
        mockPastGames.add(mockGameResult);
    }

    // ==================== /past-games Endpoint Tests ====================

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testShowPastGames_Success() throws Exception {
        when(resultService.getGameResultsByModerator(anyString())).thenReturn(mockPastGames);

        mockMvc.perform(get("/past-games"))
                .andExpect(status().isOk())
                .andExpect(view().name("pastgames"))
                .andExpect(model().attributeExists("pastGames"))
                .andExpect(model().attribute("pastGames", hasSize(1)))
                .andExpect(model().attributeExists("moderator"))
                .andExpect(model().attribute("moderator", "moderator@test.com"));

        verify(resultService).getGameResultsByModerator("moderator@test.com");
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testShowPastGames_EmptyList() throws Exception {
        when(resultService.getGameResultsByModerator(anyString())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/past-games"))
                .andExpect(status().isOk())
                .andExpect(view().name("pastgames"))
                .andExpect(model().attributeExists("pastGames"))
                .andExpect(model().attribute("pastGames", hasSize(0)));
    }

    @Test
    void testShowPastGames_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/past-games"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    // ==================== /game-result/{id} Endpoint Tests ====================

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testShowGameResult_Success() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("gameresult"))
                .andExpect(model().attributeExists("gameResult"))
                .andExpect(model().attribute("gameResult", mockGameResult));

        verify(resultService).getGameResultById(1L);
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testShowGameResult_NotFound() throws Exception {
        when(resultService.getGameResultById(999L)).thenReturn(null);

        mockMvc.perform(get("/game-result/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/past-games"));
    }

    @Test
    @WithMockUser(username = "other@test.com")
    void testShowGameResult_UnauthorizedUser() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/past-games"));
    }

    @Test
    void testShowGameResult_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/game-result/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    // ==================== /game-result/{id}/export-csv Endpoint Tests ====================

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testExportGameResultCsv_Success() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1/export-csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"game-result-12345.csv\""))
                .andExpect(content().string(containsString("Game Results Export")))
                .andExpect(content().string(containsString("PIN,12345")))
                .andExpect(content().string(containsString("Challenge,Test Quiz")))
                .andExpect(content().string(containsString("Participant Results")))
                .andExpect(content().string(containsString("player1")))
                .andExpect(content().string(containsString("Question Results")))
                .andExpect(content().string(containsString("What is 2+2?")));

        verify(resultService).getGameResultById(1L);
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testExportGameResultCsv_GameNotFound() throws Exception {
        when(resultService.getGameResultById(999L)).thenReturn(null);

        mockMvc.perform(get("/game-result/999/export-csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(username = "other@test.com")
    void testExportGameResultCsv_UnauthorizedUser() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1/export-csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testExportGameResultCsv_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/game-result/1/export-csv"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testExportGameResultCsv_CsvFormatting() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1/export-csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Rank,Username,Score,Correct,Incorrect,Max Streak,Accuracy %")))
                .andExpect(content().string(containsString("1,player1,8500,9,1,5,90.0")))
                .andExpect(content().string(containsString("Number,Question,Type,Correct,Incorrect,Timeout,Success Rate %,Difficulty")))
                .andExpect(content().string(containsString("1,\"What is 2+2?\",MULTIPLE_CHOICE,4,1,0,80.0,EASY")));
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testExportGameResultCsv_QuestionWithQuotes() throws Exception {
        QuestionResult qrWithQuotes = new QuestionResult();
        qrWithQuotes.setQuestionNumber(2);
        qrWithQuotes.setQuestionText("What is \"the answer\"?");
        qrWithQuotes.setQuestionType("TRUE_FALSE");
        qrWithQuotes.setCorrectCount(3);
        qrWithQuotes.setIncorrectCount(2);
        qrWithQuotes.setTimeoutCount(0);
        qrWithQuotes.setSuccessRatePercentage(60.0);
        qrWithQuotes.setDifficultyLevel("MEDIUM");

        mockGameResult.getQuestionResults().add(qrWithQuotes);
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1/export-csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("What is \"\"the answer\"\"?")));
    }
}
