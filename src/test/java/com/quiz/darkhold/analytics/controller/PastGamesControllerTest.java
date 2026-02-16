package com.quiz.darkhold.analytics.controller;

import com.quiz.darkhold.analytics.entity.GameResult;
import com.quiz.darkhold.analytics.entity.ParticipantResult;
import com.quiz.darkhold.analytics.entity.QuestionResult;
import com.quiz.darkhold.analytics.service.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
class PastGamesControllerTest {

    private MockMvc mockMvc;
    private ResultService resultService;
    private PastGamesController controller;
    private GameResult mockGameResult;
    private List<GameResult> mockPastGames;
    private final String moderatorEmail = "moderator@test.com";

    @BeforeEach
    void setUp() {
        resultService = mock(ResultService.class);
        controller = new PastGamesController(resultService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver())
                .build();
        mockGameResult = createMockGameResult();
        mockPastGames = new ArrayList<>(List.of(mockGameResult));
    }

    private ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    private Principal createMockPrincipal(final String name) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(name);
        return principal;
    }

    private GameResult createMockGameResult() {
        GameResult result = new GameResult();
        result.setId(1L);
        result.setPin("12345");
        result.setChallengeName("Test Quiz");
        result.setModerator(moderatorEmail);
        result.setGameMode("MULTIPLAYER");
        result.setCompletedAt(LocalDateTime.now(ZoneId.systemDefault()));
        result.setDurationMinutes(15);
        result.setTotalQuestions(10);
        result.setParticipantCount(5);
        result.setParticipantResults(List.of(createMockParticipantResult()));
        result.setQuestionResults(new ArrayList<>(List.of(createMockQuestionResult())));
        return result;
    }

    private ParticipantResult createMockParticipantResult() {
        ParticipantResult pr = new ParticipantResult();
        pr.setFinalRank(1);
        pr.setUsername("player1");
        pr.setFinalScore(8500);
        pr.setCorrectAnswers(9);
        pr.setIncorrectAnswers(1);
        pr.setMaxStreak(5);
        return pr;
    }

    private QuestionResult createMockQuestionResult() {
        QuestionResult qr = new QuestionResult();
        qr.setQuestionNumber(1);
        qr.setQuestionText("What is 2+2?");
        qr.setQuestionType("MULTIPLE_CHOICE");
        qr.setCorrectCount(4);
        qr.setIncorrectCount(1);
        qr.setTimeoutCount(0);
        return qr;
    }

    // ==================== /past-games Endpoint Tests ====================

    @Test
    void testShowPastGames_Success() throws Exception {
        when(resultService.getGameResultsByModerator(anyString())).thenReturn(mockPastGames);

        mockMvc.perform(get("/past-games")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().isOk())
                .andExpect(view().name("pastgames"))
                .andExpect(model().attributeExists("pastGames"))
                .andExpect(model().attribute("pastGames", hasSize(1)))
                .andExpect(model().attributeExists("moderator"))
                .andExpect(model().attribute("moderator", moderatorEmail));

        verify(resultService).getGameResultsByModerator(moderatorEmail);
    }

    @Test
    void testShowPastGames_EmptyList() throws Exception {
        when(resultService.getGameResultsByModerator(anyString())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/past-games")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().isOk())
                .andExpect(view().name("pastgames"))
                .andExpect(model().attributeExists("pastGames"))
                .andExpect(model().attribute("pastGames", hasSize(0)));
    }

    @Test
    void testShowPastGames_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/past-games"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // ==================== /game-result/{id} Endpoint Tests ====================

    @Test
    void testShowGameResult_Success() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().isOk())
                .andExpect(view().name("gameresult"))
                .andExpect(model().attributeExists("gameResult"))
                .andExpect(model().attribute("gameResult", mockGameResult));

        verify(resultService).getGameResultById(1L);
    }

    @Test
    void testShowGameResult_NotFound() throws Exception {
        when(resultService.getGameResultById(999L)).thenReturn(null);

        mockMvc.perform(get("/game-result/999")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/past-games"));
    }

    @Test
    void testShowGameResult_UnauthorizedUser() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1")
                        .principal(createMockPrincipal("other@test.com")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/past-games"));
    }

    @Test
    void testShowGameResult_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/game-result/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // ==================== /game-result/{id}/export-csv Endpoint Tests ====================

    @Test
    void testExportGameResultCsv_Success() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);
        mockMvc.perform(get("/game-result/1/export-csv")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"game-result-12345.csv\""))
                .andExpect(content().string(containsString("Game Results Export")))
                .andExpect(content().string(containsString("PIN,12345")))
                .andExpect(content().string(containsString("Challenge,Test Quiz")))
                .andExpect(content().string(containsString("player1")))
                .andExpect(content().string(containsString("What is 2+2?")))
                .andDo(result -> verify(resultService).getGameResultById(1L));
    }

    @Test
    void testExportGameResultCsv_GameNotFound() throws Exception {
        when(resultService.getGameResultById(999L)).thenReturn(null);

        mockMvc.perform(get("/game-result/999/export-csv")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testExportGameResultCsv_UnauthorizedUser() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1/export-csv")
                        .principal(createMockPrincipal("other@test.com")))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testExportGameResultCsv_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/game-result/1/export-csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testExportGameResultCsv_CsvFormatting() throws Exception {
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        String participantHeader = "Rank,Username,Score,Correct,Incorrect,Max Streak,Accuracy %";
        String questionHeader = "Number,Question,Type,Correct,Incorrect,Timeout,"
                + "Success Rate %,Difficulty";

        mockMvc.perform(get("/game-result/1/export-csv")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(participantHeader)))
                .andExpect(content().string(containsString("1,player1,8500,9,1,5,90.0")))
                .andExpect(content().string(containsString(questionHeader)))
                .andExpect(content().string(containsString(
                        "1,\"What is 2+2?\",MULTIPLE_CHOICE,4,1,0,80.0,EASY")));
    }

    @Test
    void testExportGameResultCsv_QuestionWithQuotes() throws Exception {
        QuestionResult qrWithQuotes = new QuestionResult();
        qrWithQuotes.setQuestionNumber(2);
        qrWithQuotes.setQuestionText("What is \"the answer\"?");
        qrWithQuotes.setQuestionType("TRUE_FALSE");
        qrWithQuotes.setCorrectCount(3);
        qrWithQuotes.setIncorrectCount(2);
        qrWithQuotes.setTimeoutCount(0);

        mockGameResult.getQuestionResults().add(qrWithQuotes);
        when(resultService.getGameResultById(1L)).thenReturn(mockGameResult);

        mockMvc.perform(get("/game-result/1/export-csv")
                        .principal(createMockPrincipal(moderatorEmail)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("What is \"\"the answer\"\"?")));
    }
}
