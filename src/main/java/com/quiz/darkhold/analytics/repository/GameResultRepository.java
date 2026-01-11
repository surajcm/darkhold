package com.quiz.darkhold.analytics.repository;

import com.quiz.darkhold.analytics.entity.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for accessing GameResult entities.
 */
@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    /**
     * Find all games by moderator, ordered by completion date descending.
     *
     * @param moderator the moderator username
     * @return list of game results
     */
    List<GameResult> findByModeratorOrderByCompletedAtDesc(String moderator);

    /**
     * Find all games for a specific challenge, ordered by completion date descending.
     *
     * @param challengeId the challenge ID
     * @return list of game results
     */
    List<GameResult> findByChallengeIdOrderByCompletedAtDesc(String challengeId);

    /**
     * Find games completed within a date range.
     *
     * @param startDate start of range
     * @param endDate   end of range
     * @return list of game results
     */
    List<GameResult> findByCompletedAtBetweenOrderByCompletedAtDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find games by moderator within a date range.
     *
     * @param moderator moderator username
     * @param startDate start of range
     * @param endDate   end of range
     * @return list of game results
     */
    List<GameResult> findByModeratorAndCompletedAtBetweenOrderByCompletedAtDesc(
            String moderator, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find game by PIN.
     *
     * @param pin game PIN
     * @return game result if found
     */
    GameResult findByPin(String pin);
}
