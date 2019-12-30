package com.quiz.darkhold.challenge.service;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ChallengeServiceTest {
    private ChallengeService challengeService;

    @Test
    public void verifyReadProcessAndSaveChallenge(){
        try {
            challengeService.readProcessAndSaveChallenge(mockMultipartFile(),"Test1","Super test");
        } catch (ChallengeException e) {
            e.printStackTrace();
            //fail(e.getErrorMessage());
        }
    }

    private MultipartFile mockMultipartFile() {
        return new MockMultipartFile("mockfile.xls", "Q1, A1,A2,A3,A4,A1".getBytes());
    }

}