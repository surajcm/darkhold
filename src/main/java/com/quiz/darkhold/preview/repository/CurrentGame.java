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

    public static final String PIN = "pin";
    public static final String USERS = "users";
    private final Logger logger = LoggerFactory.getLogger(CurrentGame.class);

    @Autowired
    private NitriteCollection collection;

    public void saveCurrentStatus(PublishInfo publishInfo) {
        List<String> users = new ArrayList<>();
        users.add(publishInfo.getUsername());
        Document doc = Document.createDocument(PIN, publishInfo.getPin())
                .put(USERS, users)
                .put("currentQuestionNo", -1)
                .put("questions", new ArrayList<>());

        collection.insert(doc);
    }

    public List<String> getActiveUsersInGame(String pin) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        List<String> users = (List<String>) cursor.toList().get(0).get(USERS);
        logger.info("Participants are :" + users);
        return users;
    }

    public void saveUserToActiveGame(String pin, String userName) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Document doc = cursor.toList().get(0);
        List<String> users = (List<String>) doc.get(USERS);
        users.add(userName);
        collection.update(doc);
    }

    public void saveQuestionsToActiveGame(String pin, List<QuestionSet> questionSets) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Document doc = cursor.toList().get(0);
        List<QuestionSet> questions = (List<QuestionSet>) doc.get("questions");
        questions.addAll(questionSets);
        collection.update(doc);
    }

    public int getCurrentQuestionNo(String pin) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Integer questionNo = (Integer) cursor.toList().get(0).get("currentQuestionNo");
        logger.info("questionNo :" + questionNo);
        return questionNo;
    }

    public List<QuestionSet> getQuestionsOnAPin(String pin) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        List<QuestionSet> questions = (List<QuestionSet>) cursor.toList().get(0).get("questions");
        logger.info("question count :" + questions.size());
        return questions;
    }

    public void incrementQuestionCount(String pin, int currentQuestionNumber) {
        Cursor cursor = collection.find(Filters.and(eq(PIN, pin)));
        Integer questionNo = (Integer) cursor.toList().get(0).get("currentQuestionNo");
        questionNo++;
    }
}
