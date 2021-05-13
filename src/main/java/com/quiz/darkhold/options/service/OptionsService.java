package com.quiz.darkhold.options.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.options.model.ChallengeInfo;
import com.quiz.darkhold.options.model.ChallengeSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionsService {

    @Autowired
    private ChallengeRepository challengeRepository;

    /**
     * get all challenges and display it there.
     * @return challenges
     */
    public ChallengeInfo populateChallengeInfo() {
        var challengeInfo = new ChallengeInfo();
        List<ChallengeSummary> summaries;
        var challenges = challengeRepository.findAll();
        summaries = challenges.stream()
                .map(this::getChallengeSummary).collect(Collectors.toList());
        challengeInfo.setChallengeSummaryList(summaries);
        return challengeInfo;
    }

    private ChallengeSummary getChallengeSummary(final Challenge challenge) {
        var summary = new ChallengeSummary();
        summary.setChallengeId(challenge.getId().intValue());
        summary.setChallengeName(challenge.getTitle());
        return summary;
    }
}
