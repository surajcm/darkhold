package com.quiz.darkhold.challenge.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("ReorderRequest Tests")
class ReorderRequestTest {

    @Test
    @DisplayName("Should have null defaults")
    void defaults() {
        var req = new ReorderRequest();
        assertNull(req.getChallengeId());
        assertNull(req.getQuestionIds());
    }

    @Test
    @DisplayName("Should set and get fields")
    void settersAndGetters() {
        var req = new ReorderRequest();
        req.setChallengeId(5L);
        req.setQuestionIds(List.of(1L, 2L, 3L));

        assertEquals(5L, req.getChallengeId());
        assertEquals(List.of(1L, 2L, 3L), req.getQuestionIds());
    }
}
