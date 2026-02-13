package com.quiz.darkhold.home.controller;

import com.quiz.darkhold.home.model.GameInfo;
import com.quiz.darkhold.home.service.HomeService;
import com.quiz.darkhold.init.RateLimitingService;
import com.quiz.darkhold.team.model.TeamAssignmentMethod;
import com.quiz.darkhold.team.service.TeamService;
import com.quiz.darkhold.user.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for HomeController.
 * Tests home page, game PIN validation, and player join flow.
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private RateLimitingService rateLimitingService;

    @MockBean
    private TeamService teamService;

    private final String testPin = "12345";
    private final String testUsername = "player1";
    private final String moderatorEmail = "moderator@test.com";

    @BeforeEach
    void setUp() {
        // Default rate limiting setup
        when(rateLimitingService.isAllowed(anyString())).thenReturn(true);
    }

    // ==================== GET / Tests ====================

    @Test
    void testHome_Success() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("gameinfo"));
    }

    // ==================== POST /home Tests ====================

    @Test
    @WithMockUser
    void testToHome_Success() throws Exception {
        mockMvc.perform(post("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("gameinfo"));
    }

    // ==================== POST /enterGame Tests ====================

    @Test
    @WithMockUser
    void testEnterGame_ValidPin() throws Exception {
        when(homeService.validateGamePin(testPin)).thenReturn(true);
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                        .with(csrf())
                        .param("gamePin", testPin)
                        .header("X-Forwarded-For", "")
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(homeService).validateGamePin(testPin);
        verify(rateLimitingService).recordSuccessfulAttempt("127.0.0.1");
    }

    @Test
    @WithMockUser
    void testEnterGame_InvalidPin() throws Exception {
        when(homeService.validateGamePin("99999")).thenReturn(false);
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                        .with(csrf())
                        .param("gamePin", "99999")
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(rateLimitingService).recordFailedAttempt("127.0.0.1");
    }

    @Test
    @WithMockUser
    void testEnterGame_RateLimitExceeded() throws Exception {
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(false);

        mockMvc.perform(post("/enterGame")
                        .with(csrf())
                        .param("gamePin", testPin)
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser
    void testEnterGame_WithXForwardedFor() throws Exception {
        when(homeService.validateGamePin(testPin)).thenReturn(true);
        when(rateLimitingService.isAllowed("192.168.1.100")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                        .with(csrf())
                        .param("gamePin", testPin)
                        .header("X-Forwarded-For", "192.168.1.100, 10.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(rateLimitingService).recordSuccessfulAttempt("192.168.1.100");
    }

    @Test
    @WithMockUser
    void testEnterGame_WithXRealIP() throws Exception {
        when(homeService.validateGamePin(testPin)).thenReturn(true);
        when(rateLimitingService.isAllowed("192.168.1.200")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                        .with(csrf())
                        .param("gamePin", testPin)
                        .header("X-Real-IP", "192.168.1.200"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(rateLimitingService).recordSuccessfulAttempt("192.168.1.200");
    }

    @Test
    @WithMockUser
    void testEnterGame_SanitizedPin() throws Exception {
        when(homeService.validateGamePin("12345")).thenReturn(true);
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                        .with(csrf())
                        .param("gamePin", "12345<script>alert('xss')</script>")
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk());
    }

    // ==================== POST /joinGame Tests ====================

    @Test
    @WithMockUser
    void testJoinGame_Success() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        activeUsers.add("existingPlayer");

        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(false);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .session(session)
                        .param("gamePin", testPin)
                        .param("name", testUsername))
                .andExpect(status().isOk())
                .andExpect(view().name("game/gamewait"))
                .andExpect(model().attributeExists("gameinfo"))
                .andExpect(model().attributeExists("teamMode"))
                .andExpect(model().attribute("teamMode", false))
                .andExpect(request().sessionAttribute("gamePin", testPin));

        verify(securityService).autoLogin(testUsername, "UNREGISTERED_USER");
        verify(homeService).participantsInActiveQuiz(testPin);
        verify(homeService).getModerator(testPin);
    }

    @Test
    @WithMockUser
    void testJoinGame_WithTeamMode() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(true);
        when(teamService.getAssignmentMethod(testPin)).thenReturn(TeamAssignmentMethod.RANDOM);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .session(session)
                        .param("gamePin", testPin)
                        .param("name", testUsername))
                .andExpect(status().isOk())
                .andExpect(view().name("game/gamewait"))
                .andExpect(model().attribute("teamMode", true))
                .andExpect(model().attributeExists("assignmentMethod"))
                .andExpect(model().attribute("assignmentMethod", "RANDOM"));

        verify(teamService).getAssignmentMethod(testPin);
    }

    @Test
    @WithMockUser
    void testJoinGame_ModeratorJoiningOwnGame() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(testUsername);
        when(homeService.isTeamMode(testPin)).thenReturn(false);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .session(session)
                        .param("gamePin", testPin)
                        .param("name", testUsername))
                .andExpect(status().isOk())
                .andExpect(view().name("game/gamewait"))
                .andExpect(model().attributeExists("moderatorWarning"))
                .andExpect(model().attribute("moderatorWarning", true));
    }

    @Test
    @WithMockUser
    void testJoinGame_ValidationError_EmptyName() throws Exception {
        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .param("gamePin", testPin)
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("gameInfo", "name"));
    }

    @Test
    @WithMockUser
    void testJoinGame_ValidationError_EmptyPin() throws Exception {
        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .param("gamePin", "")
                        .param("name", testUsername))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("gameInfo", "gamePin"));
    }

    @Test
    @WithMockUser
    void testJoinGame_ValidationError_NameTooLong() throws Exception {
        String longName = "a".repeat(101); // Assuming max length is 100

        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .param("gamePin", testPin)
                        .param("name", longName))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser
    void testJoinGame_MultipleActivePlayers() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        activeUsers.add("player1");
        activeUsers.add("player2");
        activeUsers.add("player3");

        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(false);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .session(session)
                        .param("gamePin", testPin)
                        .param("name", "player4"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/gamewait"));
    }

    @Test
    @WithMockUser
    void testJoinGame_SessionPinStorage() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(false);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/joinGame")
                        .with(csrf())
                        .session(session)
                        .param("gamePin", testPin)
                        .param("name", testUsername))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("gamePin", testPin));

        // Verify PIN is stored in session
        assert session.getAttribute("gamePin").equals(testPin);
    }
}
