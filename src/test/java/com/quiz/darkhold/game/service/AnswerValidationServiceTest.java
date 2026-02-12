package com.quiz.darkhold.game.service;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AnswerValidationService Tests")
class AnswerValidationServiceTest {

    private AnswerValidationService service;

    @BeforeEach
    void setUp() {
        service = new AnswerValidationService();
    }

    @Nested
    @DisplayName("validateAnswer - null handling")
    class NullHandling {

        @Test
        @DisplayName("Should return false for null question")
        void nullQuestion() {
            assertFalse(service.validateAnswer(null, "A"));
        }

        @Test
        @DisplayName("Should return false for null answer")
        void nullAnswer() {
            var qs = makeQuestion(QuestionType.MULTIPLE_CHOICE, "A", null);
            assertFalse(service.validateAnswer(qs, null));
        }

        @Test
        @DisplayName("Should default to MULTIPLE_CHOICE when type is null")
        void nullQuestionType() {
            var qs = makeQuestion(null, "A", null);
            qs.setCorrectOptions("A");
            assertTrue(service.validateAnswer(qs, "A"));
        }
    }

    @Nested
    @DisplayName("validateAnswer - MULTIPLE_CHOICE")
    class MultipleChoice {

        @Test
        @DisplayName("Should match correct option ignoring case")
        void correctAnswer() {
            var qs = makeQuestion(QuestionType.MULTIPLE_CHOICE, "A", null);
            qs.setCorrectOptions("A");
            assertTrue(service.validateAnswer(qs, "a"));
        }

        @Test
        @DisplayName("Should reject wrong option")
        void wrongAnswer() {
            var qs = makeQuestion(QuestionType.MULTIPLE_CHOICE, "A", null);
            qs.setCorrectOptions("A");
            assertFalse(service.validateAnswer(qs, "B"));
        }

        @Test
        @DisplayName("Should return false for blank correct options")
        void blankCorrectOptions() {
            var qs = makeQuestion(QuestionType.MULTIPLE_CHOICE, "A", null);
            qs.setCorrectOptions("");
            assertFalse(service.validateAnswer(qs, "A"));
        }

        @Test
        @DisplayName("Should return false for null correct options")
        void nullCorrectOptions() {
            var qs = makeQuestion(QuestionType.MULTIPLE_CHOICE, "A", null);
            qs.setCorrectOptions(null);
            assertFalse(service.validateAnswer(qs, "A"));
        }

        @Test
        @DisplayName("Should handle multi-option answers")
        void multiOption() {
            var qs = makeQuestion(QuestionType.MULTIPLE_CHOICE, "A", null);
            qs.setCorrectOptions("A,B");
            assertTrue(service.validateAnswer(qs, "a,b"));
        }
    }

    @Nested
    @DisplayName("validateAnswer - TRUE_FALSE")
    class TrueFalse {

        @Test
        @DisplayName("Should accept correct TRUE answer")
        void trueAnswer() {
            var qs = makeQuestion(QuestionType.TRUE_FALSE, "A", null);
            qs.setCorrectOptions("TRUE");
            assertTrue(service.validateAnswer(qs, "true"));
        }

        @Test
        @DisplayName("Should accept correct FALSE answer")
        void falseAnswer() {
            var qs = makeQuestion(QuestionType.TRUE_FALSE, "A", null);
            qs.setCorrectOptions("FALSE");
            assertTrue(service.validateAnswer(qs, "false"));
        }

        @Test
        @DisplayName("Should reject wrong true/false answer")
        void wrongAnswer() {
            var qs = makeQuestion(QuestionType.TRUE_FALSE, "A", null);
            qs.setCorrectOptions("TRUE");
            assertFalse(service.validateAnswer(qs, "false"));
        }

        @Test
        @DisplayName("Should return false for null correct options")
        void nullCorrectOptions() {
            var qs = makeQuestion(QuestionType.TRUE_FALSE, "A", null);
            qs.setCorrectOptions(null);
            assertFalse(service.validateAnswer(qs, "TRUE"));
        }

        @Test
        @DisplayName("Should return false for blank correct options")
        void blankCorrectOptions() {
            var qs = makeQuestion(QuestionType.TRUE_FALSE, "A", null);
            qs.setCorrectOptions("  ");
            assertFalse(service.validateAnswer(qs, "TRUE"));
        }
    }

    @Nested
    @DisplayName("validateAnswer - TYPE_ANSWER")
    class TypeAnswer {

        @Test
        @DisplayName("Should accept exact match")
        void exactMatch() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Paris", null);
            assertTrue(service.validateAnswer(qs, "Paris"));
        }

