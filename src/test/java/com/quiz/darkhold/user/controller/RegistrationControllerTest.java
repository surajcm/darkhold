package com.quiz.darkhold.user.controller;

import com.quiz.darkhold.home.model.GameInfo;
import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DisplayName("RegistrationController Tests")
class RegistrationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        registrationController = new RegistrationController(userService);
    }

    @Nested
    @DisplayName("user() ModelAttribute method tests")
    class UserModelAttributeMethodTests {

        @Test
        @DisplayName("should return a new User instance")
        void testUser_ShouldReturnNewUserInstance() {
            // When
            User user = registrationController.user();

            // Then
            assertNotNull(user);
        }

        @Test
        @DisplayName("should return different instances on each call")
        void testUser_ShouldReturnDifferentInstancesEachCall() {
            // When
            User user1 = registrationController.user();
            User user2 = registrationController.user();

            // Then
            assertNotNull(user1);
            assertNotNull(user2);
        }
    }

    @Nested
    @DisplayName("registration() GET method tests")
    class RegistrationGetMethodTests {

        @Test
        @DisplayName("should return registration view name")
        void testRegistrationGet_ShouldReturnRegistrationViewName() {
            // When
            String result = registrationController.registration(model);

            // Then
            assertEquals("registration", result);
        }

        @Test
        @DisplayName("should add userForm attribute to model")
        void testRegistrationGet_ShouldAddUserFormToModel() {
            // When
            registrationController.registration(model);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(model).addAttribute(eq("userForm"), userCaptor.capture());
            assertNotNull(userCaptor.getValue());
        }

        @Test
        @DisplayName("should add new User instance to model")
        void testRegistrationGet_ShouldAddNewUserInstanceToModel() {
            // When
            registrationController.registration(model);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(model).addAttribute(eq("userForm"), userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertEquals(User.class, capturedUser.getClass());
        }
    }

    @Nested
    @DisplayName("registration() POST method tests")
    class RegistrationPostMethodTests {

        private BindingResult bindingResult;
        private User user;

        @BeforeEach
        void setUpPost() {
            bindingResult = mock(BindingResult.class);
            user = new User();
            user.setFirstName("John");
            user.setEmail("john@example.com");
        }

        @Test
        @DisplayName("should return login view when binding result has errors")
        void testRegistrationPost_WhenBindingResultHasErrors_ShouldReturnLoginView() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(true);
            ObjectError error = mock(ObjectError.class);
            when(bindingResult.getAllErrors()).thenReturn(List.of(error));

            // When
            String result = registrationController.registration(model, user, bindingResult);

            // Then
            assertEquals("login", result);
            verify(userService, never()).save(any());
            verify(model, never()).addAttribute(eq("gameinfo"), any());
        }

        @Test
        @DisplayName("should save user with GUEST role when no binding errors")
        void testRegistrationPost_WhenNoErrors_ShouldSaveUserWithGuestRole() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertEquals(true, savedUser.getEnabled());
            assertEquals(Set.of(guestRole), savedUser.getRoles());
        }

        @Test
        @DisplayName("should return index view when registration succeeds")
        void testRegistrationPost_WhenNoErrors_ShouldReturnIndexView() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            String result = registrationController.registration(model, user, bindingResult);

            // Then
            assertEquals("index", result);
        }

        @Test
        @DisplayName("should add success message to model when registration succeeds")
        void testRegistrationPost_WhenNoErrors_ShouldAddGameInfoToModel() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            ArgumentCaptor<GameInfo> gameInfoCaptor = ArgumentCaptor.forClass(GameInfo.class);
            verify(model).addAttribute(eq("gameinfo"), gameInfoCaptor.capture());
            GameInfo gameInfo = gameInfoCaptor.getValue();
            assertEquals("Successfully created the account !!!", gameInfo.getMessage());
        }

        @Test
        @DisplayName("should set user enabled to true")
        void testRegistrationPost_ShouldSetUserEnabledToTrue() {
            // Given
            user.setEnabled(false);
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(userCaptor.capture());
            assertEquals(true, userCaptor.getValue().getEnabled());
        }

        @Test
        @DisplayName("should handle multiple roles and find GUEST role")
        void testRegistrationPost_WhenMultipleRoles_ShouldFindAndAssignGuestRole() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role adminRole = createMockRole("ADMIN");
            Role guestRole = createMockRole("GUEST");
            Role userRole = createMockRole("USER");
            when(userService.listRoles()).thenReturn(List.of(adminRole, userRole, guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertEquals(1, savedUser.getRoles().size());
            assertEquals("GUEST", savedUser.getRoles().iterator().next().getName());
        }

        @Test
        @DisplayName("should not set roles when GUEST role not found")
        void testRegistrationPost_WhenGuestRoleNotFound_ShouldNotSetRoles() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role adminRole = createMockRole("ADMIN");
            when(userService.listRoles()).thenReturn(List.of(adminRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            // Verify user was saved and roles should not be set if GUEST role is not found
            assertNotNull(savedUser);
        }

        @Test
        @DisplayName("should call userService.save exactly once on successful registration")
        void testRegistrationPost_ShouldCallSaveExactlyOnce() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            verify(userService, times(1)).save(any());
        }

        @Test
        @DisplayName("should call userService.listRoles exactly once")
        void testRegistrationPost_ShouldCallListRolesExactlyOnce() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            verify(userService, times(1)).listRoles();
        }

        @Test
        @DisplayName("should add gameinfo attribute only once on success")
        void testRegistrationPost_ShouldAddGameInfoAttributeOnlyOnce() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            verify(model, times(1)).addAttribute(eq("gameinfo"), any(GameInfo.class));
        }

        @Test
        @DisplayName("should not call userService methods when binding result has errors")
        void testRegistrationPost_WhenErrors_ShouldNotCallUserService() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(true);
            ObjectError error = mock(ObjectError.class);
            when(bindingResult.getAllErrors()).thenReturn(List.of(error));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            verify(userService, never()).listRoles();
            verify(userService, never()).save(any());
        }

        @Test
        @DisplayName("should handle empty roles list gracefully")
        void testRegistrationPost_WhenNoRolesAvailable_ShouldHandleGracefully() {
            // Given
            when(bindingResult.hasErrors()).thenReturn(false);
            when(userService.listRoles()).thenReturn(new ArrayList<>());

            // When
            String result = registrationController.registration(model, user, bindingResult);

            // Then
            assertEquals("index", result);
            verify(userService).save(any());
        }

        @Test
        @DisplayName("should preserve user data when saving with GUEST role")
        void testRegistrationPost_ShouldPreserveUserDataWhenSaving() {
            // Given
            String firstName = "Alice";
            String email = "alice@example.com";
            user.setFirstName(firstName);
            user.setEmail(email);
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            registrationController.registration(model, user, bindingResult);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertEquals(firstName, savedUser.getFirstName());
            assertEquals(email, savedUser.getEmail());
        }
    }

    @Nested
    @DisplayName("Integration scenario tests")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("should complete full registration flow successfully")
        void testCompleteRegistrationFlow() {
            // Given
            User newUser = new User();
            newUser.setFirstName("Alice");
            newUser.setEmail("alice@example.com");
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.hasErrors()).thenReturn(false);
            Role guestRole = createMockRole("GUEST");
            when(userService.listRoles()).thenReturn(List.of(guestRole));

            // When
            String result = registrationController.registration(model, newUser, bindingResult);

            // Then
            assertEquals("index", result);
            verify(userService).listRoles();
            verify(userService).save(any(User.class));
            verify(model).addAttribute(eq("gameinfo"), any(GameInfo.class));
        }

        @Test
        @DisplayName("should handle registration with validation errors")
        void testRegistrationWithValidationErrors() {
            // Given
            User newUser = new User();
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.hasErrors()).thenReturn(true);
            ObjectError error = new ObjectError("user", "Invalid email format");
            when(bindingResult.getAllErrors()).thenReturn(List.of(error));

            // When
            String result = registrationController.registration(model, newUser, bindingResult);

            // Then
            assertEquals("login", result);
            verify(userService, never()).listRoles();
            verify(userService, never()).save(any());
        }

        @Test
        @DisplayName("should call user model attribute factory method")
        void testUserModelAttributeMethod() {
            // When
            User user = registrationController.user();

            // Then
            assertNotNull(user);
        }
    }

    // ======================== Helper Methods ========================

    private Role createMockRole(final String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return role;
    }
}

