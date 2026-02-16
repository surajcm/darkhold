package com.quiz.darkhold.home.model;

/**
 * Response object for PIN validation requests.
 * Provides detailed feedback about validation status and rate limiting.
 */
public class PinValidationResponse {
    private boolean valid;
    private String status; // "VALID", "INVALID", "RATE_LIMITED", "BLOCKED"
    private Integer remainingAttempts;
    private String message;

    /**
     * Default constructor required by Jackson for JSON deserialization.
     */
    public PinValidationResponse() {
    }

    public PinValidationResponse(final boolean valid, final String status) {
        this.valid = valid;
        this.status = status;
    }

    public PinValidationResponse(final boolean valid, final String status, final Integer remainingAttempts) {
        this.valid = valid;
        this.status = status;
        this.remainingAttempts = remainingAttempts;
    }

    public PinValidationResponse(final boolean valid, final String status,
                                 final Integer remainingAttempts, final String message) {
        this.valid = valid;
        this.status = status;
        this.remainingAttempts = remainingAttempts;
        this.message = message;
    }

    public static PinValidationResponse validPin() {
        return new PinValidationResponse(true, "VALID");
    }

    public static PinValidationResponse invalidPin(final int remainingAttempts) {
        return new PinValidationResponse(false, "INVALID", remainingAttempts);
    }

    public static PinValidationResponse rateLimited(final int remainingAttempts) {
        return new PinValidationResponse(false, "RATE_LIMITED", remainingAttempts);
    }

    public static PinValidationResponse blocked() {
        return new PinValidationResponse(false, "BLOCKED", 0);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(final boolean valid) {
        this.valid = valid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Integer getRemainingAttempts() {
        return remainingAttempts;
    }

    public void setRemainingAttempts(final Integer remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
