package com.quiz.darkhold.options.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.options.model.ChallengeInfo;
import com.quiz.darkhold.options.model.ChallengeSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptionsService {

    @Autowired
    private ChallengeRepository challengeRepository;

    public ChallengeInfo populateChallengeInfo() {
        ChallengeInfo challengeInfo = new ChallengeInfo();
        List<ChallengeSummary> summaries = new ArrayList<>();
        List<Challenge>  challenges = challengeRepository.findAll();
        for (Challenge challenge:challenges) {
            ChallengeSummary summary = new ChallengeSummary();
            summary.setChallengeId(challenge.getId().intValue());
            summary.setChallengeName(challenge.getTitle());
            summaries.add(summary);
        }
        challengeInfo.setChallengeSummaryList(summaries);
        return challengeInfo;
    }
}
