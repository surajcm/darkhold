package com.quiz.darkhold.game.service;

import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

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
        var publishInfo = previewService.getActiveChallenge();
        var pin = publishInfo.getPin();
        logger.info("Current pin is ");
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
        return currentGame.getCurrentScore(pin);
    }

    private String currentPin() {
        var publishInfo = previewService.getActiveChallenge();
        return publishInfo.getPin();
    }

    public void cleanUpCurrentGame() {
        var publishInfo = previewService.getActiveChallenge();
        // make it inactive
        // save scores to a db
        currentGame.stopTheGame(publishInfo.getPin());
    }
}
