package com.quiz.darkhold.challenge.dto;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("QuestionResponse Tests")
class QuestionResponseTest {

    @Test
    @DisplayName("Should map entity IDs")
    void fromEntityIds() {
        var challenge = new Challenge();
        challenge.setId(10L);
        var qs = new QuestionSet();
        qs.setId(1L);
        qs.setChallenge(challenge);

        var response = QuestionResponse.fromEntity(qs);

        assertEquals(1L, response.getId());
        assertEquals(10L, response.getChallengeId());
    }

    @Test
    @DisplayName("Should map question text")
    void fromEntityQuestion() {
        var qs = new QuestionSet();
        qs.setQuestion("What is Java?");
        qs.setCorrectOptions("A");

        var response = QuestionResponse.fromEntity(qs);

        assertEquals("What is Java?", response.getQuestion());
        assertEquals("A", response.getCorrectOptions());
    }

    @Test
    @DisplayName("Should map all answer options")
    void fromEntityAllAnswers() {
        var qs = new QuestionSet();
        qs.setAnswer1("A language");
        qs.setAnswer2("A coffee");
        qs.setAnswer3("An island");
        qs.setAnswer4("All of the above");

        var response = QuestionResponse.fromEntity(qs);

        assertEquals("A language", response.getAnswer1());
        assertEquals("A coffee", response.getAnswer2());
        assertEquals("An island", response.getAnswer3());
        assertEquals("All of the above", response.getAnswer4());
    }

    @Test
    @DisplayName("Should map entity configuration fields")
    void fromEntityConfig() {
        var qs = new QuestionSet();
        qs.setDisplayOrder(3);
        qs.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        qs.setTimeLimit(30);
        qs.setPoints(500);

        var response = QuestionResponse.fromEntity(qs);

        assertEquals(3, response.getDisplayOrder());
        assertEquals(QuestionType.MULTIPLE_CHOICE, response.getQuestionType());
        assertEquals(30, response.getTimeLimit());
        assertEquals(500, response.getPoints());
    }

    @Test
    @DisplayName("Should map entity media fields")
    void fromEntityMedia() {
        var qs = new QuestionSet();
        qs.setAcceptableAnswers("language,programming language");
        qs.setImageUrl("http://img.png");
        qs.setVideoUrl("http://vid.mp4");

        var response = QuestionResponse.fromEntity(qs);

        assertEquals("language,programming language", response.getAcceptableAnswers());
        assertEquals("http://img.png", response.getImageUrl());
        assertEquals("http://vid.mp4", response.getVideoUrl());
    }

    @Test
    @DisplayName("Should handle null challenge in entity")
    void fromEntityNullChallenge() {
        var qs = new QuestionSet();
        qs.setId(2L);
        qs.setQuestion("Test");

        var response = QuestionResponse.fromEntity(qs);

        assertEquals(2L, response.getId());
        assertNull(response.getChallengeId());
    }

    @Test
    @DisplayName("Should set ID fields")
    void shouldSetIds() {
        var response = new QuestionResponse();
        response.setId(5L);
        response.setChallengeId(20L);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertEquals(20L, response.getChallengeId());
    }

    @Test
    @DisplayName("Should set answer fields")
    void shouldSetAnswers() {
        var response = new QuestionResponse();
        response.setQuestion("Q?");
        response.setAnswer1("A1");
        response.setAnswer2("A2");
        response.setAnswer3("A3");
        response.setAnswer4("A4");
        response.setCorrectOptions("B");

        assertEquals("Q?", response.getQuestion());
        assertEquals("A1", response.getAnswer1());
        assertEquals("A2", response.getAnswer2());
        assertEquals("A3", response.getAnswer3());
        assertEquals("A4", response.getAnswer4());
        assertEquals("B", response.getCorrectOptions());
    }

    @Test
    @DisplayName("Should set configuration fields")
    void shouldSetConfig() {
        var response = new QuestionResponse();
        response.setDisplayOrder(1);
        response.setQuestionType(QuestionType.TRUE_FALSE);
        response.setTimeLimit(15);
        response.setPoints(750);

        assertEquals(1, response.getDisplayOrder());
        assertEquals(QuestionType.TRUE_FALSE, response.getQuestionType());
        assertEquals(15, response.getTimeLimit());
        assertEquals(750, response.getPoints());
    }

    @Test
    @DisplayName("Should set media fields")
    void shouldSetMedia() {
        var response = new QuestionResponse();
        response.setAcceptableAnswers("alt");
        response.setImageUrl("img.jpg");
        response.setVideoUrl("vid.mp4");

        assertEquals("alt", response.getAcceptableAnswers());
        assertEquals("img.jpg", response.getImageUrl());
        assertEquals("vid.mp4", response.getVideoUrl());
    }
}
