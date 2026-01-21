package com.quiz.darkhold.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting service to prevent brute-force attacks on PIN entry.
 * Tracks failed attempts by IP address and blocks excessive requests.
 */
@Service
public class RateLimitingService {
    private static final Logger logger = LogManager.getLogger(RateLimitingService.class);

    @Value("${darkhold.security.rate-limit.max-attempts:5}")
    private int maxAttempts;

    @Value("${darkhold.security.rate-limit.window-seconds:300}")
    private int windowSeconds;

    @Value("${darkhold.security.rate-limit.block-duration-seconds:900}")
    private int blockDurationSeconds;

    // Map of IP address to attempt tracker
    private final Map<String, AttemptTracker> attemptMap = new ConcurrentHashMap<>();

    /**
     * Check if a request from the given IP should be allowed.
     *
     * @param ipAddress The IP address of the requester
     * @return true if the request is allowed, false if rate limited
     */
    public boolean isAllowed(final String ipAddress) {
        final var now = Instant.now();
        final var tracker = attemptMap.computeIfAbsent(ipAddress, k -> new AttemptTracker());

        synchronized (tracker) {
            if (isCurrentlyBlocked(tracker, now, ipAddress)) {
                return false;
            }
            resetIfWindowExpired(tracker, now, ipAddress);
            initializeWindowIfNeeded(tracker, now);
            return true;
        }
    }

    private boolean isCurrentlyBlocked(final AttemptTracker tracker, final Instant now, final String ipAddress) {
        if (tracker.blockedUntil != null && now.isBefore(tracker.blockedUntil)) {
            logger.warn("Rate limit: IP {} is blocked until {}", ipAddress, tracker.blockedUntil);
            return true;
        }
        return false;
    }

    private void resetIfWindowExpired(final AttemptTracker tracker, final Instant now, final String ipAddress) {
        if (tracker.windowStart != null && now.isAfter(tracker.windowStart.plusSeconds(windowSeconds))) {
            logger.debug("Rate limit: Resetting attempt window for IP {}", ipAddress);
            tracker.reset();
        }
    }

    private void initializeWindowIfNeeded(final AttemptTracker tracker, final Instant now) {
        if (tracker.windowStart == null) {
            tracker.windowStart = now;
        }
    }

    /**
     * Record a failed attempt for the given IP address.
     * If max attempts exceeded, the IP will be blocked.
     *
     * @param ipAddress The IP address of the requester
     */
    public void recordFailedAttempt(final String ipAddress) {
        final var now = Instant.now();
        final var tracker = attemptMap.computeIfAbsent(ipAddress, k -> new AttemptTracker());

        synchronized (tracker) {
            tracker.attemptCount++;
            logger.info("Rate limit: IP {} failed attempt {}/{}", ipAddress, tracker.attemptCount, maxAttempts);

            if (tracker.attemptCount >= maxAttempts) {
                tracker.blockedUntil = now.plusSeconds(blockDurationSeconds);
                logger.warn("Rate limit: IP {} BLOCKED until {} (exceeded {} attempts)",
                        ipAddress, tracker.blockedUntil, maxAttempts);
            }
        }
    }

    /**
     * Record a successful attempt for the given IP address.
     * Resets the attempt counter.
     *
     * @param ipAddress The IP address of the requester
     */
    public void recordSuccessfulAttempt(final String ipAddress) {
        final var tracker = attemptMap.get(ipAddress);
        if (tracker != null) {
            synchronized (tracker) {
                logger.debug("Rate limit: IP {} successful attempt, resetting counter", ipAddress);
                tracker.reset();
            }
        }
    }

    /**
     * Get the remaining attempts for an IP address.
     *
     * @param ipAddress The IP address
     * @return Number of remaining attempts
     */
    public int getRemainingAttempts(final String ipAddress) {
        final var tracker = attemptMap.get(ipAddress);
        if (tracker == null) {
            return maxAttempts;
        }
        synchronized (tracker) {
            return Math.max(0, maxAttempts - tracker.attemptCount);
        }
    }

    /**
     * Check if an IP is currently blocked.
     *
     * @param ipAddress The IP address
     * @return true if blocked
     */
    public boolean isBlocked(final String ipAddress) {
        final var tracker = attemptMap.get(ipAddress);
        if (tracker == null) {
            return false;
        }
        synchronized (tracker) {
            return tracker.blockedUntil != null && Instant.now().isBefore(tracker.blockedUntil);
        }
    }

    /**
     * Cleanup expired entries every 15 minutes.
     */
    @Scheduled(fixedDelay = 900000) // 15 minutes
    public void cleanupExpiredEntries() {
        final var now = Instant.now();
        final var sizeBefore = attemptMap.size();

        attemptMap.entrySet().removeIf(entry -> isEntryExpired(entry.getValue(), now));

        logCleanupResults(sizeBefore, attemptMap.size());
    }

    private boolean isEntryExpired(final AttemptTracker tracker, final Instant now) {
        synchronized (tracker) {
            final var blockExpired = tracker.blockedUntil == null || now.isAfter(tracker.blockedUntil);
            final var windowExpired = tracker.windowStart == null
                    || now.isAfter(tracker.windowStart.plusSeconds(windowSeconds));
            return blockExpired && windowExpired;
        }
    }

    private void logCleanupResults(final int sizeBefore, final int sizeAfter) {
        if (sizeBefore != sizeAfter) {
            logger.info("Rate limit: Cleaned up {} expired entries ({} -> {})",
                    sizeBefore - sizeAfter, sizeBefore, sizeAfter);
        }
    }

    /**
     * Tracks attempts and blocking status for a single IP address.
     */
    private static class AttemptTracker {
        private int attemptCount = 0;
        private Instant windowStart;
        private Instant blockedUntil;

        void reset() {
            attemptCount = 0;
            windowStart = null;
            blockedUntil = null;
        }
    }
}
