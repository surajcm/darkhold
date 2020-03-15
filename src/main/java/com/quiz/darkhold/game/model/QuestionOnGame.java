package com.quiz.darkhold.game.model;

public class QuestionOnGame {
    private int currentQuestionNumber;
    private String question;

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    public void setCurrentQuestionNumber(final int currentQuestionNumber) {
        this.currentQuestionNumber = currentQuestionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }
}
