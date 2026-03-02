package com.quiz.darkhold.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RateLimitingService Tests")
class RateLimitingServiceTest {

    private RateLimitingService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new RateLimitingService();
        setField("maxAttempts", 5);
        setField("windowSeconds", 300);
        setField("blockDurationSeconds", 900);
    }

    private void setField(final String fieldName, final int value) throws Exception {
        Field field = RateLimitingService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setInt(service, value);
    }

    @Test
    @DisplayName("Should allow new IP address")
    void shouldAllowNewIpAddress() {
        assertTrue(service.isAllowed("192.168.1.1"));
    }

    @Test
    @DisplayName("Should allow IP within attempt limit")
    void shouldAllowIpWithinAttemptLimit() {
        service.isAllowed("192.168.1.2");
        recordMultipleFailures("192.168.1.2", 4);
        assertTrue(service.isAllowed("192.168.1.2"));
    }

    @Test
    @DisplayName("Should block IP after max attempts")
    void shouldBlockIpAfterMaxAttempts() {
        service.isAllowed("192.168.1.3");
        recordMultipleFailures("192.168.1.3", 5);
        assertFalse(service.isAllowed("192.168.1.3"));
    }

    @Test
    @DisplayName("Should allow IP after block expires")
    void shouldAllowIpAfterBlockExpires() throws Exception {
        service.isAllowed("192.168.1.4");
        recordMultipleFailures("192.168.1.4", 5);
        assertFalse(service.isAllowed("192.168.1.4"));
        expireBlock("192.168.1.4");
        assertTrue(service.isAllowed("192.168.1.4"));
    }

    @Test
    @DisplayName("Should increment attempt count on failure")
    void shouldIncrementAttemptCountOnFailure() {
        service.isAllowed("192.168.1.5");
        service.recordFailedAttempt("192.168.1.5");
        assertEquals(4, service.getRemainingAttempts("192.168.1.5"));
    }

    @Test
    @DisplayName("Should block at threshold of 5 attempts")
    void shouldBlockAtThresholdOfFiveAttempts() {
        service.isAllowed("192.168.1.6");
        recordMultipleFailures("192.168.1.6", 5);
        assertTrue(service.isBlocked("192.168.1.6"));
    }

    @Test
    @DisplayName("Should not block below threshold")
    void shouldNotBlockBelowThreshold() {
        service.isAllowed("192.168.1.7");
        recordMultipleFailures("192.168.1.7", 4);
        assertFalse(service.isBlocked("192.168.1.7"));
    }

    @Test
    @DisplayName("Should reset counter on successful attempt")
    void shouldResetCounterOnSuccessfulAttempt() {
        service.isAllowed("192.168.1.8");
        recordMultipleFailures("192.168.1.8", 3);
        service.recordSuccessfulAttempt("192.168.1.8");
        assertEquals(5, service.getRemainingAttempts("192.168.1.8"));
    }

    @Test
    @DisplayName("Should handle success for unknown IP gracefully")
    void shouldHandleSuccessForUnknownIp() {
        service.recordSuccessfulAttempt("10.0.0.1");
        assertEquals(5, service.getRemainingAttempts("10.0.0.1"));
    }

    @Test
    @DisplayName("Should return max attempts for unknown IP")
    void shouldReturnMaxAttemptsForUnknownIp() {
        assertEquals(5, service.getRemainingAttempts("10.0.0.2"));
    }

    @Test
    @DisplayName("Should return correct remaining attempts")
    void shouldReturnCorrectRemainingAttempts() {
        service.isAllowed("192.168.1.9");
        recordMultipleFailures("192.168.1.9", 3);
        assertEquals(2, service.getRemainingAttempts("192.168.1.9"));
    }

    @Test
    @DisplayName("Should return zero remaining when at max")
    void shouldReturnZeroRemainingWhenAtMax() {
        service.isAllowed("192.168.2.1");
        recordMultipleFailures("192.168.2.1", 5);
        assertEquals(0, service.getRemainingAttempts("192.168.2.1"));
    }

    @Test
    @DisplayName("Should report unknown IP as not blocked")
    void shouldReportUnknownIpAsNotBlocked() {
        assertFalse(service.isBlocked("10.0.0.3"));
    }

    @Test
    @DisplayName("Should report blocked IP correctly")
    void shouldReportBlockedIpCorrectly() {
        service.isAllowed("192.168.2.2");
        recordMultipleFailures("192.168.2.2", 5);
        assertTrue(service.isBlocked("192.168.2.2"));
    }

    @Test
    @DisplayName("Should report expired block as not blocked")
    void shouldReportExpiredBlockAsNotBlocked() throws Exception {
        service.isAllowed("192.168.2.3");
        recordMultipleFailures("192.168.2.3", 5);
        expireBlock("192.168.2.3");
        assertFalse(service.isBlocked("192.168.2.3"));
    }

    @Test
    @DisplayName("Should cleanup expired entries")
    void shouldCleanupExpiredEntries() throws Exception {
        service.isAllowed("192.168.3.1");
        expireWindow("192.168.3.1");
        service.cleanupExpiredEntries();
        assertEquals(5, service.getRemainingAttempts("192.168.3.1"));
    }

    @Test
    @DisplayName("Should preserve active entries during cleanup")
    void shouldPreserveActiveDuringCleanup() {
        service.isAllowed("192.168.3.2");
        service.recordFailedAttempt("192.168.3.2");
        service.cleanupExpiredEntries();
        assertEquals(4, service.getRemainingAttempts("192.168.3.2"));
    }

    @Test
    @DisplayName("Should allow after window expires and resets")
    void shouldAllowAfterWindowExpiresAndResets() throws Exception {
        service.isAllowed("192.168.3.3");
        recordMultipleFailures("192.168.3.3", 3);
        expireWindow("192.168.3.3");
        assertTrue(service.isAllowed("192.168.3.3"));
    }

    @Test
    @DisplayName("Should generate unique trackers per IP")
    void shouldGenerateUniqueTrackersPerIp() {
        service.isAllowed("192.168.4.1");
        service.isAllowed("192.168.4.2");
        service.recordFailedAttempt("192.168.4.1");
        assertEquals(4, service.getRemainingAttempts("192.168.4.1"));
        assertEquals(5, service.getRemainingAttempts("192.168.4.2"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getAttemptMap() throws Exception {
        Field field = RateLimitingService.class.getDeclaredField("attemptMap");
        field.setAccessible(true);
        return (Map<String, Object>) field.get(service);
    }

    private void expireBlock(final String ipAddress) throws Exception {
        Object tracker = getAttemptMap().get(ipAddress);
        Field blockedField = tracker.getClass().getDeclaredField("blockedUntil");
        blockedField.setAccessible(true);
        blockedField.set(tracker, Instant.now().minusSeconds(1));
    }

    private void expireWindow(final String ipAddress) throws Exception {
        Object tracker = getAttemptMap().get(ipAddress);
        Field windowField = tracker.getClass().getDeclaredField("windowStart");
        windowField.setAccessible(true);
        windowField.set(tracker, Instant.now().minusSeconds(301));
    }

    private void recordMultipleFailures(final String ipAddress, final int count) {
        for (int i = 0; i < count; i++) {
            service.recordFailedAttempt(ipAddress);
        }
    }
}
