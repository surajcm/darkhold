package com.quiz.darkhold.challenge.exception;

public class ImageValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String errorMessage;

    public ImageValidationException(final String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
