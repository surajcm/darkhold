package com.quiz.darkhold.game.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ScoreResult Tests")
class ScoreResultTest {

    @Test
    @DisplayName("Full constructor should compute deltas and streak")
    void fullConstructor() {
        var result = new ScoreResult("Alice", 3000, 2000, 1, 3, 4);

        assertEquals("Alice", result.getUsername());
        assertEquals(3000, result.getCurrentScore());
        assertEquals(2000, result.getPreviousScore());
        assertEquals(1000, result.getScoreDelta());
        assertEquals(1, result.getRank());
        assertEquals(3, result.getPreviousRank());
        assertEquals(2, result.getRankChange()); // 3 - 1 = 2 (moved up)
        assertEquals(4, result.getStreak());
        assertEquals(3, result.getStreakMultiplier()); // streak 4-5 = 3x
    }

    @Test
    @DisplayName("Default constructor should have zero values")
    void defaultConstructor() {
        var result = new ScoreResult();
        assertEquals(0, result.getCurrentScore());
        assertEquals(0, result.getRank());
        assertEquals(0, result.getStreak());
    }

    @Test
    @DisplayName("Should set and get basic score fields")
    void shouldSetBasicFields() {
        var result = new ScoreResult();
        result.setUsername("Bob");
        result.setCurrentScore(1500);
        result.setPreviousScore(1000);
        result.setScoreDelta(500);
        result.setRank(2);
        result.setPreviousRank(4);

        assertEquals("Bob", result.getUsername());
        assertEquals(1500, result.getCurrentScore());
        assertEquals(1000, result.getPreviousScore());
        assertEquals(500, result.getScoreDelta());
        assertEquals(2, result.getRank());
        assertEquals(4, result.getPreviousRank());
    }

    @Test
    @DisplayName("Should set and get extended score fields")
    void shouldSetExtendedFields() {
        var result = new ScoreResult();
        result.setRankChange(2);
        result.setStreak(3);
        result.setBasePoints(800);
        result.setBonusPoints(200);

        assertEquals(2, result.getRankChange());
        assertEquals(3, result.getStreak());
        assertEquals(2, result.getStreakMultiplier()); // setStreak recalcs
        assertEquals(800, result.getBasePoints());
        assertEquals(200, result.getBonusPoints());
    }

    @Test
    @DisplayName("setStreakMultiplier should override calculated value")
    void setStreakMultiplier() {
        var result = new ScoreResult();
        result.setStreakMultiplier(99);
        assertEquals(99, result.getStreakMultiplier());
    }

    @Test
    @DisplayName("calculateStreakMultiplier - streak 0 returns 1x")
    void streakZero() {
        assertEquals(1, ScoreResult.calculateStreakMultiplier(0));
    }

    @Test
    @DisplayName("calculateStreakMultiplier - streak 1 returns 1x")
    void streakOne() {
        assertEquals(1, ScoreResult.calculateStreakMultiplier(1));
    }

    @Test
    @DisplayName("calculateStreakMultiplier - streak 2 returns 2x")
    void streakTwo() {
        assertEquals(2, ScoreResult.calculateStreakMultiplier(2));
    }

    @Test
    @DisplayName("calculateStreakMultiplier - streak 3 returns 2x")
    void streakThree() {
        assertEquals(2, ScoreResult.calculateStreakMultiplier(3));
    }

    @Test
    @DisplayName("calculateStreakMultiplier - streak 4 returns 3x")
    void streakFour() {
        assertEquals(3, ScoreResult.calculateStreakMultiplier(4));
    }

    @Test
    @DisplayName("calculateStreakMultiplier - streak 5 returns 3x")
    void streakFive() {
        assertEquals(3, ScoreResult.calculateStreakMultiplier(5));
    }

    @Test
    @DisplayName("calculateStreakMultiplier - streak 6+ returns 4x")
    void streakSixPlus() {
        assertEquals(4, ScoreResult.calculateStreakMultiplier(6));
        assertEquals(4, ScoreResult.calculateStreakMultiplier(10));
    }

    @Test
    @DisplayName("isRankUp should return true when rankChange > 0")
    void isRankUp() {
        var result = new ScoreResult();
        result.setRankChange(1);
        assertTrue(result.isRankUp());
    }

    @Test
    @DisplayName("isRankUp should return false when rankChange <= 0")
    void isRankUpFalse() {
        var result = new ScoreResult();
        result.setRankChange(0);
        assertFalse(result.isRankUp());
        result.setRankChange(-1);
        assertFalse(result.isRankUp());
    }

    @Test
    @DisplayName("isRankDown should return true when rankChange < 0")
    void isRankDown() {
        var result = new ScoreResult();
        result.setRankChange(-2);
        assertTrue(result.isRankDown());
    }

    @Test
    @DisplayName("hasStreak should return true when streak >= 2")
    void hasStreak() {
        var result = new ScoreResult();
        result.setStreak(2);
        assertTrue(result.hasStreak());
        result.setStreak(1);
        assertFalse(result.hasStreak());
    }

    @Test
    @DisplayName("getStreakDisplay should return multiplier string")
    void streakDisplay() {
        var result = new ScoreResult();
        result.setStreak(1);
        assertEquals("", result.getStreakDisplay());

        result.setStreak(3);
        assertEquals("x2", result.getStreakDisplay());

        result.setStreak(6);
        assertEquals("x4", result.getStreakDisplay());
    }
}
