package com.quiz.darkhold.analytics.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Analytics Entity Tests")
class AnalyticsEntityTest {

    @Nested
    @DisplayName("GameResult")
    class GameResultTests {

        @Test
        @DisplayName("Default constructor should initialize lists")
        void defaultConstructor() {
            var result = new GameResult();
            assertNotNull(result.getParticipantResults());
            assertNotNull(result.getQuestionResults());
        }

        @Test
        @DisplayName("Should set game identity fields")
        void shouldSetIdentityFields() {
            var result = new GameResult();
            result.setId(1L);
            result.setPin("12345");
            result.setChallengeId("C1");
            result.setChallengeName("Math Quiz");

            assertEquals(1L, result.getId());
            assertEquals("12345", result.getPin());
            assertEquals("C1", result.getChallengeId());
            assertEquals("Math Quiz", result.getChallengeName());
        }

        @Test
        @DisplayName("Should set game configuration fields")
        void shouldSetConfigFields() {
            var result = new GameResult();
            result.setModerator("admin");
            result.setGameMode("CLASSIC");
            result.setTotalQuestions(10);
            result.setParticipantCount(20);

            assertEquals("admin", result.getModerator());
            assertEquals("CLASSIC", result.getGameMode());
            assertEquals(10, result.getTotalQuestions());
            assertEquals(20, result.getParticipantCount());
        }

        @Test
        @DisplayName("Should set timing fields")
        void shouldSetTimingFields() {
            var now = LocalDateTime.now();
            var result = new GameResult();
            result.setStartedAt(now);
            result.setCompletedAt(now.plusMinutes(15));
            result.setDurationMinutes(15);

            assertEquals(now, result.getStartedAt());
            assertEquals(now.plusMinutes(15), result.getCompletedAt());
            assertEquals(15, result.getDurationMinutes());
        }

        @Test
        @DisplayName("Should set winner fields")
        void shouldSetWinnerFields() {
            var result = new GameResult();
            result.setWinnerUsername("player1");
            result.setWinnerScore(5000);
            result.setTeamMode(true);
            result.setWinningTeamName("Eagles");
            result.setWinningTeamScore(15000);

            assertEquals("player1", result.getWinnerUsername());
            assertEquals(5000, result.getWinnerScore());
            assertEquals(true, result.getTeamMode());
            assertEquals("Eagles", result.getWinningTeamName());
            assertEquals(15000, result.getWinningTeamScore());
        }

        @Test
        @DisplayName("getTeamMode should return false when null")
        void teamModeNull() {
            var result = new GameResult();
            result.setTeamMode(null);
            assertFalse(result.getTeamMode());
        }

        @Test
        @DisplayName("Should set participant and question results lists")
        void setLists() {
            var result = new GameResult();
            result.setParticipantResults(new ArrayList<>());
            result.setQuestionResults(new ArrayList<>());
            assertNotNull(result.getParticipantResults());
            assertNotNull(result.getQuestionResults());
        }
    }

    @Nested
    @DisplayName("ParticipantResult")
    class ParticipantResultTests {

        @Test
        @DisplayName("Default constructor should have null fields")
        void defaultConstructor() {
            var result = new ParticipantResult();
            assertNull(result.getId());
            assertNull(result.getUsername());
        }

        @Test
        @DisplayName("Should set participant identity fields")
        void shouldSetIdentityFields() {
            var gameResult = new GameResult();
            var result = new ParticipantResult();
            result.setId(1L);
            result.setGameResult(gameResult);
            result.setUsername("player1");

            assertEquals(1L, result.getId());
            assertEquals(gameResult, result.getGameResult());
            assertEquals("player1", result.getUsername());
        }

        @Test
        @DisplayName("Should set participant score fields")
        void shouldSetScoreFields() {
            var result = new ParticipantResult();
            result.setFinalScore(3000);
            result.setFinalRank(2);
            result.setCorrectAnswers(7);
            result.setIncorrectAnswers(3);

            assertEquals(3000, result.getFinalScore());
            assertEquals(2, result.getFinalRank());
            assertEquals(7, result.getCorrectAnswers());
            assertEquals(3, result.getIncorrectAnswers());
        }

