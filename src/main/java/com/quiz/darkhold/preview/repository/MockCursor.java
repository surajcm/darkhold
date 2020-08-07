package com.quiz.darkhold.preview.repository;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Lookup;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.RecordIterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockCursor implements Cursor {

    private static final String ADMIN = "admin";

    @Override
    public RecordIterable<Document> project(final Document projection) {
        return null;
    }

    @Override
    public RecordIterable<Document> join(final Cursor foreignCursor, final Lookup lookup) {
        return null;
    }

    @Override
    public Set<NitriteId> idSet() {
        return Collections.emptySet();
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int totalCount() {
        return 0;
    }

    @Override
    public Document firstOrDefault() {
        return null;
    }

    @Override
    public List<Document> toList() {
        Document document = new Document();
        document.put("users", getUsers());
        document.put("questions", getQuestions());
        document.put("currentQuestionNo", 5);
        document.put("MODERATOR", ADMIN);
        document.put("SCORES", getScores());
        List<Document> documents = new ArrayList<>();
        documents.add(document);
        return documents;
    }

    private List<String> getUsers() {
        List<String> users = new ArrayList<>();
        users.add(ADMIN);
        users.add("tester");
        return users;
    }

    private List<QuestionSet> getQuestions() {
        QuestionSet questionSet = new QuestionSet();
        questionSet.setQuestion("Q1");
        questionSet.setAnswer1("A1");
        questionSet.setAnswer2("A2");
        questionSet.setAnswer3("A3");
        questionSet.setAnswer4("A4");
        questionSet.setCorrectOptions("A");
        List<QuestionSet> questions = new ArrayList<>();
        questions.add(questionSet);
        return questions;
    }

    private Map<String, Integer> getScores() {
        Map<String, Integer> scores = new HashMap<>();
        scores.put(ADMIN, 5);
        return scores;
    }

    @Override
    public Iterator<Document> iterator() {
        return null;
    }
}
