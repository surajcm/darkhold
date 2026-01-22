package com.quiz.darkhold.preview.service;

import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.init.GameConfig;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.List;

@Service
public class PreviewService {

    private final ChallengeRepository challengeRepository;
    private final GameRepository gameRepository;
    private final CurrentGame currentGame;
    private final GameConfig gameConfig;

    public PreviewService(final ChallengeRepository challengeRepository,
                          final GameRepository gameRepository,
                          final CurrentGame currentGame,
                          final GameConfig gameConfig) {
        this.challengeRepository = challengeRepository;
        this.gameRepository = gameRepository;
        this.currentGame = currentGame;
        this.gameConfig = gameConfig;
    }

    /**
     * fetch the questions of a challenge id.
     *
     * @param challengeId challenge id
     * @return preview info with questions
     */
    public PreviewInfo fetchQuestions(final String challengeId) {
        var previewInfo = new PreviewInfo();
        var challengeOne = Long.valueOf(challengeId);
        var challenge = challengeRepository.findById(challengeOne)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));
        // convert challenge to type ArrayDeque<QuestionSet>
        var dequeChallenge = new ArrayDeque<>(challenge.getQuestionSets());;
        previewInfo.setQuestionSets(dequeChallenge);
        previewInfo.setChallengeName(challenge.getTitle());
        previewInfo.setChallengeId(challengeId);
        return previewInfo;
    }

    /**
     * fetch questions from a pin.
     *
     * @param pin of the game
     * @return all questions binded to preview info
     */
    public PreviewInfo fetchQuestionsFromPin(final String pin) {
        var game = gameRepository.findByPin(pin);
        var challengeId = game.getChallengeId();
        var previewInfo = new PreviewInfo();
        var challengeOne = Long.valueOf(challengeId);
        var challenge = challengeRepository.findById(challengeOne);
        challenge.ifPresent(value -> previewInfo.setQuestionSets(new ArrayDeque<>(value.getQuestionSets())));
        challenge.ifPresent(value -> previewInfo.setChallengeName(value.getTitle()));
        previewInfo.setChallengeId(challengeId);
        return previewInfo;
    }

    /**
     * Generate a unique PIN for a new game.
     *
     * @param challengeId of game
     * @param currentUser who starts it
     * @return game info
     */
    public PublishInfo generateQuizPin(final String challengeId, final String currentUser) {
        String uniquePin = generateUniquePin();
        Game game = createAndSaveGame(uniquePin, challengeId, currentUser, false);
        return createPublishInfo(game, currentUser);
    }

    /**
     * Generate a unique PIN for a new game with team mode option.
     *
     * @param challengeId of game
     * @param currentUser who starts it
     * @param teamMode whether to enable team mode
     * @return game info
     */
    public PublishInfo generateQuizPin(final String challengeId, final String currentUser,
                                       final boolean teamMode) {
        String uniquePin = generateUniquePin();
        Game game = createAndSaveGame(uniquePin, challengeId, currentUser, teamMode);
        return createPublishInfo(game, currentUser);
    }

    private String generateUniquePin() {
        int maxAttempts = 10;
        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            String pin = RandomStringUtils.random(gameConfig.getPinLength(), false, true);
            if (!gameRepository.existsByPin(pin)) {
                return pin;
            }
        }
        throw new IllegalStateException("Failed to generate unique PIN after " + maxAttempts + " attempts");
    }

    private Game createAndSaveGame(final String pin, final String challengeId,
                                   final String moderator, final boolean teamMode) {
        var game = new Game();
        game.setPin(pin);
        game.setGameStatus(GameStatus.WAITING.name());
        game.setChallengeId(challengeId);
        game.setModerator(moderator);
        game.setTeamMode(teamMode);
        return gameRepository.save(game);
    }

    private PublishInfo createPublishInfo(final Game game, final String moderator) {
        var publishInfo = new PublishInfo();
        publishInfo.setPin(game.getPin());
        publishInfo.setModerator(moderator);
        var previewInfo = fetchQuestionsFromPin(game.getPin());
        currentGame.saveCurrentStatus(publishInfo, previewInfo.getQuestionSets());
        return publishInfo;
    }


    /**
     * Get the current running game.
     *
     * <p>NOTE: This method returns the LATEST active game, which only works correctly
     * when there's a single game running. For multiple concurrent games, use
     * session-based PIN retrieval via GameService.getSessionPin() instead.
     *
     * <p>This method is kept for backwards compatibility as a fallback when
     * session context is not available (e.g., during WebSocket calls without
     * HTTP session).
     *
     * @return game info with PIN of the latest active game
     * @deprecated Use session-based PIN retrieval for concurrent game support
     */
    @Deprecated(since = "Milestone 6 - Concurrent Games")
    public PublishInfo getActiveChallenge() {
        var activeGames = gameRepository.findByGameStatusNot(GameStatus.FINISHED.name());
        var publishInfo = new PublishInfo();
        if (!activeGames.isEmpty()) {
            var size = activeGames.size();
            var activeGame = activeGames.get(size - 1);
            // Returns latest active game - only reliable with single game
            publishInfo.setPin(activeGame.getPin());
        }
        return publishInfo;
    }

    /**
     * Get game info by PIN.
     * This is the preferred method for concurrent game support.
     *
     * @param pin game PIN
     * @return game info
     */
    public PublishInfo getGameByPin(final String pin) {
        var game = gameRepository.findByPin(pin);
        var publishInfo = new PublishInfo();
        if (game != null) {
            publishInfo.setPin(game.getPin());
        }
        return publishInfo;
    }

    /**
     * Get all active games for a specific moderator.
     * Active games are those not in FINISHED status.
     *
     * @param moderator the moderator username
     * @return list of active games
     */
    public List<Game> getActiveGamesForModerator(final String moderator) {
        return gameRepository.findByModeratorAndGameStatusNotOrderByCreatedOnDesc(
                moderator, GameStatus.FINISHED.name());
    }

    /**
     * Get all games for a specific moderator with a specific status.
     *
     * @param moderator the moderator username
     * @param status the game status
     * @return list of games
     */
    public List<Game> getGamesByModeratorAndStatus(final String moderator, final GameStatus status) {
        return gameRepository.findByModeratorAndGameStatus(moderator, status.name());
    }
}
