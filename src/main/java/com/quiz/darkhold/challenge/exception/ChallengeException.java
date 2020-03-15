package com.quiz.darkhold.challenge.exception;

public class ChallengeException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String errorMessage;

    public ChallengeException(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
