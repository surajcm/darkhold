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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserDetailsServiceImplTest {
    private final UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    public void setup() {
        Whitebox.setInternalState(userDetailsService, "userRepository", userRepository);
    }

    @Test
    public void loadUserByNullUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("admin"));
    }

    @Test
    public void loadUserByValidUsername() {
        String userName = "admin";
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser(userName));
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
        Assertions.assertEquals(userName, userDetails.getUsername());
    }

    private User mockUser(final String userName) {
        User user = new User();
        user.setUsername(userName);
        user.setRoles(mockRoles());
        user.setPassword("pass");
        return user;
    }

    private Set<Role> mockRoles() {
        Role role = new Role();
        role.setName("admin");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        return roles;
    }
}