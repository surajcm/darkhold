package com.quiz.darkhold.options.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OptionsServiceTest {
    private final OptionsService optionsService = new OptionsService();
    private final ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    public void setup() {
        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("admin12345");
        SecurityContextHolder.setContext(securityContext);
        Whitebox.setInternalState(optionsService, "challengeRepository", challengeRepository);
        Whitebox.setInternalState(optionsService, "userRepository", userRepository);
    }

    @Test
    void populate() {
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser());
        when(challengeRepository.findAll()).thenReturn(mockChallenges());
        var challengeInfo = optionsService.populateChallengeInfo();
        Assertions.assertNotNull(challengeInfo);
    }

    private User mockUser() {
        var user = new User();
        user.setId(1L);
        user.setUsername("admin12345");
        return user;
    }

    private List<Challenge> mockChallenges() {
        var challenge1 = new Challenge();
        challenge1.setId(1234L);
        challenge1.setTitle("Hello");
        Challenge challenge2 = new Challenge();
        challenge2.setId(1235L);
        challenge2.setTitle("Hello1");
        List<Challenge> challenges = new ArrayList<>();
        challenges.add(challenge1);
        challenges.add(challenge2);
        return challenges;
    }
}