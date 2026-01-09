package com.quiz.darkhold.init;

import com.quiz.darkhold.challenge.exception.ChallengeException;
import com.quiz.darkhold.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ChallengeException")
    void shouldHandleChallengeException() {
        var exception = new ChallengeException("Test error message");

        var response = handler.handleChallengeException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CHALLENGE_ERROR", response.getBody().code());
        assertEquals(exception.getErrorMessage(), response.getBody().message());
    }

    @Test
    @DisplayName("Should handle UserNotFoundException")
    void shouldHandleUserNotFoundException() throws UserNotFoundException {
        var exception = new UserNotFoundException("User not found");

        var response = handler.handleUserNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER_NOT_FOUND", response.getBody().code());
    }

    @Test
    @DisplayName("Should handle validation exception")
    void shouldHandleValidationException() {
        var bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("object", "field", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
    }

    @Test
    @DisplayName("Should handle MaxUploadSizeExceededException")
    void shouldHandleMaxUploadSizeExceeded() {
        var exception = new MaxUploadSizeExceededException(1024L);

        var response = handler.handleMaxUploadSizeExceeded(exception);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FILE_TOO_LARGE", response.getBody().code());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgument() {
        var exception = new IllegalArgumentException("Invalid argument");

        var response = handler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_ARGUMENT", response.getBody().code());
        assertEquals("Invalid argument", response.getBody().message());
    }

    @Test
    @DisplayName("Should handle generic exception")
    void shouldHandleGenericException() {
        var exception = new RuntimeException("Unexpected error");

        var response = handler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().code());
    }
}
