package com.quiz.darkhold.home.controller;

import com.quiz.darkhold.home.service.HomeService;
import com.quiz.darkhold.init.RateLimitingService;
import com.quiz.darkhold.team.model.TeamAssignmentMethod;
import com.quiz.darkhold.team.service.TeamService;
import com.quiz.darkhold.user.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for HomeController.
 * Tests home page, game PIN validation, and player join flow.
 */
class HomeControllerTest {

    private MockMvc mockMvc;
    private HomeService homeService;
    private SecurityService securityService;
    private RateLimitingService rateLimitingService;
    private TeamService teamService;
    private HomeController homeController;

    private final String testPin = "12345";
    private final String testUsername = "player1";
    private final String moderatorEmail = "moderator@test.com";

    @BeforeEach
    void setUp() {
        // Mock all dependencies
        homeService = mock(HomeService.class);
        securityService = mock(SecurityService.class);
        rateLimitingService = mock(RateLimitingService.class);
        teamService = mock(TeamService.class);

        // Create controller with mocked dependencies
        homeController = new HomeController(homeService, securityService,
                rateLimitingService, teamService);

        // Build standalone MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();

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
    void testToHome_Success() throws Exception {
        mockMvc.perform(post("/home")
                        )
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("gameinfo"));
    }

    // ==================== POST /enterGame Tests ====================

    @Test
    void testEnterGame_ValidPin() throws Exception {
        when(homeService.validateGamePin(testPin)).thenReturn(true);
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(true);
        when(rateLimitingService.isBlocked("127.0.0.1")).thenReturn(false);

        mockMvc.perform(post("/enterGame")
                                                .param("gamePin", testPin)
                        .header("X-Forwarded-For", "")
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"));

        verify(homeService).validateGamePin(testPin);
        verify(rateLimitingService).recordSuccessfulAttempt("127.0.0.1");
    }

    @Test
    void testEnterGame_InvalidPin() throws Exception {
        when(homeService.validateGamePin("99999")).thenReturn(false);
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(true);
        when(rateLimitingService.isBlocked("127.0.0.1")).thenReturn(false);
        when(rateLimitingService.getRemainingAttempts("127.0.0.1")).thenReturn(3);

        mockMvc.perform(post("/enterGame")
                                                .param("gamePin", "99999")
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.status").value("INVALID"))
                .andExpect(jsonPath("$.remainingAttempts").value(3));

        verify(rateLimitingService).recordFailedAttempt("127.0.0.1");
    }

    @Test
    void testEnterGame_RateLimitExceeded() throws Exception {
        when(rateLimitingService.isBlocked("127.0.0.1")).thenReturn(false);
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(false);
        when(rateLimitingService.getRemainingAttempts("127.0.0.1")).thenReturn(0);

        mockMvc.perform(post("/enterGame")
                                                .param("gamePin", testPin)
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.status").value("RATE_LIMITED"))
                .andExpect(jsonPath("$.remainingAttempts").value(0));

        verify(homeService, never()).validateGamePin(anyString());
    }

    @Test
    void testEnterGame_Blocked() throws Exception {
        when(rateLimitingService.isBlocked("127.0.0.1")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                                                .param("gamePin", testPin)
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.remainingAttempts").value(0));

        verify(homeService, never()).validateGamePin(anyString());
        verify(rateLimitingService, never()).recordFailedAttempt(anyString());
    }

    @Test
    void testEnterGame_WithXForwardedFor() throws Exception {
        when(homeService.validateGamePin(testPin)).thenReturn(true);
        when(rateLimitingService.isBlocked("192.168.1.100")).thenReturn(false);
        when(rateLimitingService.isAllowed("192.168.1.100")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                                                .param("gamePin", testPin)
                        .header("X-Forwarded-For", "192.168.1.100, 10.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"));

        verify(rateLimitingService).recordSuccessfulAttempt("192.168.1.100");
    }

    @Test
    void testEnterGame_WithXRealIP() throws Exception {
        when(homeService.validateGamePin(testPin)).thenReturn(true);
        when(rateLimitingService.isBlocked("192.168.1.200")).thenReturn(false);
        when(rateLimitingService.isAllowed("192.168.1.200")).thenReturn(true);

        mockMvc.perform(post("/enterGame")
                                                .param("gamePin", testPin)
                        .header("X-Real-IP", "192.168.1.200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"));

        verify(rateLimitingService).recordSuccessfulAttempt("192.168.1.200");
    }

    @Test
    void testEnterGame_SanitizedPin() throws Exception {
        // Sanitization only removes newlines/tabs, HTML tags pass through but validation should fail
        // because PIN should be numeric
        String unsanitizedPin = "12345<script>alert('xss')</script>";
        when(homeService.validateGamePin(unsanitizedPin)).thenReturn(false);
        when(rateLimitingService.isBlocked("127.0.0.1")).thenReturn(false);
        when(rateLimitingService.isAllowed("127.0.0.1")).thenReturn(true);
        when(rateLimitingService.getRemainingAttempts("127.0.0.1")).thenReturn(4);

        mockMvc.perform(post("/enterGame")
                                                .param("gamePin", unsanitizedPin)
                        .remoteAddress("127.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.status").value("INVALID"))
                .andExpect(jsonPath("$.remainingAttempts").value(4));

        verify(rateLimitingService).recordFailedAttempt("127.0.0.1");
    }

    // ==================== POST /joinGame Tests ====================

    @Test
    void testJoinGame_Success() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        activeUsers.add("existingPlayer");
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(false);
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/joinGame").session(session)
                        .param("gamePin", testPin).param("name", testUsername))
                .andExpect(status().isOk()).andExpect(view().name("game/gamewait"))
                .andExpect(model().attributeExists("gameinfo", "teamMode"))
                .andExpect(model().attribute("teamMode", false))
                .andExpect(request().sessionAttribute("gamePin", testPin));
        verify(securityService).autoLogin(testUsername, "UNREGISTERED_USER");
    }

    @Test
    void testJoinGame_WithTeamMode() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(true);
        when(teamService.getAssignmentMethod(testPin)).thenReturn(TeamAssignmentMethod.RANDOM);
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/joinGame").session(session)
                        .param("gamePin", testPin).param("name", testUsername))
                .andExpect(status().isOk()).andExpect(view().name("game/gamewait"))
                .andExpect(model().attribute("teamMode", true))
                .andExpect(model().attributeExists("assignmentMethod"))
                .andExpect(model().attribute("assignmentMethod", "RANDOM"));
    }

