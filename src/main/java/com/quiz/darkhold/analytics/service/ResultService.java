package com.quiz.darkhold.analytics.service;

import com.quiz.darkhold.analytics.entity.GameResult;
import com.quiz.darkhold.analytics.entity.ParticipantResult;
import com.quiz.darkhold.analytics.entity.QuestionResult;
import com.quiz.darkhold.analytics.repository.GameResultRepository;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for saving and retrieving game analytics and results.
 */
@Service
public class ResultService {

    private static final Logger logger = LogManager.getLogger(ResultService.class);

    private final GameResultRepository gameResultRepository;
    private final GameRepository gameRepository;
    private final CurrentGame currentGame;

    public ResultService(final GameResultRepository gameResultRepository,
                         final GameRepository gameRepository,
                         final CurrentGame currentGame) {
        this.gameResultRepository = gameResultRepository;
        this.gameRepository = gameRepository;
        this.currentGame = currentGame;
    }

    /**
     * Save complete game results when a game ends.
     *
     * @param pin           game PIN
     * @param challengeName challenge name
     * @return saved GameResult entity
     */
    @Transactional
    public GameResult saveGameResult(final String pin, final String challengeName) {
        logger.info("Saving game result for PIN: {}", pin);

        Game game = gameRepository.findByPin(pin);
        if (game == null) {
            logger.error("Game not found for PIN: {}", pin);
            return null;
        }

        GameResult gameResult = createGameResult(pin, challengeName, game);
        addParticipantResults(gameResult, pin);
        addQuestionResults(gameResult, pin);

        GameResult saved = gameResultRepository.save(gameResult);
        logger.info("Game result saved with ID: {}", saved.getId());
        return saved;
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private GameResult createGameResult(final String pin, final String challengeName, final Game game) {
        GameResult gameResult = new GameResult();
        gameResult.setPin(pin);
        gameResult.setChallengeId(game.getChallengeId());
        gameResult.setChallengeName(challengeName);
        gameResult.setModerator(game.getModerator() != null ? game.getModerator() : "Unknown");
        gameResult.setGameMode(game.getGameMode());

        List<QuestionSet> questions = currentGame.getQuestionsOnAPin(pin);
        gameResult.setTotalQuestions(questions.size());

        List<String> participants = currentGame.getActiveUsersInGame(pin);
        gameResult.setParticipantCount(participants != null ? participants.size() - 1 : 0);

        gameResult.setStartedAt(game.getCreatedOn());
        gameResult.setCompletedAt(LocalDateTime.now(ZoneId.systemDefault()));

        Duration duration = Duration.between(game.getCreatedOn(), gameResult.getCompletedAt());
        gameResult.setDurationMinutes((int) duration.toMinutes());

        return gameResult;
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private void addParticipantResults(final GameResult gameResult, final String pin) {
        Map<String, Integer> scores = currentGame.getCurrentScore(pin);
        if (scores == null || scores.isEmpty()) {
            logger.warn("No scores found for game: {}", pin);
            return;
        }

        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
        sortedScores.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        String moderator = currentGame.findModerator(pin);
        int rank = 1;

        for (Map.Entry<String, Integer> entry : sortedScores) {
            String username = entry.getKey();
            if (username.equals(moderator)) {
                continue; // Skip moderator
            }

            ParticipantResult participantResult = new ParticipantResult();
            participantResult.setGameResult(gameResult);
            participantResult.setUsername(username);
            participantResult.setFinalScore(entry.getValue());
            participantResult.setFinalRank(rank);

            int streak = currentGame.getStreak(pin, username);
            participantResult.setMaxStreak(streak);

            participantResult.setCorrectAnswers(0);
            participantResult.setIncorrectAnswers(0);
            participantResult.setAverageAnswerTimeSeconds(0);

            gameResult.getParticipantResults().add(participantResult);

            if (rank == 1) {
                gameResult.setWinnerUsername(username);
                gameResult.setWinnerScore(entry.getValue());
            }
            rank++;
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private void addQuestionResults(final GameResult gameResult, final String pin) {
        List<QuestionSet> questions = currentGame.getQuestionsOnAPin(pin);
        if (questions == null || questions.isEmpty()) {
            return;
        }

        int questionNumber = 1;
        for (QuestionSet question : questions) {
            QuestionResult questionResult = new QuestionResult();
            questionResult.setGameResult(gameResult);
            questionResult.setQuestionNumber(questionNumber);
            questionResult.setQuestionText(question.getQuestion());
            questionResult.setQuestionType(question.getQuestionType() != null
                    ? question.getQuestionType().name() : "MULTIPLE_CHOICE");

            questionResult.setCorrectCount(0);
            questionResult.setIncorrectCount(0);
            questionResult.setTimeoutCount(0);
            questionResult.setAverageAnswerTimeSeconds(0);
            questionResult.setFastestAnswerTimeSeconds(0);

            gameResult.getQuestionResults().add(questionResult);
            questionNumber++;
        }
    }

    /**
     * Get all game results for a specific moderator.
     *
     * @param moderator moderator username
     * @return list of game results
     */
    public List<GameResult> getGameResultsByModerator(final String moderator) {
        return gameResultRepository.findByModeratorOrderByCompletedAtDesc(moderator);
    }

    /**
     * Get detailed game result by ID.
     *
     * @param id game result ID
     * @return game result with all details
     */
    public GameResult getGameResultById(final Long id) {
        return gameResultRepository.findById(id).orElse(null);
    }

    /**
     * Get all game results for a challenge.
     *
     * @param challengeId challenge ID
     * @return list of game results
     */
    public List<GameResult> getGameResultsByChallenge(final String challengeId) {
        return gameResultRepository.findByChallengeIdOrderByCompletedAtDesc(challengeId);
    }
}