        @Test
        @DisplayName("Should accept case-insensitive match")
        void caseInsensitive() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Paris", null);
            assertTrue(service.validateAnswer(qs, "paris"));
        }

        @Test
        @DisplayName("Should accept fuzzy match within tolerance")
        void fuzzyMatch() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Paris", null);
            // 1 char off for 5-char answer => tolerance 1
            assertTrue(service.validateAnswer(qs, "Pais"));
        }

        @Test
        @DisplayName("Should reject answer far beyond tolerance")
        void beyondTolerance() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Paris", null);
            assertFalse(service.validateAnswer(qs, "London"));
        }

        @Test
        @DisplayName("Should return false when answer1 is null")
        void nullAnswer1() {
            var qs = new QuestionSet();
            qs.setQuestionType(QuestionType.TYPE_ANSWER);
            qs.setAnswer1(null);
            assertFalse(service.validateAnswer(qs, "test"));
        }

        @Test
        @DisplayName("Should return false when answer1 is blank")
        void blankAnswer1() {
            var qs = new QuestionSet();
            qs.setQuestionType(QuestionType.TYPE_ANSWER);
            qs.setAnswer1("  ");
            assertFalse(service.validateAnswer(qs, "test"));
        }

        @Test
        @DisplayName("Should accept acceptable alternative")
        void acceptableAlternative() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "USA", "United States,America");
            assertTrue(service.validateAnswer(qs, "United States"));
        }

        @Test
        @DisplayName("Should reject when no alternatives match")
        void noAlternativesMatch() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "USA", "United States");
            assertFalse(service.validateAnswer(qs, "Canada"));
        }

        @Test
        @DisplayName("Should handle null acceptable answers")
        void nullAcceptableAnswers() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Paris", null);
            assertFalse(service.validateAnswer(qs, "London"));
        }

        @Test
        @DisplayName("Should handle blank acceptable answers")
        void blankAcceptableAnswers() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Paris", "  ");
            assertFalse(service.validateAnswer(qs, "London"));
        }

        @Test
        @DisplayName("Should fuzzy match medium-length answers")
        void fuzzyMediumLength() {
            // 8-char answer => tolerance 2
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Elephant", null);
            assertTrue(service.validateAnswer(qs, "Elephnt"));
        }

        @Test
        @DisplayName("Should fuzzy match long answers")
        void fuzzyLongLength() {
            // 15-char answer => tolerance ceil(15*0.2)=3
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "Constantinople", null);
            assertTrue(service.validateAnswer(qs, "Constantnople"));
        }

        @Test
        @DisplayName("Should trim and normalize whitespace")
        void normalizeWhitespace() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "New York", null);
            assertTrue(service.validateAnswer(qs, "  new   york  "));
        }
    }

    @Nested
    @DisplayName("validateAnswer - POLL")
    class Poll {

        @Test
        @DisplayName("Should always return true for polls")
        void pollAlwaysTrue() {
            var qs = makeQuestion(QuestionType.POLL, "A", null);
            assertTrue(service.validateAnswer(qs, "anything"));
        }
    }

    @Nested
    @DisplayName("shouldScore")
    class ShouldScore {

        @Test
        @DisplayName("Should return true for MULTIPLE_CHOICE")
        void multipleChoice() {
            var qs = makeQuestion(QuestionType.MULTIPLE_CHOICE, "A", null);
            assertTrue(service.shouldScore(qs));
        }

        @Test
        @DisplayName("Should return true for TRUE_FALSE")
        void trueFalse() {
            var qs = makeQuestion(QuestionType.TRUE_FALSE, "A", null);
            assertTrue(service.shouldScore(qs));
        }

        @Test
        @DisplayName("Should return true for TYPE_ANSWER")
        void typeAnswer() {
            var qs = makeQuestion(QuestionType.TYPE_ANSWER, "A", null);
            assertTrue(service.shouldScore(qs));
        }

        @Test
        @DisplayName("Should return false for POLL")
        void poll() {
            var qs = makeQuestion(QuestionType.POLL, "A", null);
            assertFalse(service.shouldScore(qs));
        }

        @Test
        @DisplayName("Should return true for null question")
        void nullQuestion() {
            assertTrue(service.shouldScore(null));
        }

        @Test
        @DisplayName("Should return true for null question type")
        void nullQuestionType() {
            var qs = new QuestionSet();
            qs.setQuestionType(null);
            assertTrue(service.shouldScore(qs));
        }
    }

    @Nested
    @DisplayName("getPoints")
    class GetPoints {

        @Test
        @DisplayName("Should return question points when set")
        void withPoints() {
            var qs = new QuestionSet();
            qs.setPoints(500);
            assertEquals(500, service.getPoints(qs));
        }

        @Test
        @DisplayName("Should return 1000 default when points null")
        void nullPoints() {
            var qs = new QuestionSet();
            qs.setPoints(null);
            assertEquals(1000, service.getPoints(qs));
        }

        @Test
        @DisplayName("Should return 1000 default for null question")
        void nullQuestion() {
            assertEquals(1000, service.getPoints(null));
        }
    }

    // Helper to build a QuestionSet quickly
    private QuestionSet makeQuestion(final QuestionType type, final String answer1,
                                     final String acceptableAnswers) {
        var qs = new QuestionSet();
        qs.setQuestionType(type);
        qs.setAnswer1(answer1);
        qs.setAcceptableAnswers(acceptableAnswers);
        return qs;
    }
}
