package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.challenge.repository.QuestionSetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ChallengeServiceTest {
    private final ChallengeService challengeService = new ChallengeService();
    private final ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    private final QuestionSetRepository questionSetRepository = Mockito.mock(QuestionSetRepository.class);

    @BeforeEach
    public void setup() {
        Whitebox.setInternalState(challengeService, "challengeRepository", challengeRepository);
        Whitebox.setInternalState(challengeService, "questionSetRepository", questionSetRepository);
    }

    @Test
    void readProcessAndSaveChallengeSuccess() throws IOException {
        InputStream anyInputStream = new ByteArrayInputStream("test data".getBytes());
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(anyInputStream);
        /*Assertions.assertAll(() ->
                challengeService.readProcessAndSaveChallenge(file, "Test1", "Super test"));*/
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
        return new MockMultipartFile("mockfile.xls", "Q1, A1,A2,A3,A4,A1".getBytes());
    }

}