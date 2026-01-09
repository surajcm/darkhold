package com.quiz.darkhold.game.service;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Locale;


/**
 * Service for validating answers based on question type.
 * Supports fuzzy matching for TYPE_ANSWER questions using Levenshtein distance.
 */
@Service
public class AnswerValidationService {

    private final Logger logger = LogManager.getLogger(AnswerValidationService.class);
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    /**
     * Maximum allowed Levenshtein distance for short answers (up to 5 chars).
     */
    private static final int SHORT_ANSWER_TOLERANCE = 1;

    /**
     * Maximum allowed Levenshtein distance for medium answers (6-10 chars).
     */
    private static final int MEDIUM_ANSWER_TOLERANCE = 2;

    /**
     * Maximum proportion of characters that can differ for long answers.
     */
    private static final double LONG_ANSWER_TOLERANCE_RATIO = 0.2;

    /**
     * Validate a user's answer against the correct answer(s) for a question.
     *
     * @param question   the question being answered
     * @param userAnswer the user's submitted answer
     * @return true if the answer is correct (or close enough for TYPE_ANSWER)
     */
    public boolean validateAnswer(final QuestionSet question, final String userAnswer) {
        if (question == null || userAnswer == null) {
            return false;
        }

        var questionType = question.getQuestionType();
        if (questionType == null) {
            questionType = QuestionType.MULTIPLE_CHOICE;
        }

        return switch (questionType) {
            case MULTIPLE_CHOICE -> validateMultipleChoice(question, userAnswer);
            case TRUE_FALSE -> validateTrueFalse(question, userAnswer);
            case TYPE_ANSWER -> validateTypeAnswer(question, userAnswer);
            case POLL -> true; // Polls have no correct answer
        };
    }

    /**
     * Check if this question type should be scored.
     */
    public boolean shouldScore(final QuestionSet question) {
        if (question == null || question.getQuestionType() == null) {
            return true;
        }
        return question.getQuestionType() != QuestionType.POLL;
    }

    /**
     * Get the points value for a question (defaults to 1000).
     */
    public int getPoints(final QuestionSet question) {
        if (question == null || question.getPoints() == null) {
            return 1000;
        }
        return question.getPoints();
    }

    private boolean validateMultipleChoice(final QuestionSet question, final String userAnswer) {
        var correctOptions = question.getCorrectOptions();
        if (correctOptions == null || correctOptions.isBlank()) {
            return false;
        }
        // User answer is option letter(s) like "A" or "A,B"
        var userOptions = userAnswer.toUpperCase(Locale.ROOT).trim();
        return correctOptions.equalsIgnoreCase(userOptions);
    }

    private boolean validateTrueFalse(final QuestionSet question, final String userAnswer) {
        var correctAnswer = question.getCorrectOptions();
        if (correctAnswer == null || correctAnswer.isBlank()) {
            return false;
        }
        // Correct is stored as "TRUE" or "FALSE"
        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }

    private boolean validateTypeAnswer(final QuestionSet question, final String userAnswer) {
        var correctAnswer = question.getAnswer1();
        if (correctAnswer == null || correctAnswer.isBlank()) {
            logger.warn("TYPE_ANSWER question has no correct answer set");
            return false;
        }
        var normalizedUserAnswer = normalizeAnswer(userAnswer);
        if (isFuzzyMatch(normalizedUserAnswer, normalizeAnswer(correctAnswer))) {
            return true;
        }
        return checkAlternatives(question.getAcceptableAnswers(), normalizedUserAnswer);
    }

    private boolean checkAlternatives(final String acceptableAnswers, final String userAnswer) {
        if (acceptableAnswers == null || acceptableAnswers.isBlank()) {
            return false;
        }
        for (var alt : acceptableAnswers.split(",", -1)) {
            if (isFuzzyMatch(userAnswer, normalizeAnswer(alt))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Normalize an answer for comparison: trim, lowercase, remove extra spaces.
     */
    private String normalizeAnswer(final String answer) {
        if (answer == null) {
            return "";
        }
        return answer.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    /**
     * Check if two answers match using fuzzy matching with Levenshtein distance.
     */
    private boolean isFuzzyMatch(final String userAnswer, final String correctAnswer) {
        if (userAnswer.equals(correctAnswer)) {
            return true;
        }

        int distance = levenshtein.apply(userAnswer, correctAnswer);
        int maxLength = Math.max(userAnswer.length(), correctAnswer.length());
        int tolerance = calculateTolerance(maxLength);

        var isMatch = distance <= tolerance;
        if (isMatch && distance > 0) {
            logger.debug("Fuzzy match: '{}' -> '{}' (distance: {})", userAnswer, correctAnswer, distance);
        }
        return isMatch;
    }

    /**
     * Calculate the allowed Levenshtein distance based on answer length.
     */
    private int calculateTolerance(final int length) {
        if (length <= 5) {
            return SHORT_ANSWER_TOLERANCE;
        } else if (length <= 10) {
            return MEDIUM_ANSWER_TOLERANCE;
        } else {
            return (int) Math.ceil(length * LONG_ANSWER_TOLERANCE_RATIO);
        }
    }
}
