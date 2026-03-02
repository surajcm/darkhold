package com.quiz.darkhold.user.service;

import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.exception.UserNotFoundException;
import com.quiz.darkhold.user.repository.RoleRepository;
import com.quiz.darkhold.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, encoder);
    }

    @Nested
    @DisplayName("save tests")
    class SaveTests {

        @Test
        @DisplayName("Should encode password for new user")
        void shouldEncodePasswordForNewUser() {
            User newUser = createUser(null, "test@test.com", "rawPassword");
            when(encoder.encode("rawPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            userService.save(newUser);

            assertEquals("encodedPassword", newUser.getPassword());
            verify(userRepository).save(newUser);
        }

        @Test
        @DisplayName("Should preserve existing password when update password is empty")
        void shouldPreserveExistingPasswordWhenEmpty() {
            User existingUser = createUser(1L, "test@test.com", "existingEncoded");
            User updatingUser = createUser(1L, "test@test.com", "");
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(updatingUser);

            userService.save(updatingUser);

            assertEquals("existingEncoded", updatingUser.getPassword());
        }

        @Test
        @DisplayName("Should encode new password when updating with new password")
        void shouldEncodeNewPasswordWhenUpdating() {
            User existingUser = createUser(1L, "test@test.com", "oldEncoded");
            User updatingUser = createUser(1L, "test@test.com", "newRawPassword");
            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(encoder.encode("newRawPassword")).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(updatingUser);

            userService.save(updatingUser);

            assertEquals("newEncodedPassword", updatingUser.getPassword());
        }

        @Test
        @DisplayName("Should save via repository")
        void shouldSaveViaRepository() {
            User newUser = createUser(null, "test@test.com", "pass");
            when(encoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            User result = userService.save(newUser);

            verify(userRepository).save(newUser);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("findByUsername tests")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should delegate to repository findByEmail")
        void shouldDelegateToRepositoryFindByEmail() {
            User user = createUser(1L, "user@test.com", "pass");
            when(userRepository.findByEmail("user@test.com")).thenReturn(user);

            User result = userService.findByUsername("user@test.com");

            assertEquals(user, result);
            verify(userRepository).findByEmail("user@test.com");
        }
    }

    @Nested
    @DisplayName("getAllUsers tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should create PageRequest with correct page number and USERS_PER_PAGE")
        void shouldCreatePageRequestWithCorrectParams() {
            Page<User> page = new PageImpl<>(List.of());
            when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

            userService.getAllUsers(1);

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(userRepository).findAll(captor.capture());
            assertEquals(0, captor.getValue().getPageNumber());
            assertEquals(UserServiceImpl.USERS_PER_PAGE, captor.getValue().getPageSize());
        }
    }

    @Nested
    @DisplayName("listRoles tests")
    class ListRolesTests {

        @Test
        @DisplayName("Should delegate to roleRepository findAll")
        void shouldDelegateToRoleRepositoryFindAll() {
            List<Role> roles = List.of(new Role());
            when(roleRepository.findAll()).thenReturn(roles);

            List<Role> result = userService.listRoles();

            assertEquals(roles, result);
            verify(roleRepository).findAll();
        }
    }

    @Nested
    @DisplayName("isEmailUnique tests")
    class IsEmailUniqueTests {

        @Test
        @DisplayName("Should return true when no user with email")
        void shouldReturnTrueWhenNoUserWithEmail() {
            when(userRepository.findByEmail("new@test.com")).thenReturn(null);

            Boolean result = userService.isEmailUnique(null, "new@test.com");

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true when updating own email")
        void shouldReturnTrueWhenUpdatingOwnEmail() {
            User existing = createUser(5L, "user@test.com", "pass");
            when(userRepository.findByEmail("user@test.com")).thenReturn(existing);

            Boolean result = userService.isEmailUnique(5L, "user@test.com");

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when different user has email")
        void shouldReturnFalseWhenDifferentUserHasEmail() {
            User existing = createUser(5L, "user@test.com", "pass");
            when(userRepository.findByEmail("user@test.com")).thenReturn(existing);

            Boolean result = userService.isEmailUnique(10L, "user@test.com");

            assertFalse(result);
        }

        @Test
        @DisplayName("Should handle null id for new user with existing email")
        void shouldHandleNullIdForNewUserWithExistingEmail() {
            User existing = createUser(5L, "taken@test.com", "pass");
            when(userRepository.findByEmail("taken@test.com")).thenReturn(existing);

            // For new user (id=null), isCreatingNew returns true
            // The code then checks `userByEmail == null` which is false, so returns false
            Boolean result = userService.isEmailUnique(null, "taken@test.com");

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("get tests")
    class GetTests {

        @Test
        @DisplayName("Should return user when found")
        void shouldReturnUserWhenFound() throws UserNotFoundException {
            User user = createUser(1L, "user@test.com", "pass");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            User result = userService.get(1L);

            assertEquals(user, result);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when not found")
        void shouldThrowUserNotFoundExceptionWhenNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.get(999L));
        }
    }

    @Nested
    @DisplayName("delete tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete via repository")
        void shouldDeleteViaRepository() throws UserNotFoundException {
            when(userRepository.countById(1L)).thenReturn(1L);

            userService.delete(1L);

            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when count is 0")
        void shouldThrowUserNotFoundExceptionWhenCountIsZero() {
            when(userRepository.countById(999L)).thenReturn(0L);

            assertThrows(UserNotFoundException.class, () -> userService.delete(999L));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when count is null")
        void shouldThrowUserNotFoundExceptionWhenCountIsNull() {
            when(userRepository.countById(999L)).thenReturn(null);

            assertThrows(UserNotFoundException.class, () -> userService.delete(999L));
        }
    }

    @Nested
    @DisplayName("updateUserEnabledStatus tests")
    class UpdateUserEnabledStatusTests {

        @Test
        @DisplayName("Should delegate to repository")
        void shouldDelegateToRepository() {
            userService.updateUserEnabledStatus(1L, true);

            verify(userRepository).updateEnabledStatus(1L, true);
        }
    }

    // Helper methods

    private User createUser(final Long id, final String email, final String password) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName("Test");
        user.setLastName("User");
        return user;
    }
}
