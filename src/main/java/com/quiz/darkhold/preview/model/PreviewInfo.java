package com.quiz.darkhold.preview.model;

import com.quiz.darkhold.challenge.entity.QuestionSet;

import java.util.Deque;

public class PreviewInfo {
    private String challengeId;
    private String challengeName;
    private Deque<QuestionSet> questionSets;

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

    public Deque<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(final Deque<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }
}
