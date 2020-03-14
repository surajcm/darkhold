package com.quiz.darkhold.options.model;

import java.util.List;

public class ChallengeInfo {
    private List<ChallengeSummary> challengeSummaryList;

    public List<ChallengeSummary> getChallengeSummaryList() {
        return challengeSummaryList;
    }

    public void setChallengeSummaryList(final List<ChallengeSummary> challengeSummaryList) {
        this.challengeSummaryList = challengeSummaryList;
    }
}
