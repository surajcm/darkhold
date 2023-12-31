package com.quiz.darkhold.preview.model;

import com.quiz.darkhold.challenge.entity.QuestionSet;

import java.util.ArrayDeque;

public class PreviewInfo {
    private String challengeId;
    private String challengeName;
    private ArrayDeque<QuestionSet> questionSets;

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

    public ArrayDeque<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(final ArrayDeque<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }
}
