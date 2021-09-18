package com.quiz.darkhold.preview.service;

import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class PreviewService {

    private final ChallengeRepository challengeRepository;

    private final GameRepository gameRepository;

    private final CurrentGame currentGame;

    public PreviewService(final ChallengeRepository challengeRepository,
                          final GameRepository gameRepository,
                          final CurrentGame currentGame) {
        this.challengeRepository = challengeRepository;
        this.gameRepository = gameRepository;
        this.currentGame = currentGame;
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
        var challenge = challengeRepository.getById(challengeOne);
        previewInfo.setQuestionSets(challenge.getQuestionSets());
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
        challenge.ifPresent(value -> previewInfo.setQuestionSets(value.getQuestionSets()));
        challenge.ifPresent(value -> previewInfo.setChallengeName(value.getTitle()));
        previewInfo.setChallengeId(challengeId);
        return previewInfo;
    }

    /**
     * Indicating the start of the game, generate a pin.
     *
     * @param challengeId of game
     * @param currentUser who starts it
     * @return game info
     */
    public PublishInfo generateQuizPin(final String challengeId, final String currentUser) {
        var generatedString = RandomStringUtils.random(5, false, true);
        var game = new Game();
        game.setPin(generatedString);
        game.setGameStatus(GameStatus.WAITING.name());
        game.setChallengeId(challengeId);
        gameRepository.save(game);
        var publishInfo = new PublishInfo();
        publishInfo.setPin(generatedString);
        publishInfo.setModerator(currentUser);
        var previewInfo = fetchQuestionsFromPin(generatedString);
        var questionSets = previewInfo.getQuestionSets();
        currentGame.saveCurrentStatus(publishInfo, questionSets);
        return publishInfo;
    }


    /**
     * get the current running game.
     *
     * @return game info
     */
    public PublishInfo getActiveChallenge() {
        var activeGames = gameRepository.findByGameStatusNot(GameStatus.FINISHED.name());
        var publishInfo = new PublishInfo();
        if (!activeGames.isEmpty()) {
            var size = activeGames.size();
            var activeGame = activeGames.get(size - 1);
            //currently, we are taking the latest one, may need optimization if we run multiple games in parallel
            publishInfo.setPin(activeGame.getPin());
        }
        return publishInfo;
    }
}
