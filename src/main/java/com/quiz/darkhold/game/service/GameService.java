package com.quiz.darkhold.game.service;

import com.quiz.darkhold.game.model.GameMode;
import com.quiz.darkhold.game.model.GameStatus;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.game.model.ScoreResult;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

@Service
public class GameService {
    private final Logger logger = LogManager.getLogger(GameService.class);

    private final CurrentGame currentGame;

    private final PreviewService previewService;

    public GameService(final CurrentGame currentGame, final PreviewService previewService) {
        this.currentGame = currentGame;
        this.previewService = previewService;
    }

    public PublishInfo getActiveChallenge() {
        return previewService.getActiveChallenge();
    }

    //todo : this is needed !!!
    public List<String> saveAndGetAllParticipants(final String pin, final String userName) {
        currentGame.saveUserToActiveGame(pin, userName);
        return currentGame.getActiveUsersInGame(pin);
    }

    public List<String> getAllParticipants(final String pin) {
        return currentGame.getActiveUsersInGame(pin);
    }

    public QuestionPointer getCurrentQuestionPointer() {
        var pin = currentPin();
        logger.info("Current pin is {}", pin);
        return currentGame.getCurrentQuestionPointer(pin);
    }

    /**
     * Get current question pointer for a specific PIN (for WebSocket handlers).
     *
     * @param pin game PIN
     * @return current question pointer
     */
    public QuestionPointer getCurrentQuestionPointer(final String pin) {
        logger.info("Current pin is {}", pin);
        return currentGame.getCurrentQuestionPointer(pin);
    }

    public void incrementQuestionNo() {
        var pin = currentPin();
        currentGame.incrementQuestionCount(pin);
    }

    /**
     * save current score.
     *
     * @param name of user
     * @param status of user
     */
    public void saveCurrentScore(final String name, final Integer status) {
        var pin = currentPin();
        currentGame.saveCurrentScore(pin, name, status);
    }

    public String findModerator() {
        var pin = currentPin();
        return currentGame.findModerator(pin);
    }

    public Map<String, Integer> getCurrentScore() {
        var pin = currentPin();
        logger.info("current pin is {}", pin);
        return currentGame.getCurrentScore(pin);
    }

    /**
     * Get current game PIN from HTTP session.
     * This enables support for multiple concurrent games.
     *
     * @return game PIN from session, or fallback to legacy single-game lookup
     */
    private String currentPin() {
        // Try to get PIN from HTTP session first (supports concurrent games)
        String sessionPin = getSessionPin();
        if (sessionPin != null) {
            logger.debug("Using PIN from session: {}", sessionPin);
            return sessionPin;
        }
        // Fallback to legacy single-game behavior for backwards compatibility
        logger.warn("No PIN in session, falling back to getActiveChallenge()");
        var publishInfo = previewService.getActiveChallenge();
        return publishInfo.getPin();
    }

    /**
     * Get game PIN from current HTTP session.
     *
     * @return PIN from session, or null if not in HTTP context
     */
    private String getSessionPin() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        HttpSession session = request.getSession(false);
        return session != null ? (String) session.getAttribute("gamePin") : null;
    }

    public void cleanUpCurrentGame() {
        var pin = currentPin();
        logger.info("Cleaning up game: {}", pin);
        // make it inactive
        // save scores to a db
        currentGame.stopTheGame(pin);
    }

    // ==================== Streak Methods ====================

    /**
     * Update user streak and return the new streak value.
     *
     * @param name      username
     * @param isCorrect whether the answer was correct
     * @return new streak value
     */
    public int updateStreak(final String name, final boolean isCorrect) {
        var pin = currentPin();
        return currentGame.updateStreak(pin, name, isCorrect);
    }

    /**
     * Get current streak for a user.
     *
     * @param name username
     * @return streak count
     */
    public int getStreak(final String name) {
        var pin = currentPin();
        return currentGame.getStreak(pin, name);
    }

    /**
     * Save current scores before starting a new question.
     */
    public void savePreviousScores() {
        var pin = currentPin();
        currentGame.savePreviousScores(pin);
    }

    /**
     * Get previous scores for delta calculation.
     *
     * @return previous scores map
     */
    public Map<String, Integer> getPreviousScores() {
        var pin = currentPin();
        return currentGame.getPreviousScores(pin);
    }

    // ==================== Game Control Methods ====================

    /**
     * Set game status.
     *
     * @param status new status
     */
    public void setGameStatus(final GameStatus status) {
        var pin = currentPin();
        currentGame.setGameStatus(pin, status);
    }

    /**
     * Get current game status.
     *
     * @return game status
     */
    public GameStatus getGameStatus() {
        var pin = currentPin();
        return currentGame.getGameStatus(pin);
    }

    /**
     * Pause the current game.
     *
     * @return true if paused
     */
    public boolean pauseGame() {
        var pin = currentPin();
        return currentGame.pauseGame(pin);
    }

    /**
     * Resume the current game.
     *
     * @return elapsed pause time in milliseconds
     */
    public long resumeGame() {
        var pin = currentPin();
        return currentGame.resumeGame(pin);
    }

    /**
     * Skip to the next question.
     */
    public void skipQuestion() {
        var pin = currentPin();
        currentGame.incrementQuestionCount(pin);
    }

    // ==================== Player Management Methods ====================

    /**
     * Remove a player from the game (kick).
     *
     * @param username player to remove
     * @return true if removed
     */
    public boolean kickPlayer(final String username) {
        var pin = currentPin();
        return currentGame.removeUserFromGame(pin, username);
    }

    /**
     * Get participant count (excluding moderator).
     *
     * @return participant count
     */
    public int getParticipantCount() {
        var pin = currentPin();
        return currentGame.getParticipantCount(pin);
    }

    /**
     * Calculate score with streak multiplier.
     *
     * @param basePoints   base points for correct answer
     * @param timeFactor   time-based factor (0-1000)
     * @param streak       current streak count
     * @return final score with multiplier applied
     */
    public int calculateScoreWithStreak(final int basePoints, final int timeFactor, final int streak) {
        int multiplier = ScoreResult.calculateStreakMultiplier(streak);
        int finalScore = (basePoints * timeFactor * multiplier) / 1000;
        logger.info("Score: base={}, time={}, streak={}, mult={}, final={}",
                basePoints, timeFactor, streak, multiplier, finalScore);
        return finalScore;
    }

    /**
     * Get the game mode for the current game.
     *
     * @return GameMode (MULTIPLAYER or PRACTICE)
     */
    public GameMode getGameMode() {
        var pin = currentPin();
        return currentGame.getGameMode(pin);
    }

    /**
     * Get the game mode for a specific PIN (for use when PIN is explicitly available).
     *
     * @param pin game PIN
     * @return GameMode (MULTIPLAYER or PRACTICE)
     */
    public GameMode getGameMode(final String pin) {
        return currentGame.getGameMode(pin);
    }

    /**
     * Check if current game is in practice mode.
     *
     * @return true if practice mode, false otherwise
     */
    public boolean isPracticeMode() {
        return getGameMode() == GameMode.PRACTICE;
    }
}
