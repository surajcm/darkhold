package com.quiz.darkhold.preview.model;

import com.quiz.darkhold.challenge.entity.QuestionSet;

import java.util.List;

public class PreviewInfo {
    String challengeName;
    List<QuestionSet> questionSets;

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(List<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }
}
