package com.quiz.darkhold.challenge.entity;

/**
 * Enum representing the different types of questions supported in the quiz.
 */
public enum QuestionType {
    /**
     * Standard multiple choice with 2-4 options, one or more correct answers.
     */
    MULTIPLE_CHOICE,

    /**
     * True or False question with exactly 2 options.
     */
    TRUE_FALSE,

    /**
     * Free text answer with fuzzy matching support.
     */
    TYPE_ANSWER,

    /**
     * Poll question - multiple choice but no correct answer, shows result distribution.
     */
    POLL
}
