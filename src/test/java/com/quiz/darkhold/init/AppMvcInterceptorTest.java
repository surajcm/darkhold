package com.quiz.darkhold.init;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@DisplayName("AppMvcInterceptor Tests")
class AppMvcInterceptorTest {

    private AppMvcInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new AppMvcInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("Should always return true from preHandle")
    void shouldAlwaysReturnTrueFromPreHandle() throws Exception {
        var result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    @DisplayName("Should set transactionId in MDC")
    void shouldSetTransactionIdInMdc() throws Exception {
        interceptor.preHandle(request, response, new Object());
        assertNotNull(MDC.get("transactionId"));
    }

    @Test
    @DisplayName("Should generate valid UUID format")
    void shouldGenerateValidUuidFormat() throws Exception {
        interceptor.preHandle(request, response, new Object());
        var transactionId = MDC.get("transactionId");
        var uuidPattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
        assertTrue(transactionId.matches(uuidPattern));
    }

    @Test
    @DisplayName("Should generate unique ID for each call")
    void shouldGenerateUniqueIdForEachCall() throws Exception {
        interceptor.preHandle(request, response, new Object());
        var firstId = MDC.get("transactionId");
        interceptor.preHandle(request, response, new Object());
        var secondId = MDC.get("transactionId");
        assertNotEquals(firstId, secondId);
    }
}
