package com.quiz.darkhold.challenge.dto;

import com.quiz.darkhold.challenge.entity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating/updating a question.
 */
public class QuestionRequest {

    @NotNull(message = "Challenge ID is required")
    private Long challengeId;

    @NotBlank(message = "Question text is required")
    @Size(max = 425, message = "Question must be at most 425 characters")
    private String question;

    @Size(max = 225, message = "Answer must be at most 225 characters")
    private String answer1;

    @Size(max = 225, message = "Answer must be at most 225 characters")
    private String answer2;

    @Size(max = 225, message = "Answer must be at most 225 characters")
    private String answer3;

    @Size(max = 225, message = "Answer must be at most 225 characters")
    private String answer4;

    private String correctOptions;

    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    private Integer timeLimit;

    private Integer points = 1000;

    @Size(max = 500, message = "Acceptable answers must be at most 500 characters")
    private String acceptableAnswers;

    @Size(max = 500, message = "Image URL must be at most 500 characters")
    private String imageUrl;

    @Size(max = 500, message = "Video URL must be at most 500 characters")
    private String videoUrl;

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(final Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(final String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(final String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(final String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(final String answer4) {
        this.answer4 = answer4;
    }

    public String getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(final String correctOptions) {
        this.correctOptions = correctOptions;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(final QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(final Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(final Integer points) {
        this.points = points;
    }

    public String getAcceptableAnswers() {
        return acceptableAnswers;
    }

    public void setAcceptableAnswers(final String acceptableAnswers) {
        this.acceptableAnswers = acceptableAnswers;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(final String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
