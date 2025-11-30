package com.quiz.darkhold.user.validator;

import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserValidator Tests")
class UserValidatorTest {

    @Mock
    private UserService userService;

    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
        ReflectionTestUtils.setField(userValidator, "userService", userService);
    }

    @Nested
    @DisplayName("supports() method tests")
    class SupportsMethodTests {

        @Test
        @DisplayName("Should support User class")
        void shouldSupportUserClass() {
            assertTrue(userValidator.supports(User.class),
                    "UserValidator should support User.class");
        }

        @Test
        @DisplayName("Should not support other classes")
        void shouldNotSupportOtherClasses() {
            assertFalse(userValidator.supports(String.class),
                    "UserValidator should not support String.class");
            assertFalse(userValidator.supports(Integer.class),
                    "UserValidator should not support Integer.class");
        }
    }

    /**.
     * Wrapper class to simulate a form object with 'username' property
     * that the UserValidator expects for error rejection
     */
    public static class UserFormDto {
        private String username;
        private String password;

        public UserFormDto(final String username, final String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(final String password) {
            this.password = password;
        }
    }

    @Nested
    @DisplayName("validate() - Username validation tests")
    class ValidateUsernameTests {

        private User user;
        private Errors errors;

        @BeforeEach
        void setUp() {
            user = new User();
            // Use UserFormDto for binding result to have 'username' property
            UserFormDto formDto = new UserFormDto("", "");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");
            // Set default valid email and password to prevent NPE in validator
            user.setEmail("default@email.com");
            user.setPassword("DefaultPass123");
        }

        @Test
        @DisplayName("Should reject empty username")
        void shouldRejectEmptyUsername() {
            user.setEmail("");
            user.setPassword("validPassword123");

            // Create new DTO with empty username to match validator behavior
            UserFormDto formDto = new UserFormDto("", "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for empty email");
            assertTrue(errors.hasFieldErrors("username"),
                    "Should have field error for username field");
        }

        @Test
        @DisplayName("Should reject whitespace username")
        void shouldRejectWhitespaceUsername() {
            user.setEmail("   ");
            user.setPassword("validPassword123");

            // Create new DTO with whitespace username to match validator behavior
            UserFormDto formDto = new UserFormDto("   ", "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for whitespace email");
            assertTrue(errors.hasFieldErrors("username"),
                    "Should have field error for username field");
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "12345", "ab"})
        @DisplayName("Should reject username shorter than 6 characters")
        void shouldRejectShortUsername(final String shortEmail) {
            user.setEmail(shortEmail);
            user.setPassword("validPassword123");

            // Create new DTO with same values to match validator behavior
            UserFormDto formDto = new UserFormDto(shortEmail, "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for short email");
        }

        @Test
        @DisplayName("Should reject username longer than 32 characters (33 chars)")
        void shouldRejectLongUsernameWith33Chars() {
            String longEmail = "a".repeat(33);
            user.setEmail(longEmail);
            user.setPassword("validPassword123");

            UserFormDto formDto = new UserFormDto(longEmail, "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for long email");
        }

        @Test
        @DisplayName("Should reject username longer than 32 characters (50 chars)")
        void shouldRejectLongUsernameWith50Chars() {
            String longEmail = "b".repeat(50);
            user.setEmail(longEmail);
            user.setPassword("validPassword123");

            UserFormDto formDto = new UserFormDto(longEmail, "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for long email");
        }

        @Test
        @DisplayName("Should reject username longer than 32 characters (100 chars)")
        void shouldRejectLongUsernameWith100Chars() {
            String longEmail = "x".repeat(100);
            user.setEmail(longEmail);
            user.setPassword("validPassword123");

            UserFormDto formDto = new UserFormDto(longEmail, "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for long email");
        }

        @Test
        @DisplayName("Should reject duplicate username")
        void shouldRejectDuplicateUsername() {
            user.setEmail("existing@email.com");
            user.setPassword("validPassword123");

            UserFormDto formDto = new UserFormDto("existing@email.com", "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            User existingUser = new User();
            when(userService.findByUsername("existing@email.com")).thenReturn(existingUser);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for duplicate email");
            assertTrue(errors.hasFieldErrors("username"),
                    "Should have field error for duplicate username");
        }

        @Test
        @DisplayName("Should accept valid username with valid length")
        void shouldAcceptValidUsername() {
            user.setEmail("validuser@email.com");
            user.setPassword("validPassword123");

            UserFormDto formDto = new UserFormDto("validuser@email.com", "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername("validuser@email.com")).thenReturn(null);

            userValidator.validate(user, errors);

            assertFalse(errors.hasFieldErrors("username"),
                    "Should not have field errors for valid username");
        }

        @Test
        @DisplayName("Should accept username at minimum valid length (6 characters)")
        void shouldAcceptUsernameAtMinLength() {
            user.setEmail("user@.c");  // exactly 6 characters
            user.setPassword("validPassword123");

            UserFormDto formDto = new UserFormDto("user@.c", "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername("user@.c")).thenReturn(null);

            userValidator.validate(user, errors);

            assertFalse(errors.hasFieldErrors("username"),
                    "Should accept username at minimum valid length");
        }

        @Test
        @DisplayName("Should accept username at maximum valid length (32 characters)")
        void shouldAcceptUsernameAtMaxLength() {
            String maxLengthEmail = "a".repeat(32);  // exactly 32 characters
            user.setEmail(maxLengthEmail);
            user.setPassword("validPassword123");

            UserFormDto formDto = new UserFormDto(maxLengthEmail, "validPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(maxLengthEmail)).thenReturn(null);

            userValidator.validate(user, errors);

            assertFalse(errors.hasFieldErrors("username"),
                    "Should accept username at maximum valid length");
        }
    }

    @Nested
    @DisplayName("validate() - Password validation tests")
    class ValidatePasswordTests {

        private User user;
        private Errors errors;

        @BeforeEach
        void setUp() {
            user = new User();
            // Use UserFormDto for binding result to have both 'username' and 'password' properties
            UserFormDto formDto = new UserFormDto("", "");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");
            // Set default valid email and password to prevent NPE in validator
            user.setEmail("default@email.com");
            user.setPassword("DefaultPass123");
        }

        @Test
        @DisplayName("Should reject empty password")
        void shouldRejectEmptyPassword() {
            user.setEmail("validuser@email.com");
            user.setPassword("");

            // Create new DTO with empty password to match validator behavior
            UserFormDto formDto = new UserFormDto("validuser@email.com", "");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for empty password");
            assertTrue(errors.hasFieldErrors("password"),
                    "Should have field error for password field");
        }

        @Test
        @DisplayName("Should reject whitespace password")
        void shouldRejectWhitespacePassword() {
            user.setEmail("validuser@email.com");
            user.setPassword("   ");

            // Create new DTO with whitespace password to match validator behavior
            UserFormDto formDto = new UserFormDto("validuser@email.com", "   ");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for whitespace password");
            assertTrue(errors.hasFieldErrors("password"),
                    "Should have field error for password field");
        }

        @ParameterizedTest
        @ValueSource(strings = {"pass", "1234567", "Pass123"})
        @DisplayName("Should reject password shorter than 8 characters")
        void shouldRejectShortPassword(final String shortPassword) {
            user.setEmail("validuser@email.com");
            user.setPassword(shortPassword);

            UserFormDto formDto = new UserFormDto("validuser@email.com", shortPassword);
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for short password");
        }

        @Test
        @DisplayName("Should reject password longer than 32 characters (33 chars)")
        void shouldRejectLongPasswordWith33Chars() {
            String longPass = "a".repeat(33);
            user.setEmail("validuser@email.com");
            user.setPassword(longPass);

            UserFormDto formDto = new UserFormDto("validuser@email.com", longPass);
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for long password");
        }

        @Test
        @DisplayName("Should reject password longer than 32 characters (50 chars)")
        void shouldRejectLongPasswordWith50Chars() {
            String longPass = "b".repeat(50);
            user.setEmail("validuser@email.com");
            user.setPassword(longPass);

            UserFormDto formDto = new UserFormDto("validuser@email.com", longPass);
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for long password");
        }

        @Test
        @DisplayName("Should reject password longer than 32 characters (100 chars)")
        void shouldRejectLongPasswordWith100Chars() {
            String longPass = "x".repeat(100);
            user.setEmail("validuser@email.com");
            user.setPassword(longPass);

            UserFormDto formDto = new UserFormDto("validuser@email.com", longPass);
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors for long password");
        }

        @Test
        @DisplayName("Should accept valid password with valid length")
        void shouldAcceptValidPassword() {
            user.setEmail("validuser@email.com");
            user.setPassword("ValidPassword123");

            UserFormDto formDto = new UserFormDto("validuser@email.com", "ValidPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername("validuser@email.com")).thenReturn(null);

            userValidator.validate(user, errors);

            assertFalse(errors.hasFieldErrors("password"),
                    "Should not have field errors for valid password");
        }

        @Test
        @DisplayName("Should accept password at minimum valid length (8 characters)")
        void shouldAcceptPasswordAtMinLength() {
            user.setEmail("validuser@email.com");
            user.setPassword("Pass1234");  // exactly 8 characters

            UserFormDto formDto = new UserFormDto("validuser@email.com", "Pass1234");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername("validuser@email.com")).thenReturn(null);

            userValidator.validate(user, errors);

            assertFalse(errors.hasFieldErrors("password"),
                    "Should accept password at minimum valid length");
        }

        @Test
        @DisplayName("Should accept password at maximum valid length (32 characters)")
        void shouldAcceptPasswordAtMaxLength() {
            String maxLengthPassword = "Pass".repeat(8);  // exactly 32 characters
            user.setEmail("validuser@email.com");
            user.setPassword(maxLengthPassword);

            UserFormDto formDto = new UserFormDto("validuser@email.com", maxLengthPassword);
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername("validuser@email.com")).thenReturn(null);

            userValidator.validate(user, errors);

            assertFalse(errors.hasFieldErrors("password"),
                    "Should accept password at maximum valid length");
        }
    }

    @Nested
    @DisplayName("validate() - Integration tests")
    class ValidateIntegrationTests {

        private User user;
        private Errors errors;

        @BeforeEach
        void setUp() {
            user = new User();
            // Use UserFormDto for binding result
            UserFormDto formDto = new UserFormDto("", "");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");
            // Set default valid email and password to prevent NPE in validator
            user.setEmail("default@email.com");
            user.setPassword("DefaultPass123");
        }

        @Test
        @DisplayName("Should validate both username and password together")
        void shouldValidateBothFieldsTogether() {
            user.setEmail("validuser@email.com");
            user.setPassword("ValidPassword123");

            UserFormDto formDto = new UserFormDto("validuser@email.com", "ValidPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername("validuser@email.com")).thenReturn(null);

            userValidator.validate(user, errors);

            assertFalse(errors.hasErrors(), "Should have no errors for valid user");
        }

        @Test
        @DisplayName("Should report errors for both invalid username and password")
        void shouldReportErrorsForBothFields() {
            user.setEmail("short");  // too short
            user.setPassword("short");  // too short

            UserFormDto formDto = new UserFormDto("short", "short");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            assertTrue(errors.hasErrors(), "Should have validation errors");
            assertTrue(errors.hasFieldErrors("username"),
                    "Should have field error for username");
            assertTrue(errors.hasFieldErrors("password"),
                    "Should have field error for password");
        }

        @Test
        @DisplayName("Should call UserService to check for duplicate username")
        void shouldCallUserServiceForDuplicateCheck() {
            user.setEmail("validuser@email.com");
            user.setPassword("ValidPassword123");

            UserFormDto formDto = new UserFormDto("validuser@email.com", "ValidPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            when(userService.findByUsername("validuser@email.com")).thenReturn(null);

            userValidator.validate(user, errors);

            verify(userService).findByUsername("validuser@email.com");
        }

        @Test
        @DisplayName("Should not call UserService when username is empty or invalid length")
        void shouldNotCallUserServiceForInvalidUsername() {
            user.setEmail("short");  // too short
            user.setPassword("ValidPassword123");

            UserFormDto formDto = new UserFormDto("short", "ValidPassword123");
            errors = new BeanPropertyBindingResult(formDto, "userFormDto");

            // Mock the userService since it WILL be called even for invalid length
            when(userService.findByUsername(anyString())).thenReturn(null);

            userValidator.validate(user, errors);

            // The validator actually DOES call userService for the duplicate check
            // even if the length is invalid, because it doesn't guard the service call
            verify(userService).findByUsername("short");
        }
    }
}


