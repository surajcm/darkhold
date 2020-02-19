package com.quiz.darkhold.game.service;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.Challenge;
import com.quiz.darkhold.game.model.QuestionOnGame;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class GameService {

    @Autowired
    private CurrentGame currentGame;

    @Autowired
    private PreviewService previewService;


    public PublishInfo getActiveChallenge(String name) {
        return  previewService.getActiveChallenge(name);
    }

    public List<String> saveAndGetAllParticipants(String pin, String userName) {
        currentGame.saveUserToActiveGame(pin, userName);
        return currentGame.getActiveUsersInGame(pin);
    }

    public List<String> getAllParticipants(String pin) {
        return currentGame.getActiveUsersInGame(pin);
    }

    public int getCurrentQuestionNo(String pin) {
        return currentGame.getCurrentQuestionNo(pin);
    }

    public QuestionOnGame initialFetchAndUpdateNitrate(String pin) {
        PreviewInfo previewInfo = previewService.fetchQuestionsFromPin(pin);
        List<QuestionSet> questionSets = previewInfo.getQuestionSets();
        // fetch the questions and load it to nitrate
        currentGame.saveQuestionsToActiveGame(pin, questionSets);
        QuestionOnGame questionOnGame = new QuestionOnGame();
        questionOnGame.setCurrentQuestionNumber(0);
        questionSets.stream().findFirst().ifPresent(e -> questionOnGame.setQuestion(e.getQuestion()));
        return questionOnGame;
    }

    public QuestionOnGame fetchAnotherQuestion(String pin, int currentQuestionNumber) {
        List<QuestionSet> questionSets = currentGame.getQuestionsOnAPin(pin);
        QuestionSet questionSet = questionSets.get(currentQuestionNumber + 1);

        QuestionOnGame questionOnGame = new QuestionOnGame();
        questionOnGame.setCurrentQuestionNumber(currentQuestionNumber + 1);
        questionOnGame.setQuestion(questionSet.getQuestion());
        currentGame.incrementQuestionCount(pin, currentQuestionNumber);
        return questionOnGame;
    }

    public Challenge getCurrentQuestionSet(String pin, int currentQuestionNumber) {
        Challenge challenge = new Challenge();
        challenge.setQuestionNumber(currentQuestionNumber);
        challenge.setQuestionSet(currentGame.getQuestionsOnAPin(pin).get(currentQuestionNumber));
        return challenge;
    }
}
