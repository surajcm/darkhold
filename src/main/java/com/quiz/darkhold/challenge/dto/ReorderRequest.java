package com.quiz.darkhold.challenge.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for reordering questions.
 */
public class ReorderRequest {

    @NotNull(message = "Challenge ID is required")
    private Long challengeId;

    @NotNull(message = "Question IDs list is required")
    private List<Long> questionIds;

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(final Long challengeId) {
        this.challengeId = challengeId;
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(final List<Long> questionIds) {
        this.questionIds = questionIds;
    }
}
