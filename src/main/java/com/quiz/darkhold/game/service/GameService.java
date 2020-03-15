package com.quiz.darkhold.game.service;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.Challenge;
import com.quiz.darkhold.game.model.QuestionOnGame;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * During start of the game, get the first question.
     *
     * @return question
     */
    public QuestionOnGame initialFetchAndUpdateNitrate() {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        String pin = publishInfo.getPin();
        PreviewInfo previewInfo = previewService.fetchQuestionsFromPin(pin);
        List<QuestionSet> questionSets = previewInfo.getQuestionSets();
        // fetch the questions and load it to nitrate
        currentGame.saveQuestionsToActiveGame(pin, questionSets);
        QuestionOnGame questionOnGame = new QuestionOnGame();
        questionOnGame.setCurrentQuestionNumber(0);
        questionSets.stream().findFirst().ifPresent(e -> questionOnGame.setQuestion(e.getQuestion()));
        return questionOnGame;
    }

    public List<QuestionSet> getQuestionsOnAPin() {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        String pin = publishInfo.getPin();
        List<QuestionSet> questionSets = currentGame.getQuestionsOnAPin(pin);
        logger.info("Size of question set is" + questionSets.size());
        return questionSets;
    }

    /**
     * Fetch the next question, and it's options.
     *
     * @param currentQuestionNumber current one
     * @return question
     */
    public QuestionOnGame fetchAnotherQuestion(final int currentQuestionNumber) {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        String pin = publishInfo.getPin();
        List<QuestionSet> questionSets = currentGame.getQuestionsOnAPin(pin);
        QuestionSet questionSet = questionSets.get(currentQuestionNumber);
        QuestionOnGame questionOnGame = new QuestionOnGame();
        questionOnGame.setCurrentQuestionNumber(currentQuestionNumber);
        questionOnGame.setQuestion(questionSet.getQuestion());
        return questionOnGame;
    }

    /**
     * Once we have the question number, get the questions from nitrate.
     *
     * @param currentQuestionNumber yes it is
     * @return challenge
     */
    public Challenge getCurrentQuestionSet(final int currentQuestionNumber) {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        String pin = publishInfo.getPin();
        Challenge challenge = new Challenge();
        challenge.setQuestionNumber(currentQuestionNumber);
        challenge.setQuestionSet(currentGame.getQuestionsOnAPin(pin).get(currentQuestionNumber));
        return challenge;
    }

    public void updateQuestionNo() {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        String pin = publishInfo.getPin();
        currentGame.incrementQuestionCount(pin);
    }

    public void saveCurrentScore(final String name, final String name1) {

    }

    public String findModerator() {
        PublishInfo publishInfo = previewService.getActiveChallenge();
        String pin = publishInfo.getPin();
        return currentGame.findModerator(pin);
    }
}
