package com.quiz.darkhold.game.model;

import com.quiz.darkhold.challenge.entity.QuestionSet;

public class QuestionPointer {
    private int currentQuestionNumber;
    private int totalQuestionCount;
    private String pin;
    private QuestionSet currentQuestion;

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    public void setCurrentQuestionNumber(final int currentQuestionNumber) {
        this.currentQuestionNumber = currentQuestionNumber;
    }

    public int getTotalQuestionCount() {
        return totalQuestionCount;
    }

    public void setTotalQuestionCount(final int totalQuestionCount) {
        this.totalQuestionCount = totalQuestionCount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(final String pin) {
        this.pin = pin;
    }

    public QuestionSet getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(final QuestionSet currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
