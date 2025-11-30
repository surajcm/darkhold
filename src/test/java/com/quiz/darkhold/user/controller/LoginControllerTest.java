package com.quiz.darkhold.user.controller;

import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.service.SecurityService;
import com.quiz.darkhold.util.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DisplayName("LoginController Tests")
class LoginControllerTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController(securityService);
    }

    @Nested
    @DisplayName("login() method tests")
    class LoginMethodTests {

        @Test
        @DisplayName("should redirect to home when user is already authenticated")
        void testLogin_WhenAuthenticated_ShouldRedirectToHome() {
            // Given
            when(securityService.isAuthenticated()).thenReturn(true);

            // When
            var result = loginController.login(model, null, null);

            // Then
            assertEquals("redirect:/", result);
            verify(model, never()).addAttribute(anyString(), any());
        }

        @Test
        @DisplayName("should display error message when error parameter is present")
        void testLogin_WhenErrorPresent_ShouldDisplayErrorMessage() {
            // Given
            when(securityService.isAuthenticated()).thenReturn(false);
            var error = "error";

            // When
            var result = loginController.login(model, error, null);

            // Then
            assertEquals("login", result);
            verify(model).addAttribute("danger", "Your username or password is invalid.");
        }

        @Test
        @DisplayName("should display logout message when logout parameter is present")
        void testLogin_WhenLogoutPresent_ShouldDisplayLogoutMessage() {
            // Given
            when(securityService.isAuthenticated()).thenReturn(false);
            var logout = "true";

            // When
            var result = loginController.login(model, null, logout);

            // Then
            assertEquals("login", result);
            verify(model).addAttribute("message", "You have been logged out successfully.");
        }

        @Test
        @DisplayName("should not add any attributes when no error or logout")
        void testLogin_WhenNoErrorOrLogout_ShouldReturnLoginView() {
            // Given
            when(securityService.isAuthenticated()).thenReturn(false);

            // When
            var result = loginController.login(model, null, null);

            // Then
            assertEquals("login", result);
            verify(model, never()).addAttribute(anyString(), any());
        }

        @Test
        @DisplayName("should handle both error and logout when both are present")
        void testLogin_WhenBothErrorAndLogout_ShouldPrioritizeLogout() {
            // Given
            when(securityService.isAuthenticated()).thenReturn(false);
            var error = "error";
            var logout = "true";

            // When
            var result = loginController.login(model, error, logout);

            // Then
            assertEquals("login", result);
            // When both are present, logout takes priority in modelAttributes logic
            verify(model).addAttribute("message", "You have been logged out successfully.");
        }
    }

    @Nested
    @DisplayName("loginGet() method tests")
    class LoginGetMethodTests {

        @Test
        @DisplayName("should perform auto-login and redirect to options page on successful login")
        void testLoginGet_WhenNoErrorOrLogout_ShouldAutoLoginAndRedirect() {
            // Given
            var userName = "testuser";
            var password = "testpass";

            // When
            var result = loginController.loginGet(model, userName, password, null, null);

            // Then
            assertEquals("options/options", result);
            verify(securityService).autoLogin(userName, password);
            verify(model).addAttribute(eq("userForm"), any(User.class));
        }

        @Test
        @DisplayName("should add error attribute when error parameter is present")
        void testLoginGet_WhenErrorPresent_ShouldAddErrorAttribute() {
            // Given
            var userName = "testuser";
            var password = "testpass";
            var error = "error";

            // When
            var result = loginController.loginGet(model, userName, password, error, null);

            // Then
            assertEquals("options/options", result);
            verify(securityService).autoLogin(userName, password);
            verify(model).addAttribute("error", "Your username and password is invalid.");
        }

        @Test
        @DisplayName("should add message attribute when logout parameter is present")
        void testLoginGet_WhenLogoutPresent_ShouldAddMessageAttribute() {
            // Given
            var userName = "testuser";
            var password = "testpass";
            var logout = "true";

            // When
            var result = loginController.loginGet(model, userName, password, null, logout);

            // Then
            assertEquals("options/options", result);
            verify(securityService).autoLogin(userName, password);
            verify(model).addAttribute("message", "You have been logged out successfully.");
        }

        @Test
        @DisplayName("should prioritize logout over error when both are present")
        void testLoginGet_WhenBothErrorAndLogout_ShouldPrioritizeLogout() {
            // Given
            var userName = "testuser";
            var password = "testpass";
            var error = "error";
            var logout = "true";

            // When
            var result = loginController.loginGet(model, userName, password, error, logout);

            // Then
            assertEquals("options/options", result);
            verify(model).addAttribute("message", "You have been logged out successfully.");
        }

        @Test
        @DisplayName("should always create new User object and add to model")
        void testLoginGet_ShouldAlwaysAddNewUserForm() {
            // Given
            var userName = "testuser";
            var password = "testpass";

            // When
            var result = loginController.loginGet(model, userName, password, null, null);

            // Then
            assertEquals("options/options", result);
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(model).addAttribute(eq("userForm"), userCaptor.capture());
            assertNotNull(userCaptor.getValue());
        }

        @Test
        @DisplayName("should call autoLogin with provided credentials")
        void testLoginGet_ShouldCallAutoLoginWithCorrectCredentials() {
            // Given
            var userName = "john.doe";
            var password = "secure123";

            // When
            loginController.loginGet(model, userName, password, null, null);

            // Then
            verify(securityService, times(1)).autoLogin(userName, password);
        }
    }

    @Nested
    @DisplayName("logout() method tests")
    class LogoutMethodTests {

        @Test
        @DisplayName("should clear security context when session exists")
        void testLogout_WhenSessionExists_ShouldClearSecurityContextAndInvalidateSession() {
            // Given
            when(request.getSession(false)).thenReturn(session);

            // When
            try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
                String result = loginController.logout(request);

                // Then
                assertEquals("login", result);
                verify(request).getSession(false);
                verify(session).invalidate();
                mockedSecurityContext.verify(SecurityContextHolder::clearContext);
            }
        }

        @Test
        @DisplayName("should clear security context when session is null")
        void testLogout_WhenSessionIsNull_ShouldClearSecurityContext() {
            // Given
            when(request.getSession(false)).thenReturn(null);

            // When
            try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
                String result = loginController.logout(request);

                // Then
                assertEquals("login", result);
                verify(request).getSession(false);
                mockedSecurityContext.verify(SecurityContextHolder::clearContext);
            }
        }

        @Test
        @DisplayName("should not invalidate null session")
        void testLogout_WhenSessionIsNull_ShouldNotCallInvalidate() {
            // Given
            when(request.getSession(false)).thenReturn(null);

            // When
            try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
                loginController.logout(request);

                // Then
                verify(session, never()).invalidate();
                mockedSecurityContext.verify(SecurityContextHolder::clearContext);
            }
        }

        @Test
        @DisplayName("should return login view after logout")
        void testLogout_ShouldReturnLoginView() {
            // Given
            when(request.getSession(false)).thenReturn(session);

            // When
            try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
                String result = loginController.logout(request);

                // Then
                assertEquals("login", result);
            }
        }

        @Test
        @DisplayName("should use getSession(false) to avoid creating new session")
        void testLogout_ShouldUseGetSessionFalse() {
            // Given
            when(request.getSession(false)).thenReturn(null);

            // When
            try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
                loginController.logout(request);

                // Then
                verify(request).getSession(false);
                verify(request, never()).getSession();
                verify(request, never()).getSession(true);
            }
        }
    }

    @Nested
    @DisplayName("loginMe() method tests")
    class LoginMeMethodTests {

        @Test
        @DisplayName("should return login view name for POST request")
        void testLoginMe_ShouldReturnLoginViewName() {
            // When
            var result = loginController.loginMe();

            // Then
            assertEquals("login", result);
        }
    }

    @Nested
    @DisplayName("loginMe2() method tests")
    class LoginMe2MethodTests {

        @Test
        @DisplayName("should return login view name for GET request")
        void testLoginMe2_ShouldReturnLoginViewName() {
            // When
            var result = loginController.loginMe2();

            // Then
            assertEquals("login", result);
        }
    }

    @Nested
    @DisplayName("Integration scenario tests")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("should handle complete login flow: login -> redirect when authenticated")
        void testCompleteLoginFlow_WhenAlreadyAuthenticated() {
            // Given
            when(securityService.isAuthenticated()).thenReturn(true);

            // When
            var result = loginController.login(model, null, null);

            // Then
            assertEquals("redirect:/", result);
            verify(securityService).isAuthenticated();
        }

        @Test
        @DisplayName("should handle login page with error and logout parameters")
        void testLoginWithMultipleParameters() {
            // Given
            when(securityService.isAuthenticated()).thenReturn(false);

            // When
            var resultWithError = loginController.login(model, "true", null);
            var resultWithLogout = loginController.login(model, null, "true");

            // Then
            assertEquals("login", resultWithError);
            assertEquals("login", resultWithLogout);
            verify(model).addAttribute("danger", "Your username or password is invalid.");
            verify(model).addAttribute("message", "You have been logged out successfully.");
        }

        @Test
        @DisplayName("should sanitize username in logging")
        void testLoginGetSanitizesUsernameLogging() {
            // Given
            var userName = "test<script>alert('xss')</script>user";
            var password = "password";

            // When
            try (MockedStatic<CommonUtils> mockedCommonUtils = mockStatic(CommonUtils.class)) {
                mockedCommonUtils.when(() -> CommonUtils.sanitizedString(anyString()))
                        .thenReturn("tesscriptalert(xss)script/user");

                loginController.loginGet(model, userName, password, null, null);

                // Then
                mockedCommonUtils.verify(() -> CommonUtils.sanitizedString(userName), times(1));
            }
        }
    }
}

