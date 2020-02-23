package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.preview.model.PublishInfo;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.dizitart.no2.filters.Filters.eq;

@Repository
public class CurrentGame {

    private static final String PIN = "pin";
    private static final String USERS = "users";
    private static final String CURRENT_QUESTION_NO = "currentQuestionNo";
    private static final String QUESTIONS = "questions";
    private final Logger logger = LoggerFactory.getLogger(CurrentGame.class);

    @Autowired
    private NitriteCollection collection;

    /**
     * save the game info to nitrate before we start the game
     *
     * @param publishInfo publish info
     */
    public void saveCurrentStatus(PublishInfo publishInfo) {
        List<String> users = new ArrayList<>();
        users.add(publishInfo.getUsername());
        Document doc = Document.createDocument(PIN, publishInfo.getPin())
                .put(USERS, users)
                .put(CURRENT_QUESTION_NO, -1)
                .put(QUESTIONS, new ArrayList<>());

        collection.insert(doc);
    }

    /**
     * get the active users in the gme
     *
     * @param pin of the game
     * @return users
     */
    public List<String> getActiveUsersInGame(String pin) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        List<String> users = (List<String>) cursor.toList().get(0).get(USERS);
        logger.info("Participants are :" + users);
        return users;
    }

    /**
     * save user to active game
     *
     * @param pin      of the game
     * @param userName of user
     */
    public void saveUserToActiveGame(String pin, String userName) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Document doc = cursor.toList().get(0);
        List<String> users = (List<String>) doc.get(USERS);
        users.add(userName);
        collection.update(doc);
    }

    /**
     * save questions to active game
     *
     * @param pin          of the game
     * @param questionSets of the game
     */
    public void saveQuestionsToActiveGame(String pin, List<QuestionSet> questionSets) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Document doc = cursor.toList().get(0);
        List<QuestionSet> questions = (List<QuestionSet>) doc.get(QUESTIONS);
        questions.addAll(questionSets);
        collection.update(doc);
    }

    /**
     * current question no
     *
     * @param pin of game
     * @return question no
     */
    public int getCurrentQuestionNo(String pin) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Integer questionNo = (Integer) cursor.toList().get(0).get(CURRENT_QUESTION_NO);
        logger.info("questionNo :" + questionNo);
        return questionNo;
    }

    /**
     * get all questions of the pin
     *
     * @param pin of game
     * @return question list
     */
    public List<QuestionSet> getQuestionsOnAPin(String pin) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        List<QuestionSet> questions = (List<QuestionSet>) cursor.toList().get(0).get(QUESTIONS);
        logger.info("question count :" + questions.size());
        return questions;
    }

    /**
     * points to the next question
     *
     * @param pin of game
     */
    public void incrementQuestionCount(String pin) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Integer questionNo = (Integer) cursor.toList().get(0).get(CURRENT_QUESTION_NO);
        questionNo++;
        Document doc = cursor.toList().get(0);
        doc.put(CURRENT_QUESTION_NO, questionNo);
        collection.update(doc);
    }
}
