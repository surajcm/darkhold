package com.quiz.darkhold.game.service;

import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Scheduled service to clean up expired and abandoned games.
 * Runs every hour to find games that have been inactive for too long.
 */
@Service
public class GameCleanupScheduler {

    private static final Logger logger = LogManager.getLogger(GameCleanupScheduler.class);

    private final GameRepository gameRepository;
    private final CurrentGame currentGame;

    @Value("${darkhold.game.expiration-hours:24}")
    private int expirationHours;

    @Value("${darkhold.game.waiting-expiration-hours:2}")
    private int waitingExpirationHours;

    public GameCleanupScheduler(final GameRepository gameRepository,
                                final CurrentGame currentGame) {
        this.gameRepository = gameRepository;
        this.currentGame = currentGame;
    }

    /**
     * Scheduled task to clean up expired games.
     * Runs every hour (3600000 ms).
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 3600000)
    @Transactional
    public void cleanupExpiredGames() {
        logger.info("Starting scheduled game cleanup task");
        List<Game> activeGames = gameRepository.findByGameStatusNot(GameStatus.FINISHED.name());
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        int cleanedCount = processExpiredGames(activeGames, now);
        logCleanupResults(cleanedCount);
    }

    private int processExpiredGames(final List<Game> games, final LocalDateTime now) {
        int count = 0;
        for (Game game : games) {
            if (isGameExpired(game, now)) {
                cleanupSingleGame(game);
                count++;
            }
        }
        return count;
    }

    private boolean isGameExpired(final Game game, final LocalDateTime now) {
        if (game.getCreatedOn() == null) {
            return false;
        }
        LocalDateTime expirationTime = calculateExpirationTime(game);
        return now.isAfter(expirationTime);
    }

    private LocalDateTime calculateExpirationTime(final Game game) {
        if (GameStatus.WAITING.name().equals(game.getGameStatus())) {
            return game.getCreatedOn().plusHours(waitingExpirationHours);
        } else {
            return game.getCreatedOn().plusHours(expirationHours);
        }
    }

    private void cleanupSingleGame(final Game game) {
        String reason = GameStatus.WAITING.name().equals(game.getGameStatus())
                ? "WAITING game expired after " + waitingExpirationHours + " hours"
                : "Game expired after " + expirationHours + " hours";
        logger.info("Cleaning up expired game: PIN={}, Status={}, Created={}, Reason={}",
                game.getPin(), game.getGameStatus(), game.getCreatedOn(), reason);
        game.setGameStatus(GameStatus.FINISHED.name());
        gameRepository.save(game);
        currentGame.stopTheGame(game.getPin());
    }

    private void logCleanupResults(final int cleanedCount) {
        if (cleanedCount > 0) {
            logger.info("Game cleanup complete: {} game(s) cleaned up", cleanedCount);
        } else {
            logger.info("Game cleanup complete: No expired games found");
        }
    }

    /**
     * Manually trigger cleanup for a specific game PIN.
     *
     * @param pin game PIN
     * @return true if cleaned up, false otherwise
     */
    @Transactional
    public boolean cleanupGame(final String pin) {
        Game game = gameRepository.findByPin(pin);
        if (game == null || GameStatus.FINISHED.name().equals(game.getGameStatus())) {
            logger.info("Game not found or already finished: {}", pin);
            return false;
        }
        performGameCleanup(game, pin);
        return true;
    }

    private void performGameCleanup(final Game game, final String pin) {
        logger.info("Manually cleaning up game: {}", pin);
        game.setGameStatus(GameStatus.FINISHED.name());
        gameRepository.save(game);
        currentGame.stopTheGame(pin);
    }
}
