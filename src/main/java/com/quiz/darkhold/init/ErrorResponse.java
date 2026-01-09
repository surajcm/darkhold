package com.quiz.darkhold.init;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Standard error response format for API errors.
 *
 * @param code      Error code identifier
 * @param message   Human-readable error message
 * @param timestamp Time when the error occurred
 */
public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {
    /**
     * Creates an ErrorResponse with current timestamp.
     *
     * @param code    Error code identifier
     * @param message Human-readable error message
     */
    public ErrorResponse(final String code, final String message) {
        this(code, message, LocalDateTime.now(ZoneId.systemDefault()));
    }
}
