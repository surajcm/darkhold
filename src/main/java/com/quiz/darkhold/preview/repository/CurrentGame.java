package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.preview.model.PublishInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dizitart.no2.filters.Filters.eq;

@Repository
public class CurrentGame {

    private static final String PIN = "pin";
    private static final String USERS = "users";
    private static final String CURRENT_QUESTION_NO = "currentQuestionNo";
    private static final String QUESTIONS = "questions";
    private static final String MODERATOR = "MODERATOR";
    private static final String SCORES = "SCORES";
    private final Logger logger = LogManager.getLogger(CurrentGame.class);

    @Autowired
    private NitriteCollection collection;

    /**
     * save the game info to nitrate before we start the game.
     *
     * @param publishInfo publish info
     */
    public void saveCurrentStatus(final PublishInfo publishInfo, final List<QuestionSet> questionSets) {
        List<String> users = new ArrayList<>();
        users.add(publishInfo.getModerator());
        // table contents
        // PIN , List of users, Game moderator, Current question number
        // table of questions
        // table of scores
        Document doc = Document.createDocument(PIN, publishInfo.getPin())
                .put(USERS, users)
                .put(MODERATOR, publishInfo.getModerator())
                .put(CURRENT_QUESTION_NO, 0)
                .put(QUESTIONS, questionSets)
                .put(SCORES, new HashMap<String, Integer>());

        collection.insert(doc);
    }

    /**
     * get the active users in the gme.
     *
     * @param pin of the game
     * @return users
     */
    public List<String> getActiveUsersInGame(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var users = (List<String>) cursor.toList().get(0).get(USERS);
        logger.info("Participants are : {}", users);
        return users;
    }

    /**
     * save user to active game.
     *
     * @param pin      of the game
     * @param userName of user
     */
    public void saveUserToActiveGame(final String pin, final String userName) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var doc = cursor.toList().get(0);
        var users = (List<String>) doc.get(USERS);
        users.add(userName);
        collection.update(doc);
    }

    /**
     * save questions to active game.
     *
     * @param pin          of the game
     * @param questionSets of the game
     */
    public void saveQuestionsToActiveGame(final String pin, final List<QuestionSet> questionSets) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var doc = cursor.toList().get(0);
        var questions = (List<QuestionSet>) doc.get(QUESTIONS);
        questions.addAll(questionSets);
        collection.update(doc);
    }

    /**
     * current question no.
     *
     * @param pin of game
     * @return question no
     */
    public int getCurrentQuestionNo(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var questionNo = (Integer) cursor.toList().get(0).get(CURRENT_QUESTION_NO);
        logger.info("getCurrentQuestionNo : questionNo : {}}", questionNo);
        return questionNo;
    }

    /**
     * get all questions of the pin.
     *
     * @param pin of game
     * @return question list
     */
    public List<QuestionSet> getQuestionsOnAPin(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var questions = (List<QuestionSet>) cursor.toList().get(0).get(QUESTIONS);
        logger.info("question count : {}", questions.size());
        return questions;
    }

    /**
     * points to the next question.
     *
     * @param pin of game
     */
    public void incrementQuestionCount(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var questionNo = (Integer) cursor.toList().get(0).get(CURRENT_QUESTION_NO);
        questionNo++;
        var doc = cursor.toList().get(0);
        doc.put(CURRENT_QUESTION_NO, questionNo);
        collection.update(doc);
    }

    /**
     * find the moderator.
     *
     * @param pin pin
     * @return moderator
     */
    public String findModerator(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        return (String) cursor.toList().get(0).get(MODERATOR);
    }

    /**
     * save current score to nitrate.
     *
     * @param pin    pin of game
     * @param name   of user
     * @param status success or not
     */
    public void saveCurrentScore(final String pin, final String name, final Integer status) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var scores = (Map<String, Integer>) cursor.toList().get(0).get(SCORES);
        if (scores.containsKey(name)) {
            var currentValue = scores.get(name);
            scores.put(name, currentValue + status);
        } else {
            scores.put(name, status);
        }
        var doc = cursor.toList().get(0);
        doc.put(SCORES, scores);
        collection.update(doc);
    }

    public Map<String, Integer> getCurrentScore(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        return (Map<String, Integer>) cursor.toList().get(0).get(SCORES);
    }

    public QuestionPointer getCurrentQuestionPointer(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var doc = cursor.toList().get(0);
        var questionNo = (Integer) doc.get(CURRENT_QUESTION_NO);
        logger.info("questionNo : {}", questionNo);
        var questions = (List<QuestionSet>) doc.get(QUESTIONS);
        logger.info("questions size : {}}", questions.size());
        var questionPointer = new QuestionPointer();
        questionPointer.setCurrentQuestionNumber(questionNo);
        questionPointer.setTotalQuestionCount(questions.size());
        if (questionNo < questions.size()) {
            questionPointer.setCurrentQuestion(questions.get(questionNo));
        }
        return questionPointer;
    }

    public void stopTheGame(final String pin) {
        var cursor = collection.find(Filters.and(eq(PIN, pin)));
        var doc = cursor.toList().get(0);
        doc.remove(PIN);
        doc.remove(USERS);
        doc.remove(MODERATOR);
        doc.remove(CURRENT_QUESTION_NO);
        doc.remove(QUESTIONS);
        doc.remove(SCORES);
        collection.update(doc);
    }
}
