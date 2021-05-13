package com.quiz.darkhold.login.service;

import com.quiz.darkhold.login.entity.Role;
import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserDetailsServiceImplTest {
    private final UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    public void setup() {
        Whitebox.setInternalState(userDetailsService, "userRepository", userRepository);
    }

    @Test
    void loadUserByNullUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("admin"));
    }

    @Test
    void loadUserByValidUsername() {
        var userName = "admin";
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser(userName));
        var userDetails = userDetailsService.loadUserByUsername("admin");
        Assertions.assertEquals(userName, userDetails.getUsername());
    }

    private User mockUser(final String userName) {
        var user = new User();
        user.setUsername(userName);
        user.setRoles(mockRoles());
        user.setPassword("pass");
        return user;
    }

    private Set<Role> mockRoles() {
        var role = new Role();
        role.setName("admin");
        return Set.of(role);
    }
}