    @Test
    void testJoinGame_ModeratorJoiningOwnGame() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(testUsername);
        when(homeService.isTeamMode(testPin)).thenReturn(false);
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/joinGame").session(session)
                        .param("gamePin", testPin).param("name", testUsername))
                .andExpect(status().isOk()).andExpect(view().name("game/gamewait"))
                .andExpect(model().attributeExists("moderatorWarning"))
                .andExpect(model().attribute("moderatorWarning", true));
    }

    @Test
    void testJoinGame_ValidationError_EmptyName() throws Exception {
        mockMvc.perform(post("/joinGame")
                                                .param("gamePin", testPin)
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("gameInfo", "name"));
    }

    @Test
    void testJoinGame_ValidationError_EmptyPin() throws Exception {
        mockMvc.perform(post("/joinGame")
                                                .param("gamePin", "")
                        .param("name", testUsername))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("gameInfo", "gamePin"));
    }

    @Test
    void testJoinGame_ValidationError_NameTooLong() throws Exception {
        String longName = "a".repeat(101); // Assuming max length is 100

        mockMvc.perform(post("/joinGame")
                                                .param("gamePin", testPin)
                        .param("name", longName))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testJoinGame_MultipleActivePlayers() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        activeUsers.add("player1");
        activeUsers.add("player2");
        activeUsers.add("player3");
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(false);
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/joinGame").session(session)
                        .param("gamePin", testPin).param("name", "player4"))
                .andExpect(status().isOk()).andExpect(view().name("game/gamewait"));
    }

    @Test
    void testJoinGame_SessionPinStorage() throws Exception {
        List<String> activeUsers = new ArrayList<>();
        when(homeService.participantsInActiveQuiz(testPin)).thenReturn(activeUsers);
        when(homeService.getModerator(testPin)).thenReturn(moderatorEmail);
        when(homeService.isTeamMode(testPin)).thenReturn(false);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/joinGame")
                                                .session(session)
                        .param("gamePin", testPin)
                        .param("name", testUsername))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("gamePin", testPin));

        // Verify PIN is stored in session
        assert session.getAttribute("gamePin").equals(testPin);
    }
}
