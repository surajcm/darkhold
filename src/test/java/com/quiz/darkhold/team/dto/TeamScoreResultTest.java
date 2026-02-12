package com.quiz.darkhold.team.dto;

import com.quiz.darkhold.game.model.ScoreResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TeamScoreResult Tests")
class TeamScoreResultTest {

    @Test
    @DisplayName("Default constructor should initialize lists and scores")
    void defaultConstructor() {
        var result = new TeamScoreResult();

        assertNotNull(result.getIndividualScores());
        assertTrue(result.getIndividualScores().isEmpty());
        assertEquals(0, result.getTotalScore());
        assertEquals(0, result.getPreviousScore());
        assertNull(result.getTeamName());
    }

    @Test
    @DisplayName("Full constructor should set all fields")
    void fullConstructor() {
        var result = new TeamScoreResult("Alpha", "#FF0000", 500, 1);

        assertEquals("Alpha", result.getTeamName());
        assertEquals("#FF0000", result.getColor());
        assertEquals(500, result.getTotalScore());
        assertEquals(1, result.getRank());
        assertEquals(0, result.getPreviousScore());
        assertEquals(1, result.getPreviousRank());
        assertNotNull(result.getIndividualScores());
    }

    @Test
    @DisplayName("Should set and get basic team fields")
    void shouldSetBasicFields() {
        var result = new TeamScoreResult();
        result.setTeamName("Beta");
        result.setColor("#00FF00");
        result.setTotalScore(1000);
        result.setPreviousScore(800);

        assertEquals("Beta", result.getTeamName());
        assertEquals("#00FF00", result.getColor());
        assertEquals(1000, result.getTotalScore());
        assertEquals(800, result.getPreviousScore());
    }

    @Test
    @DisplayName("Should set and get ranking fields")
    void shouldSetRankingFields() {
        var result = new TeamScoreResult();
        result.setScoreDelta(200);
        result.setRank(2);
        result.setPreviousRank(3);
        result.setRankChange(1);

        assertEquals(200, result.getScoreDelta());
        assertEquals(2, result.getRank());
        assertEquals(3, result.getPreviousRank());
        assertEquals(1, result.getRankChange());
    }

    @Test
    @DisplayName("isRankUp should return true for positive rank change")
    void isRankUp() {
        var result = new TeamScoreResult();
        result.setRankChange(2);
        assertTrue(result.isRankUp());
    }

    @Test
    @DisplayName("isRankUp should return false for zero rank change")
    void isRankUpZero() {
        var result = new TeamScoreResult();
        result.setRankChange(0);
        assertFalse(result.isRankUp());
    }

    @Test
    @DisplayName("isRankUp should return false for null rank change")
    void isRankUpNull() {
        var result = new TeamScoreResult();
        result.setRankChange(null);
        assertFalse(result.isRankUp());
    }

    @Test
    @DisplayName("isRankDown should return true for negative rank change")
    void isRankDown() {
        var result = new TeamScoreResult();
        result.setRankChange(-1);
        assertTrue(result.isRankDown());
    }

    @Test
    @DisplayName("isRankDown should return false for null rank change")
    void isRankDownNull() {
        var result = new TeamScoreResult();
        result.setRankChange(null);
        assertFalse(result.isRankDown());
    }

    @Test
    @DisplayName("getMemberCount should return size of individual scores")
    void getMemberCount() {
        var result = new TeamScoreResult();
        assertEquals(0, result.getMemberCount());

        result.addIndividualScore(new ScoreResult());
        result.addIndividualScore(new ScoreResult());
        assertEquals(2, result.getMemberCount());
    }

    @Test
    @DisplayName("Should set individual scores list")
    void setIndividualScores() {
        var result = new TeamScoreResult();
        var scores = java.util.List.of(new ScoreResult(), new ScoreResult());
        result.setIndividualScores(scores);
        assertEquals(2, result.getIndividualScores().size());
    }
}
