package com.quiz.darkhold.preview.model;

import com.quiz.darkhold.challenge.entity.QuestionSet;

import java.util.Set;

public class PreviewInfo {
    String challengeId;
    String challengeName;
    Set<QuestionSet> questionSets;

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public Set<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(Set<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }
}
