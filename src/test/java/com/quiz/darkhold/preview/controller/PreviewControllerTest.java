package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.practice.service.PracticeService;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.service.PreviewService;
import com.quiz.darkhold.team.model.TeamConfig;
import com.quiz.darkhold.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for PreviewController.
 * Tests challenge preview, game publishing, and practice mode.
 */
@WebMvcTest(PreviewController.class)
class PreviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PreviewService previewService;

    @MockBean
    private PracticeService practiceService;

    @MockBean
    private TeamService teamService;

    private PreviewInfo mockPreviewInfo;
    private PublishInfo mockPublishInfo;
    private final String testChallengeId = "1";
    private final String testPin = "12345";
    private final String moderatorEmail = "moderator@test.com";

    @BeforeEach
    void setUp() {
        // Setup mock preview info
        mockPreviewInfo = new PreviewInfo();
        mockPreviewInfo.setChallengeId(Long.parseLong(testChallengeId));
        mockPreviewInfo.setChallengeName("Test Quiz");
        mockPreviewInfo.setQuestionCount(10);

        // Setup mock publish info
        mockPublishInfo = new PublishInfo();
        mockPublishInfo.setPin(testPin);
        mockPublishInfo.setModerator(moderatorEmail);
    }

    // ==================== POST /preconfigure Tests ====================

    @Test
    @WithMockUser
    void testPreconfigure_Success() throws Exception {
        when(previewService.fetchQuestions(testChallengeId)).thenReturn(mockPreviewInfo);

        mockMvc.perform(post("/preconfigure")
                        .with(csrf())
                        .param("challenges", testChallengeId))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/preview"))
                .andExpect(model().attributeExists("previewInfo"))
                .andExpect(model().attribute("previewInfo", mockPreviewInfo));

        verify(previewService).fetchQuestions(testChallengeId);
    }

    @Test
    @WithMockUser
    void testPreconfigure_WithSanitization() throws Exception {
        String maliciousInput = "1<script>alert('xss')</script>";
        when(previewService.fetchQuestions(anyString())).thenReturn(mockPreviewInfo);

        mockMvc.perform(post("/preconfigure")
                        .with(csrf())
                        .param("challenges", maliciousInput))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/preview"));
    }

    @Test
    void testPreconfigure_NotAuthenticated() throws Exception {
        mockMvc.perform(post("/preconfigure")
                        .with(csrf())
                        .param("challenges", testChallengeId))
                .andExpect(status().is3xxRedirection());
    }

    // ==================== POST /publish Tests ====================

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testPublish_WithoutTeamMode() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, false))
                .thenReturn(mockPublishInfo);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/publish"))
                .andExpect(model().attributeExists("quizPin"))
                .andExpect(model().attribute("quizPin", testPin))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", moderatorEmail))
                .andExpect(model().attribute("teamMode", false))
                .andExpect(request().sessionAttribute("gamePin", testPin))
                .andExpect(request().sessionAttribute("teamMode", false));

        verify(previewService).generateQuizPin(testChallengeId, moderatorEmail, false);
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testPublish_WithTeamMode() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "true")
                        .param("team_count", "3")
                        .param("assignment_method", "RANDOM"))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/publish"))
                .andExpect(model().attribute("teamMode", true))
                .andExpect(model().attribute("teamCount", 3))
                .andExpect(model().attribute("assignmentMethod", "RANDOM"))
                .andExpect(request().sessionAttribute("teamMode", true));

        verify(previewService).generateQuizPin(testChallengeId, moderatorEmail, true);
        verify(teamService).createTeams(eq(testPin), any(TeamConfig.class));
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testPublish_WithCustomTeamNames() throws Exception {
        String teamNamesJson = "{\"1\":\"Team Red\",\"2\":\"Team Blue\",\"3\":\"Team Green\"}";

        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "true")
                        .param("team_count", "3")
                        .param("assignment_method", "MANUAL")
                        .param("team_names", teamNamesJson))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/publish"))
                .andExpect(model().attribute("teamMode", true));

        verify(teamService).createTeams(eq(testPin), any(TeamConfig.class));
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testPublish_WithInvalidTeamNamesJson() throws Exception {
        String invalidJson = "{invalid json";

        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "true")
                        .param("team_count", "3")
                        .param("assignment_method", "BALANCED")
                        .param("team_names", invalidJson))
                .andExpect(status().isOk())
                .andExpect(view().name("challenge/publish"));

        // Should still create teams with empty names map
        verify(teamService).createTeams(eq(testPin), any(TeamConfig.class));
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testPublish_DefaultTeamCount() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("teamCount", 2))
                .andExpect(model().attribute("assignmentMethod", "BALANCED"));
    }

    @Test
    @WithMockUser(username = "moderator@test.com")
    void testPublish_MaxTeamCount() throws Exception {
        when(previewService.generateQuizPin(testChallengeId, moderatorEmail, true))
                .thenReturn(mockPublishInfo);
        doNothing().when(teamService).createTeams(eq(testPin), any(TeamConfig.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/publish")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId)
                        .param("team_mode", "true")
                        .param("team_count", "6")
                        .param("assignment_method", "RANDOM"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("teamCount", 6));
    }

    @Test
    void testPublish_NotAuthenticated() throws Exception {
        mockMvc.perform(post("/publish")
                        .with(csrf())
                        .param("challenge_id", testChallengeId))
                .andExpect(status().is3xxRedirection());
    }

    // ==================== POST /start_practice Tests ====================

    @Test
    @WithMockUser(username = "player1")
    void testStartPractice_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(practiceService.initializePracticeGame(eq(testChallengeId), eq("player1"), any()))
                .thenReturn(mockPublishInfo);

        mockMvc.perform(post("/start_practice")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId))
                .andExpect(status().isOk())
                .andExpect(view().name("interstitial"))
                .andExpect(model().attributeExists("quizPin"))
                .andExpect(model().attribute("quizPin", testPin));

        verify(practiceService).initializePracticeGame(eq(testChallengeId), eq("player1"), any());
    }

    @Test
    @WithMockUser(username = "player2")
    void testStartPractice_WithSanitization() throws Exception {
        String maliciousInput = "1<script>alert('xss')</script>";
        MockHttpSession session = new MockHttpSession();

        when(practiceService.initializePracticeGame(anyString(), eq("player2"), any()))
                .thenReturn(mockPublishInfo);

        mockMvc.perform(post("/start_practice")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", maliciousInput))
                .andExpect(status().isOk())
                .andExpect(view().name("interstitial"));
    }

    @Test
    @WithMockUser(username = "player3")
    void testStartPractice_MultipleChallenges() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // First practice game
        when(practiceService.initializePracticeGame(eq("1"), eq("player3"), any()))
                .thenReturn(mockPublishInfo);

        mockMvc.perform(post("/start_practice")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", "1"))
                .andExpect(status().isOk());

        // Second practice game
        PublishInfo anotherPublishInfo = new PublishInfo();
        anotherPublishInfo.setPin("67890");
        anotherPublishInfo.setModerator("player3");

        when(practiceService.initializePracticeGame(eq("2"), eq("player3"), any()))
                .thenReturn(anotherPublishInfo);

        mockMvc.perform(post("/start_practice")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("quizPin", "67890"));
    }

    @Test
    void testStartPractice_NotAuthenticated() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/start_practice")
                        .with(csrf())
                        .session(session)
                        .param("challenge_id", testChallengeId))
                .andExpect(status().is3xxRedirection());
    }
}
