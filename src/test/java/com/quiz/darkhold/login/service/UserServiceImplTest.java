package com.quiz.darkhold.login.service;

import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.repository.RoleRepository;
import com.quiz.darkhold.user.repository.UserRepository;
import com.quiz.darkhold.user.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private final BCryptPasswordEncoder encoder = Mockito.mock(BCryptPasswordEncoder.class);
    private final UserServiceImpl userService = new UserServiceImpl(userRepository, roleRepository, encoder);

    @Test
    void save() {
        when(encoder.encode(anyString())).thenReturn("MockedPassword");
        when(roleRepository.findAll()).thenReturn(mockRoles());
        var user = new User();
        Assertions.assertAll(() -> userService.save(user));
    }

    @Test
    void findByUsername() {
        var username = "superman";
        when(userRepository.findByEmail(anyString())).thenReturn(mockUser(username));
        var user = userService.findByUsername(username);
        Assertions.assertEquals(username, user.getFirstName());
    }

    private List<Role> mockRoles() {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role());
        return roles;
    }

    private User mockUser(final String userName) {
        var user = new User();
        user.setFirstName(userName);
        return user;
    }
}