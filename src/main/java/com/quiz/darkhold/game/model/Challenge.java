package com.quiz.darkhold.game.model;

import com.quiz.darkhold.challenge.entity.QuestionSet;

public class Challenge {
    private int questionNumber;
    private QuestionSet questionSet;

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(final int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public QuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(final QuestionSet questionSet) {
        this.questionSet = questionSet;
    }
}
