package com.quiz.darkhold.preview.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreviewService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CurrentGame currentGame;

    /**
     * fetch the questions of a challenge id.
     *
     * @param challengeId challenge id
     * @return preview info with questions
     */
    public PreviewInfo fetchQuestions(final String challengeId) {
        PreviewInfo previewInfo = new PreviewInfo();
        Long challengeOne = Long.valueOf(challengeId);
        Challenge challenge = challengeRepository.getOne(challengeOne);
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
        Game game = gameRepository.findByPin(pin);
        String challengeId = game.getChallengeId();
        PreviewInfo previewInfo = new PreviewInfo();
        Long challengeOne = Long.valueOf(challengeId);
        Optional<Challenge> challenge = challengeRepository.findById(challengeOne);
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
        String generatedString = RandomStringUtils.random(5, false, true);
        Game game = new Game();
        game.setPin(generatedString);
        game.setGameStatus(GameStatus.WAITING.name());
        game.setChallengeId(challengeId);
        gameRepository.save(game);
        PublishInfo publishInfo = new PublishInfo();
        publishInfo.setPin(generatedString);
        publishInfo.setModerator(currentUser);
        currentGame.saveCurrentStatus(publishInfo);
        return publishInfo;
    }


    /**
     * get the current running game.
     *
     * @return game info
     */
    public PublishInfo getActiveChallenge() {
        List<Game> activeGames = gameRepository.findByGameStatusNot(GameStatus.FINISHED.name());
        Game activeGame = activeGames.get(0);
        //currently, we are taking the first one, may need optimization if we run multiple games in parallel
        PublishInfo publishInfo = new PublishInfo();
        publishInfo.setPin(activeGame.getPin());
        return publishInfo;
    }
}
