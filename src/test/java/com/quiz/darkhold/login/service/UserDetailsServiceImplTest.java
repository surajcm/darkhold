package com.quiz.darkhold.login.service;

import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.repository.UserRepository;
import com.quiz.darkhold.user.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserDetailsServiceImplTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);

    @Test
    void loadUserByNullUsername() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("admin"));
    }

    @Test
    void loadUserByValidUsername() {
        var userName = "admin";
        when(userRepository.findByEmail(anyString())).thenReturn(mockUser(userName));
        var userDetails = userDetailsService.loadUserByUsername("admin");
        Assertions.assertEquals(userName, userDetails.getUsername());
    }

    private User mockUser(final String userName) {
        var user = new User();
        user.setFirstName(userName);
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