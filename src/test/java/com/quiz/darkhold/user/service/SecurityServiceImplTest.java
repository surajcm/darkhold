package com.quiz.darkhold.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityServiceImpl Tests")
class SecurityServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private SecurityServiceImpl securityService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("findLoggedInUsername tests")
    class FindLoggedInUsernameTests {

        @Test
        @DisplayName("Should return username from UserDetails")
        void shouldReturnUsernameFromUserDetails() {
            UserDetails userDetails = new User("admin@test.com", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            Authentication auth = mock(Authentication.class);
            when(auth.getDetails()).thenReturn(userDetails);
            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            String result = securityService.findLoggedInUsername();

            assertEquals("admin@test.com", result);
        }

        @Test
        @DisplayName("Should return null when not UserDetails")
        void shouldReturnNullWhenNotUserDetails() {
            Authentication auth = mock(Authentication.class);
            when(auth.getDetails()).thenReturn("not-user-details");
            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            String result = securityService.findLoggedInUsername();

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("autoLogin tests")
    class AutoLoginTests {

        @Test
        @DisplayName("Should authenticate registered user via AuthenticationManager")
        void shouldAuthenticateRegisteredUser() {
            UserDetails userDetails = new User("admin@test.com", "password",
                    Set.of(new SimpleGrantedAuthority("ROLE_MODERATOR")));
            when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenAnswer(inv -> {
                        UsernamePasswordAuthenticationToken tok = inv.getArgument(0);
                        return tok;
                    });

            securityService.autoLogin("admin@test.com", "password");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Should skip authentication for UNREGISTERED_USER")
        void shouldSkipAuthenticationForUnregisteredUser() {
            securityService.autoLogin("player1", "UNREGISTERED_USER");

            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("Should set SecurityContext after login")
        void shouldSetSecurityContextAfterLogin() {
            UserDetails userDetails = new User("admin@test.com", "password",
                    Set.of(new SimpleGrantedAuthority("ROLE_MODERATOR")));
            when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            securityService.autoLogin("admin@test.com", "password");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertTrue(auth.isAuthenticated());
        }
    }

    @Nested
    @DisplayName("isAuthenticated tests")
    class IsAuthenticatedTests {

        @Test
        @DisplayName("Should return false when authentication is null")
        void shouldReturnFalseWhenAuthenticationIsNull() {
            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(null);
            SecurityContextHolder.setContext(context);

            assertFalse(securityService.isAuthenticated());
        }

        @Test
        @DisplayName("Should return false for AnonymousAuthenticationToken")
        void shouldReturnFalseForAnonymousAuthenticationToken() {
            AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                    "key", "anonymous",
                    List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(anonymousToken);
            SecurityContextHolder.setContext(context);

            assertFalse(securityService.isAuthenticated());
        }

        @Test
        @DisplayName("Should return true when authenticated")
        void shouldReturnTrueWhenAuthenticated() {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "user", "pass",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContext context = mock(SecurityContext.class);
            when(context.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(context);

            assertTrue(securityService.isAuthenticated());
        }
    }
}
