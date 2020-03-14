package com.quiz.darkhold.preview.model;

import com.quiz.darkhold.challenge.entity.QuestionSet;

import java.util.List;

public class PreviewInfo {
    private String challengeId;
    private String challengeName;
    private List<QuestionSet> questionSets;

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(final String challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(final String challengeName) {
        this.challengeName = challengeName;
    }

    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(final List<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }
}
