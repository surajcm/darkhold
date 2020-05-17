package com.quiz.darkhold.game.service;

import com.quiz.darkhold.game.model.QuestionOnGame;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GameService {

    @Autowired
    private CurrentGame currentGame;

    @Autowired
    private PreviewService previewService;

    private final Logger logger = LoggerFactory.getLogger(GameService.class);


    public PublishInfo getActiveChallenge() {
        return previewService.getActiveChallenge();
    }

    public List<String> saveAndGetAllParticipants(final String pin, final String userName) {
        currentGame.saveUserToActiveGame(pin, userName);
        return currentGame.getActiveUsersInGame(pin);
    }

    public List<String> getAllParticipants(final String pin) {
        return currentGame.getActiveUsersInGame(pin);
    }

    public int getCurrentQuestionNo() {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        int currentQuestionNo = currentGame.getCurrentQuestionNo(publishInfo.getPin());
        logger.info("current question # is " + currentQuestionNo);
        return currentQuestionNo;
    }

    public QuestionPointer getCurrentQuestionPointer() {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        return currentGame.getCurrentQuestionPointer(publishInfo.getPin());
    }

    public QuestionOnGame fetchQuestion() {
        String pin = currentPin();
        QuestionPointer pointer = currentGame.getCurrentQuestionPointer(pin);
        QuestionOnGame questionOnGame = new QuestionOnGame();
        questionOnGame.setCurrentQuestionNumber(pointer.getCurrentQuestionNumber());
        questionOnGame.setQuestion(pointer.getCurrentQuestion().getQuestion());
        return questionOnGame;
    }

    public void incrementQuestionNo() {
        String pin = currentPin();
        currentGame.incrementQuestionCount(pin);
    }

    /**
     * save current score.
     *
     * @param name of user
     * @param status of user
     */
    public void saveCurrentScore(final String name, final Integer status) {
        String pin = currentPin();
        currentGame.saveCurrentScore(pin, name, status);
    }

    public String findModerator() {
        String pin = currentPin();
        return currentGame.findModerator(pin);
    }

    public Map<String, Integer> getCurrentScore() {
        String pin = currentPin();
        return currentGame.getCurrentScore(pin);
    }

    private String currentPin() {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        return publishInfo.getPin();
    }
}
