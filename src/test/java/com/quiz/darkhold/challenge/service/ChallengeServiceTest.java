package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.challenge.repository.QuestionSetRepository;
import com.quiz.darkhold.login.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ChallengeServiceTest {
    private final ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    private final QuestionSetRepository questionSetRepository = Mockito.mock(QuestionSetRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ChallengeService challengeService = new ChallengeService(challengeRepository,
            questionSetRepository,
            userRepository);


    @Test
    void readProcessAndSaveChallengeSuccess() throws IOException {
        var anyInputStream = new ByteArrayInputStream("test data".getBytes(UTF_8));
        var file = Mockito.mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(anyInputStream);
        Assertions.assertThrows(ChallengeException.class, () -> {
            challengeService.readProcessAndSaveChallenge(file, "Test1", "Super test");
        });
    }

    @Test
    void verifyReadProcessAndSaveChallengeWithException() {
        Assertions.assertThrows(ChallengeException.class, () -> {
            challengeService.readProcessAndSaveChallenge(mockMultipartFile(), "Test1", "Super test");
        });
    }

    private MultipartFile mockMultipartFile() {
        return new MockMultipartFile("mockfile.xls", "Q1, A1,A2,A3,A4,A1".getBytes(UTF_8));
    }

}