        @Test
        @DisplayName("Should set participant stats and team fields")
        void shouldSetStatsFields() {
            var result = new ParticipantResult();
            result.setMaxStreak(4);
            result.setAverageAnswerTimeSeconds(8);
            result.setTeamName("Eagles");
            result.setTeamColor("#0000FF");

            assertEquals(4, result.getMaxStreak());
            assertEquals(8, result.getAverageAnswerTimeSeconds());
            assertEquals("Eagles", result.getTeamName());
            assertEquals("#0000FF", result.getTeamColor());
        }

        @Test
        @DisplayName("Should calculate accuracy percentage")
        void accuracyPercentage() {
            var result = new ParticipantResult();
            result.setCorrectAnswers(7);
            result.setIncorrectAnswers(3);
            assertEquals(70.0, result.getAccuracyPercentage());
        }

        @Test
        @DisplayName("Should return 0 accuracy when no answers")
        void accuracyZero() {
            var result = new ParticipantResult();
            result.setCorrectAnswers(0);
            result.setIncorrectAnswers(0);
            assertEquals(0.0, result.getAccuracyPercentage());
        }
    }

    @Nested
    @DisplayName("QuestionResult")
    class QuestionResultTests {

        @Test
        @DisplayName("Default constructor should have null fields")
        void defaultConstructor() {
            var result = new QuestionResult();
            assertNull(result.getId());
            assertNull(result.getQuestionText());
        }

        @Test
        @DisplayName("Should set question identity fields")
        void shouldSetIdentityFields() {
            var gameResult = new GameResult();
            var result = new QuestionResult();
            result.setId(1L);
            result.setGameResult(gameResult);
            result.setQuestionNumber(3);
            result.setQuestionText("What is 2+2?");
            result.setQuestionType("MULTIPLE_CHOICE");

            assertEquals(1L, result.getId());
            assertEquals(gameResult, result.getGameResult());
            assertEquals(3, result.getQuestionNumber());
            assertEquals("What is 2+2?", result.getQuestionText());
            assertEquals("MULTIPLE_CHOICE", result.getQuestionType());
        }

        @Test
        @DisplayName("Should set question statistics fields")
        void shouldSetStatsFields() {
            var result = new QuestionResult();
            result.setCorrectCount(15);
            result.setIncorrectCount(3);
            result.setTimeoutCount(2);
            result.setAverageAnswerTimeSeconds(6);
            result.setFastestAnswerTimeSeconds(2);

            assertEquals(15, result.getCorrectCount());
            assertEquals(3, result.getIncorrectCount());
            assertEquals(2, result.getTimeoutCount());
            assertEquals(6, result.getAverageAnswerTimeSeconds());
            assertEquals(2, result.getFastestAnswerTimeSeconds());
        }

        @Test
        @DisplayName("Should calculate total participants")
        void totalParticipants() {
            var result = new QuestionResult();
            result.setCorrectCount(10);
            result.setIncorrectCount(5);
            result.setTimeoutCount(3);
            assertEquals(18, result.getTotalParticipants());
        }

        @Test
        @DisplayName("Should calculate success rate percentage")
        void successRate() {
            var result = new QuestionResult();
            result.setCorrectCount(15);
            result.setIncorrectCount(3);
            result.setTimeoutCount(2);
            assertEquals(75.0, result.getSuccessRatePercentage());
        }

        @Test
        @DisplayName("Should return 0 success rate when no participants")
        void successRateZero() {
            var result = new QuestionResult();
            result.setCorrectCount(0);
            result.setIncorrectCount(0);
            result.setTimeoutCount(0);
            assertEquals(0.0, result.getSuccessRatePercentage());
        }

        @Test
        @DisplayName("Should return EASY difficulty for high success rate")
        void difficultyEasy() {
            var result = new QuestionResult();
            result.setCorrectCount(80);
            result.setIncorrectCount(10);
            result.setTimeoutCount(10);
            assertEquals("EASY", result.getDifficultyLevel());
        }

        @Test
        @DisplayName("Should return MEDIUM difficulty for moderate success rate")
        void difficultyMedium() {
            var result = new QuestionResult();
            result.setCorrectCount(60);
            result.setIncorrectCount(30);
            result.setTimeoutCount(10);
            assertEquals("MEDIUM", result.getDifficultyLevel());
        }

        @Test
        @DisplayName("Should return HARD difficulty for low success rate")
        void difficultyHard() {
            var result = new QuestionResult();
            result.setCorrectCount(20);
            result.setIncorrectCount(60);
            result.setTimeoutCount(20);
            assertEquals("HARD", result.getDifficultyLevel());
        }
    }
}
