package com.quiz.darkhold.options.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.options.model.ChallengeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OptionsServiceTest {
    private final OptionsService optionsService = new OptionsService();
    private final ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);

    @BeforeEach
    public void setup() {

        Whitebox.setInternalState(optionsService, "challengeRepository", challengeRepository);
    }

    @Test
    public void populate() {
        when(challengeRepository.findAll()).thenReturn(mockChallenges());
        ChallengeInfo challengeInfo = optionsService.populateChallengeInfo();
        Assertions.assertNotNull(challengeInfo);
    }

    private List<Challenge> mockChallenges() {
        Challenge challenge1 = new Challenge();
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