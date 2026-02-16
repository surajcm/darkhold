package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.practice.service.PracticeService;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.service.PreviewService;
import com.quiz.darkhold.team.model.TeamConfig;
import com.quiz.darkhold.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for PreviewController.
 * Tests challenge preview, game publishing, and practice mode.
 */
class PreviewControllerTest {

    private MockMvc mockMvc;
    private PreviewService previewService;
    private PracticeService practiceService;
    private TeamService teamService;
    private PreviewController previewController;

    private PreviewInfo mockPreviewInfo;
    private PublishInfo mockPublishInfo;
    private final String testChallengeId = "1";
    private final String testPin = "12345";
    private final String moderatorEmail = "moderator@test.com";

    @BeforeEach
    void setUp() {
        // Mock all dependencies
        previewService = mock(PreviewService.class);
        practiceService = mock(PracticeService.class);
        teamService = mock(TeamService.class);

        // Create controller with mocked dependencies
        previewController = new PreviewController(previewService, practiceService, teamService);

        // Build standalone MockMvc with ViewResolver
        mockMvc = MockMvcBuilders.standaloneSetup(previewController)
                .setViewResolvers(viewResolver())
                .build();

        // Setup mock preview info
        mockPreviewInfo = new PreviewInfo();
        mockPreviewInfo.setChallengeId(testChallengeId);
        mockPreviewInfo.setChallengeName("Test Quiz");

        // Setup mock publish info
        mockPublishInfo = new PublishInfo();
        mockPublishInfo.setPin(testPin);
        mockPublishInfo.setModerator(moderatorEmail);
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

    // ==================== POST /preconfigure Tests ====================

    @Test
    void testPreconfigure_Success() throws Exception {
        when(previewService.fetchQuestions(testChallengeId)).thenReturn(mockPreviewInfo);

        mockMvc.perform(post("/preconfigure")
                        .param("challenges", testChallengeId))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/preview"))
                .andExpect(model().attributeExists("previewInfo"))
                .andExpect(model().attribute("previewInfo", mockPreviewInfo));

        verify(previewService).fetchQuestions(testChallengeId);
    }

    @Test
    void testPreconfigure_WithSanitization() throws Exception {
        String maliciousInput = "1<script>alert('xss')</script>";
        when(previewService.fetchQuestions(anyString())).thenReturn(mockPreviewInfo);

        mockMvc.perform(post("/preconfigure")
                        .param("challenges", maliciousInput))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/preview"));
    }

    @Test
    void testPreconfigure_NotAuthenticated() throws Exception {
        mockMvc.perform(post("/preconfigure")
                        .param("challenges", testChallengeId))
                .andExpect(status().isOk());
    }

    // ==================== POST /publish Tests ====================

    @Test
    void testPublish_WithoutTeamMode() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, false))
                .thenReturn(mockPublishInfo);
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/publish").session(session)
                        .principal(createMockPrincipal(moderatorEmail))
                        .param("challenge_id", testChallengeId).param("team_mode", "false"))
                .andExpect(status().isOk()).andExpect(view().name("challenge/publish"))
                .andExpect(model().attributeExists("quizPin", "user"))
                .andExpect(model().attribute("quizPin", testPin))
                .andExpect(model().attribute("user", moderatorEmail))
                .andExpect(model().attribute("teamMode", false))
                .andExpect(request().sessionAttribute("gamePin", testPin))
                .andExpect(request().sessionAttribute("teamMode", false));
    }

    @Test
    void testPublish_WithTeamMode() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/publish").session(session)
                        .principal(createMockPrincipal(moderatorEmail))
                        .param("challenge_id", testChallengeId).param("team_mode", "true")
                        .param("team_count", "3").param("assignment_method", "RANDOM"))
                .andExpect(status().isOk()).andExpect(view().name("challenge/publish"))
                .andExpect(model().attribute("teamMode", true))
                .andExpect(model().attribute("teamCount", 3))
                .andExpect(model().attribute("assignmentMethod", "RANDOM"))
                .andExpect(request().sessionAttribute("teamMode", true));
    }

    @Test
    void testPublish_WithCustomTeamNames() throws Exception {
        String teamNamesJson = "{\"1\":\"Team Red\",\"2\":\"Team Blue\",\"3\":\"Team Green\"}";
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/publish").session(session)
                        .principal(createMockPrincipal(moderatorEmail))
                        .param("challenge_id", testChallengeId).param("team_mode", "true")
                        .param("team_count", "3").param("assignment_method", "MANUAL")
                        .param("team_names", teamNamesJson))
                .andExpect(status().isOk()).andExpect(view().name("challenge/publish"))
                .andExpect(model().attribute("teamMode", true));
        verify(teamService).createTeams(eq(testPin), any(TeamConfig.class));
    }

    @Test
    void testPublish_WithInvalidTeamNamesJson() throws Exception {
        String invalidJson = "{invalid json";
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/publish").session(session)
                        .principal(createMockPrincipal(moderatorEmail))
                        .param("challenge_id", testChallengeId).param("team_mode", "true")
                        .param("team_count", "3").param("assignment_method", "BALANCED")
                        .param("team_names", invalidJson))
                .andExpect(status().isOk()).andExpect(view().name("challenge/publish"));
        verify(teamService).createTeams(eq(testPin), any(TeamConfig.class));
    }

    @Test
    void testPublish_DefaultTeamCount() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .session(session)
                        .principal(createMockPrincipal(moderatorEmail))
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("teamCount", 2))
                .andExpect(model().attribute("assignmentMethod", "BALANCED"));
    }

    @Test
    void testPublish_MaxTeamCount() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .session(session)
                        .principal(createMockPrincipal(moderatorEmail))
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "true")
                        .param("team_count", "6")
                        .param("assignment_method", "RANDOM"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("teamCount", 6));
    }

    @Test
    void testPublish_NotAuthenticated() throws Exception {
        when(previewService.generateQuizPin(anyString(), anyString(), eq(false)))
                .thenReturn(mockPublishInfo);
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .session(session)
                        .principal(createMockPrincipal("test@test.com"))
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "false"))
                .andExpect(status().isOk());
    }

    // ==================== POST /start_practice Tests ====================

    @Test
    void testStartPractice_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(practiceService.initializePracticeGame(eq(testChallengeId), eq("player1"), any()))
                .thenReturn(mockPublishInfo);

        mockMvc.perform(post("/start_practice")
                        .session(session)
                        .principal(createMockPrincipal("player1"))
                        .param("challenge_id", testChallengeId))
                .andExpect(status().isOk())
                .andExpect(view().name("interstitial"))
                .andExpect(model().attributeExists("quizPin"))
                .andExpect(model().attribute("quizPin", testPin));

        verify(practiceService).initializePracticeGame(eq(testChallengeId), eq("player1"), any());
    }

    @Test
    void testStartPractice_WithSanitization() throws Exception {
        String maliciousInput = "1<script>alert('xss')</script>";
        final MockHttpSession session = new MockHttpSession();

        when(practiceService.initializePracticeGame(anyString(), eq("player2"), any()))
                .thenReturn(mockPublishInfo);

        mockMvc.perform(post("/start_practice")
                        .session(session)
                        .principal(createMockPrincipal("player2"))
                        .param("challenge_id", maliciousInput))
                .andExpect(status().isOk())
                .andExpect(view().name("interstitial"));
    }

    @Test
    void testStartPractice_MultipleChallenges() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        when(practiceService.initializePracticeGame(eq("1"), eq("player3"), any()))
                .thenReturn(mockPublishInfo);
        when(practiceService.initializePracticeGame(eq("2"), eq("player3"), any()))
                .thenReturn(createSecondPublishInfo());
        mockMvc.perform(post("/start_practice").session(session)
                        .principal(createMockPrincipal("player3"))
                        .param("challenge_id", "1")).andExpect(status().isOk());
        mockMvc.perform(post("/start_practice").session(session)
                        .principal(createMockPrincipal("player3"))
                        .param("challenge_id", "2")).andExpect(status().isOk())
                .andExpect(model().attribute("quizPin", "67890"));
    }

    private PublishInfo createSecondPublishInfo() {
        PublishInfo info = new PublishInfo();
        info.setPin("67890");
        info.setModerator("player3");
        return info;
    }

    @Test
    void testStartPractice_NotAuthenticated() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(practiceService.initializePracticeGame(eq(testChallengeId), anyString(), any()))
                .thenReturn(mockPublishInfo);

        mockMvc.perform(post("/start_practice")
                        .session(session)
                        .principal(createMockPrincipal("test@test.com"))
                        .param("challenge_id", testChallengeId))
                .andExpect(status().isOk());
    }
}
