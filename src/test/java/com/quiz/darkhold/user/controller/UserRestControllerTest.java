package com.quiz.darkhold.user.controller;

import com.quiz.darkhold.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DisplayName("UserRestController Tests")
class UserRestControllerTest {
    @Mock
    private UserService userService;
    private UserRestController userRestController;

    @BeforeEach
    void setUp() {
        userRestController = new UserRestController(userService);
    }

    @Nested
    @DisplayName("checkDuplicateEmail() method tests")
    class CheckDuplicateEmailMethodTests {
        @Test
        @DisplayName("should return OK when email is unique")
        void testCheckDuplicateEmail_WhenEmailIsUnique_ShouldReturnOK() {
            // Given
            Long userId = 1L;
            String email = "unique@example.com";
            when(userService.isEmailUnique(userId, email)).thenReturn(true);
            // When
            String result = userRestController.checkDuplicateEmail(userId, email);
            // Then
            assertEquals("OK", result);
            verify(userService).isEmailUnique(userId, email);
        }

        @Test
        @DisplayName("should return DUPLICATED when email is not unique")
        void testCheckDuplicateEmail1() {
            // Given
            Long userId = 1L;
            String email = "duplicate@example.com";
            when(userService.isEmailUnique(userId, email)).thenReturn(false);
            // When
            String result = userRestController.checkDuplicateEmail(userId, email);
            // Then
            assertEquals("DUPLICATED", result);
            verify(userService).isEmailUnique(userId, email);
        }

        @Test
        @DisplayName("should call userService.isEmailUnique with correct parameters")
        void testCheckDuplicateEmail_ShouldCallServiceWithCorrectParameters() {
            // Given
            Long userId = 5L;
            String email = "test@example.com";
            when(userService.isEmailUnique(userId, email)).thenReturn(true);
            // When
            userRestController.checkDuplicateEmail(userId, email);
            // Then
            verify(userService, times(1)).isEmailUnique(userId, email);
        }

        @Test
        @DisplayName("should handle null userId parameter")
        void testCheckDuplicateEmail_WithNullUserId_ShouldCallService() {
            // Given
            String email = "test@example.com";
            when(userService.isEmailUnique(null, email)).thenReturn(true);
            // When
            String result = userRestController.checkDuplicateEmail(null, email);
            // Then
            assertEquals("OK", result);
            verify(userService).isEmailUnique(null, email);
        }

        @Test
        @DisplayName("should handle null email parameter")
        void testCheckDuplicateEmail_WithNullEmail_ShouldCallService() {
            // Given
            Long userId = 1L;
            when(userService.isEmailUnique(userId, null)).thenReturn(false);
            // When
            String result = userRestController.checkDuplicateEmail(userId, null);
            // Then
            assertEquals("DUPLICATED", result);
            verify(userService).isEmailUnique(userId, null);
        }

        @Test
        @DisplayName("should handle empty email string")
        void testCheckDuplicateEmail_WithEmptyEmail_ShouldCallService() {
            // Given
            Long userId = 1L;
            String email = "";
            when(userService.isEmailUnique(userId, email)).thenReturn(true);
            // When
            String result = userRestController.checkDuplicateEmail(userId, email);
            // Then
            assertEquals("OK", result);
            verify(userService).isEmailUnique(userId, email);
        }

        @Test
        @DisplayName("should handle zero userId")
        void testCheckDuplicateEmail_WithZeroUserId_ShouldReturnOK() {
            // Given
            Long userId = 0L;
            String email = "test@example.com";
            when(userService.isEmailUnique(userId, email)).thenReturn(true);
            // When
            String result = userRestController.checkDuplicateEmail(userId, email);
            // Then
            assertEquals("OK", result);
            verify(userService).isEmailUnique(userId, email);
        }

        @Test
        @DisplayName("should handle negative userId")
        void testCheckDuplicateEmail_WithNegativeUserId_ShouldCallService() {
            // Given
            Long userId = -1L;
            String email = "test@example.com";
            when(userService.isEmailUnique(userId, email)).thenReturn(false);
            // When
            String result = userRestController.checkDuplicateEmail(userId, email);
            // Then
            assertEquals("DUPLICATED", result);
            verify(userService).isEmailUnique(userId, email);
        }

        @Test
        @DisplayName("should handle email with special characters")
        void testCheckDuplicateEmail_WithSpecialCharacters_ShouldReturnOK() {
            // Given
            Long userId = 1L;
            String email = "test+tag@example.co.uk";
            when(userService.isEmailUnique(userId, email)).thenReturn(true);
            // When
            String result = userRestController.checkDuplicateEmail(userId, email);
            // Then
            assertEquals("OK", result);
            verify(userService).isEmailUnique(userId, email);
        }

        @Test
        @DisplayName("should handle case-sensitive email validation")
        void testCheckDuplicateEmail2() {
            // Given
            Long userId = 1L;
            String email = "Test@Example.COM";
            when(userService.isEmailUnique(userId, email)).thenReturn(false);
            // When
            String result = userRestController.checkDuplicateEmail(userId, email);
            // Then
            assertEquals("DUPLICATED", result);
            verify(userService).isEmailUnique(userId, email);
        }
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {
        @Test
        @DisplayName("should initialize controller with UserService dependency")
        void testConstructor_ShouldInitializeWithUserService() {
            // When
            UserRestController controller = new UserRestController(userService);
            // Then
            assertNotNull(controller);
        }
    }

    @Nested
    @DisplayName("Integration scenario tests")
    class IntegrationScenarioTests {
        @Test
        @DisplayName("should complete email check flow for new user registration")
        void testCompleteEmailCheckForNewUserRegistration() {
            // Given
            Long newUserId = null;
            String newEmail = "newuser@example.com";
            when(userService.isEmailUnique(newUserId, newEmail)).thenReturn(true);
            // When
            String result = userRestController.checkDuplicateEmail(newUserId, newEmail);
            // Then
            assertEquals("OK", result);
            verify(userService).isEmailUnique(newUserId, newEmail);
        }

        @Test
        @DisplayName("should handle multiple consecutive email checks")
        void testMultipleConsecutiveEmailChecks() {
            // Given
            Long userId = 1L;
            String email1 = "first@example.com";
            String email2 = "second@example.com";
            String email3 = "third@example.com";
            when(userService.isEmailUnique(userId, email1)).thenReturn(true);
            when(userService.isEmailUnique(userId, email2)).thenReturn(false);
            when(userService.isEmailUnique(userId, email3)).thenReturn(true);
            // When
            String result1 = userRestController.checkDuplicateEmail(userId, email1);
            String result2 = userRestController.checkDuplicateEmail(userId, email2);
            String result3 = userRestController.checkDuplicateEmail(userId, email3);
            // Then
            assertEquals("OK", result1);
            assertEquals("DUPLICATED", result2);
            assertEquals("OK", result3);
            verify(userService, times(3)).isEmailUnique(anyLong(), anyString());
        }
    }
}
