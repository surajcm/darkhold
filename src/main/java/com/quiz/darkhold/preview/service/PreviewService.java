package com.quiz.darkhold.preview.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.preview.model.PreviewInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PreviewService {

    @Autowired
    private ChallengeRepository challengeRepository;

    public PreviewInfo fetchQuestions(String challengeId) {
        PreviewInfo previewInfo = new PreviewInfo();
        Long challengeOne = Long.valueOf(challengeId);
        Challenge challenge = challengeRepository.getOne(challengeOne);
        previewInfo.setQuestionSets(new ArrayList<>(challenge.getQuestionSets()));
        previewInfo.setChallengeName(challenge.getTitle());
        return previewInfo;
    }

}
