package com.quiz.darkhold.init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Test
    @DisplayName("Should create with code and message")
    void shouldCreateWithCodeAndMessage() {
        var response = new ErrorResponse("TEST_CODE", "Test message");

        assertEquals("TEST_CODE", response.code());
        assertEquals("Test message", response.message());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Should create with full constructor")
    void shouldCreateWithFullConstructor() {
        var timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);
        var response = new ErrorResponse("CODE", "Message", timestamp);

        assertEquals("CODE", response.code());
        assertEquals("Message", response.message());
        assertEquals(timestamp, response.timestamp());
    }

    @Test
    @DisplayName("Should handle null message")
    void shouldHandleNullMessage() {
        var response = new ErrorResponse("CODE", null);

        assertEquals("CODE", response.code());
        assertEquals(null, response.message());
    }

    @Test
    @DisplayName("Should have timestamp set automatically")
    void shouldHaveTimestampSetAutomatically() {
        var response = new ErrorResponse("CODE", "Message");

        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Record equality should work")
    void shouldSupportEquality() {
        var timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);
        var response1 = new ErrorResponse("CODE", "Message", timestamp);
        var response2 = new ErrorResponse("CODE", "Message", timestamp);

        assertEquals(response1, response2);
    }
}
