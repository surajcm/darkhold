package com.quiz.darkhold.challenge.dto;

import com.quiz.darkhold.challenge.entity.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("QuestionRequest Tests")
class QuestionRequestTest {

    @Test
    @DisplayName("Should have correct defaults")
    void defaults() {
        var req = new QuestionRequest();
        assertEquals(QuestionType.MULTIPLE_CHOICE, req.getQuestionType());
        assertEquals(1000, req.getPoints());
        assertNull(req.getChallengeId());
        assertNull(req.getQuestion());
        assertNull(req.getTimeLimit());
    }

    @Test
    @DisplayName("Should set question text fields")
    void shouldSetQuestionFields() {
        var req = new QuestionRequest();
        req.setChallengeId(1L);
        req.setQuestion("What?");
        req.setCorrectOptions("B");

        assertEquals(1L, req.getChallengeId());
        assertEquals("What?", req.getQuestion());
        assertEquals("B", req.getCorrectOptions());
    }

    @Test
    @DisplayName("Should set answer option fields")
    void shouldSetAnswerFields() {
        var req = new QuestionRequest();
        req.setAnswer1("A1");
        req.setAnswer2("A2");
        req.setAnswer3("A3");
        req.setAnswer4("A4");

        assertEquals("A1", req.getAnswer1());
        assertEquals("A2", req.getAnswer2());
        assertEquals("A3", req.getAnswer3());
        assertEquals("A4", req.getAnswer4());
    }

    @Test
    @DisplayName("Should set and get extended fields")
    void shouldSetExtendedFields() {
        var req = new QuestionRequest();
        req.setQuestionType(QuestionType.TYPE_ANSWER);
        req.setTimeLimit(30);
        req.setPoints(500);
        req.setAcceptableAnswers("alt1,alt2");
        req.setImageUrl("img.jpg");
        req.setVideoUrl("vid.mp4");

        assertEquals(QuestionType.TYPE_ANSWER, req.getQuestionType());
        assertEquals(30, req.getTimeLimit());
        assertEquals(500, req.getPoints());
        assertEquals("alt1,alt2", req.getAcceptableAnswers());
        assertEquals("img.jpg", req.getImageUrl());
        assertEquals("vid.mp4", req.getVideoUrl());
    }
}
