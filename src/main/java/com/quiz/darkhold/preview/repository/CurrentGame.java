package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.GameStatus;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.preview.entity.CurrentGameSession;
import com.quiz.darkhold.preview.model.PublishInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CurrentGame {

    private final Logger logger = LogManager.getLogger(CurrentGame.class);

    @Autowired
    private CurrentGameSessionRepository repository;

    /**
     * save the game info to H2 database before we start the game.
     *
     * @param publishInfo publish info
     */
    public void saveCurrentStatus(final PublishInfo publishInfo, final ArrayDeque<QuestionSet> questionSets) {
        List<String> users = new ArrayList<>();
        users.add(publishInfo.getModerator());

        CurrentGameSession session = new CurrentGameSession(publishInfo.getPin(), publishInfo.getModerator());
        session.setUsersList(users);
        session.setQuestionsList(new ArrayList<>(questionSets));
        session.setCurrentQuestionNo(0);
        session.setScoresMap(new HashMap<>());

        repository.save(session);
    }

    /**
     * get the active users in the game.
     *
     * @param pin of the game
     * @return users
     */
    public List<String> getActiveUsersInGame(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            List<String> users = session.get().getUsersList();
            logger.info("Participants are : {}", users);
            return users;
        }
        return new ArrayList<>();
    }

    /**
     * save user to active game.
     *
     * @param pin      of the game
     * @param userName of user
     */
    public void saveUserToActiveGame(final String pin, final String userName) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            List<String> users = gameSession.getUsersList();
            if (users == null) {
                users = new ArrayList<>();
            }
            users.add(userName);
            gameSession.setUsersList(users);
            repository.save(gameSession);
        }
    }

    /**
     * save questions to active game.
     *
     * @param pin          of the game
     * @param questionSets of the game
     */
    public void saveQuestionsToActiveGame(final String pin, final List<QuestionSet> questionSets) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            List<QuestionSet> questions = gameSession.getQuestionsList();
            if (questions == null) {
                questions = new ArrayList<>();
            }
            questions.addAll(questionSets);
            gameSession.setQuestionsList(questions);
            repository.save(gameSession);
        }
    }

    /**
     * current question no.
     *
     * @param pin of game
     * @return question no
     */
    public int getCurrentQuestionNo(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            Integer questionNo = session.get().getCurrentQuestionNo();
            logger.info("getCurrentQuestionNo : questionNo : {}}", questionNo);
            return questionNo != null ? questionNo : 0;
        }
        return 0;
    }

    /**
     * get all questions of the pin.
     *
     * @param pin of game
     * @return question list
     */
    public List<QuestionSet> getQuestionsOnAPin(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            List<QuestionSet> questions = session.get().getQuestionsList();
            if (questions != null) {
                logger.info("question count : {}", questions.size());
                return questions;
            }
        }
        return new ArrayList<>();
    }

    /**
     * points to the next question.
     *
     * @param pin of game
     */
    public void incrementQuestionCount(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Integer questionNo = gameSession.getCurrentQuestionNo();
            if (questionNo != null) {
                gameSession.setCurrentQuestionNo(questionNo + 1);
                repository.save(gameSession);
            }
        }
    }

    /**
     * find the moderator.
     *
     * @param pin pin
     * @return moderator
     */
    public String findModerator(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        return session.map(CurrentGameSession::getModerator).orElse(null);
    }

    /**
     * save current score to H2 database.
     *
     * @param pin    pin of game
     * @param name   of user
     * @param status success or not
     */
    public void saveCurrentScore(final String pin, final String name, final Integer status) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Map<String, Integer> scores = gameSession.getScoresMap();
            if (scores == null) {
                scores = new HashMap<>();
            }
            if (scores.containsKey(name)) {
                scores.compute(name, (k, currentValue) -> currentValue + status);
            } else {
                scores.put(name, status);
            }
            gameSession.setScoresMap(scores);
            repository.save(gameSession);
        }
    }

    public Map<String, Integer> getCurrentScore(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            Map<String, Integer> scores = session.get().getScoresMap();
            return scores != null ? scores : new HashMap<>();
        }
        return new HashMap<>();
    }

    public QuestionPointer getCurrentQuestionPointer(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        QuestionPointer questionPointer = new QuestionPointer();

        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Integer questionNo = gameSession.getCurrentQuestionNo();
            List<QuestionSet> questions = gameSession.getQuestionsList();

            if (questionNo != null) {
                logger.info("questionNo : {}", questionNo);
                if (questions != null) {
                    logger.info("questions size : {}", questions.size());
                    questionPointer.setCurrentQuestionNumber(questionNo);
                    questionPointer.setTotalQuestionCount(questions.size());
                    if (questionNo < questions.size()) {
                        questionPointer.setCurrentQuestion(questions.get(questionNo));
                    }
                }
            }
        }
        return questionPointer;
    }

    public void stopTheGame(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        session.ifPresent(currentGameSession -> repository.delete(currentGameSession));
    }

    // ==================== Streak Methods ====================

    /**
     * Update streak for a user. Increment if correct, reset if incorrect.
     *
     * @param pin       game pin
     * @param username  user name
     * @param isCorrect whether the answer was correct
     * @return the new streak value
     */
    public int updateStreak(final String pin, final String username, final boolean isCorrect) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Map<String, Integer> streaks = gameSession.getStreakMap();
            int currentStreak = streaks.getOrDefault(username, 0);
            int newStreak = isCorrect ? currentStreak + 1 : 0;
            streaks.put(username, newStreak);
            gameSession.setStreakMap(streaks);
            repository.save(gameSession);
            logger.info("Updated streak for {}: {} -> {}", username, currentStreak, newStreak);
            return newStreak;
        }
        return 0;
    }

    /**
     * Get current streak for a user.
     *
     * @param pin      game pin
     * @param username user name
     * @return current streak count
     */
    public int getStreak(final String pin, final String username) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            Map<String, Integer> streaks = session.get().getStreakMap();
            return streaks.getOrDefault(username, 0);
        }
        return 0;
    }

    /**
     * Save current scores as previous scores (for delta calculation).
     * Call this before starting a new question.
     *
     * @param pin game pin
     */
    public void savePreviousScores(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            Map<String, Integer> currentScores = gameSession.getScoresMap();
            if (currentScores != null) {
                gameSession.setPreviousScoresMap(new HashMap<>(currentScores));
                repository.save(gameSession);
            }
        }
    }

    /**
     * Get previous round scores for delta display.
     *
     * @param pin game pin
     * @return previous scores map
     */
    public Map<String, Integer> getPreviousScores(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            return session.get().getPreviousScoresMap();
        }
        return new HashMap<>();
    }

    // ==================== Game Status Methods ====================

    /**
     * Set the game status.
     *
     * @param pin    game pin
     * @param status new status
     */
    public void setGameStatus(final String pin, final GameStatus status) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            gameSession.setGameStatus(status);
            repository.save(gameSession);
            logger.info("Game {} status set to {}", pin, status);
        }
    }

    /**
     * Get the current game status.
     *
     * @param pin game pin
     * @return game status
     */
    public GameStatus getGameStatus(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            GameStatus status = session.get().getGameStatus();
            return status != null ? status : GameStatus.WAITING;
        }
        return GameStatus.WAITING;
    }

    /**
     * Pause the game.
     *
     * @param pin game pin
     * @return true if paused successfully
     */
    public boolean pauseGame(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            gameSession.setGameStatus(GameStatus.PAUSED);
            gameSession.setPausedAt(System.currentTimeMillis());
            repository.save(gameSession);
            logger.info("Game {} paused", pin);
            return true;
        }
        return false;
    }

    /**
     * Resume a paused game.
     *
     * @param pin game pin
     * @return elapsed pause time in milliseconds (for timer adjustment)
     */
    public long resumeGame(final String pin) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            gameSession.setGameStatus(GameStatus.ACTIVE);
            long elapsed = calculateElapsedPause(gameSession.getPausedAt());
            gameSession.setPausedAt(null);
            repository.save(gameSession);
            logger.info("Game {} resumed after {}ms pause", pin, elapsed);
            return elapsed;
        }
        return 0;
    }

    private long calculateElapsedPause(final Long pausedAt) {
        return pausedAt != null ? System.currentTimeMillis() - pausedAt : 0;
    }

    // ==================== Player Management Methods ====================

    /**
     * Remove a user from the game (kick functionality).
     *
     * @param pin      game pin
     * @param username user to remove
     * @return true if removed successfully
     */
    public boolean removeUserFromGame(final String pin, final String username) {
        Optional<CurrentGameSession> session = repository.findByPin(pin);
        if (session.isPresent()) {
            CurrentGameSession gameSession = session.get();
            List<String> users = gameSession.getUsersList();
            if (users != null && users.remove(username)) {
                gameSession.setUsersList(users);
                // Also remove from scores and streaks
                removeUserFromMaps(gameSession, username);
                repository.save(gameSession);
                logger.info("User {} removed from game {}", username, pin);
                return true;
            }
        }
        return false;
    }

    private void removeUserFromMaps(final CurrentGameSession session, final String username) {
        Map<String, Integer> scores = session.getScoresMap();
        if (scores != null) {
            scores.remove(username);
            session.setScoresMap(scores);
        }
        Map<String, Integer> streaks = session.getStreakMap();
        if (streaks != null) {
            streaks.remove(username);
            session.setStreakMap(streaks);
        }
    }

    /**
     * Get participant count.
     *
     * @param pin game pin
     * @return number of participants (excluding moderator)
     */
    public int getParticipantCount(final String pin) {
        List<String> users = getActiveUsersInGame(pin);
        String moderator = findModerator(pin);
        if (users == null) {
            return 0;
        }
        // Subtract 1 for moderator if present
        return moderator != null && users.contains(moderator) ? users.size() - 1 : users.size();
    }
}
