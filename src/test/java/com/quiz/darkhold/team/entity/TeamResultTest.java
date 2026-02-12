package com.quiz.darkhold.team.entity;

import com.quiz.darkhold.analytics.entity.GameResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("TeamResult Entity Tests")
class TeamResultTest {

    @Test
    @DisplayName("Default constructor should have null fields")
    void defaultConstructor() {
        var result = new TeamResult();

        assertNull(result.getId());
        assertNull(result.getGameResult());
        assertNull(result.getTeamName());
        assertNull(result.getTeamColor());
        assertNull(result.getFinalScore());
        assertNull(result.getFinalRank());
        assertNull(result.getMemberCount());
        assertNull(result.getAverageScorePerMember());
    }

    @Test
    @DisplayName("Should set and get basic team fields")
    void shouldSetBasicFields() {
        var gameResult = new GameResult();
        gameResult.setId(10L);

        var result = new TeamResult();
        result.setId(1L);
        result.setGameResult(gameResult);
        result.setTeamName("Eagles");
        result.setTeamColor("#0000FF");

        assertEquals(1L, result.getId());
        assertEquals(gameResult, result.getGameResult());
        assertEquals("Eagles", result.getTeamName());
        assertEquals("#0000FF", result.getTeamColor());
    }

    @Test
    @DisplayName("Should set and get score fields")
    void shouldSetScoreFields() {
        var result = new TeamResult();
        result.setFinalScore(3500);
        result.setFinalRank(1);
        result.setMemberCount(4);
        result.setAverageScorePerMember(875);

        assertEquals(3500, result.getFinalScore());
        assertEquals(1, result.getFinalRank());
        assertEquals(4, result.getMemberCount());
        assertEquals(875, result.getAverageScorePerMember());
    }
}
