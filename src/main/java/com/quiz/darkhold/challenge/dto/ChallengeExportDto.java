package com.quiz.darkhold.challenge.dto;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for exporting/importing challenges as JSON.
 */
public class ChallengeExportDto {

    private String title;
    private String description;
    private List<QuestionExportDto> questions = new ArrayList<>();

    public static ChallengeExportDto fromEntity(final Challenge challenge) {
        var dto = new ChallengeExportDto();
        dto.setTitle(challenge.getTitle());
        dto.setDescription(challenge.getDescription());
        if (challenge.getQuestionSets() != null) {
            for (var questionSet : challenge.getQuestionSets()) {
                dto.getQuestions().add(QuestionExportDto.fromEntity(questionSet));
            }
        }
        return dto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<QuestionExportDto> getQuestions() {
        return questions;
    }

    public void setQuestions(final List<QuestionExportDto> questions) {
        this.questions = questions;
    }

    /**
     * Nested DTO for question export.
     */
    public static class QuestionExportDto {
        private String question;
        private String answer1;
        private String answer2;
        private String answer3;
        private String answer4;
        private String correctOptions;
        private QuestionType questionType;
        private Integer timeLimit;
        private Integer points;
        private String acceptableAnswers;
        private String imageUrl;
        private String videoUrl;

        @SuppressWarnings("checkstyle:MethodLength")
        public static QuestionExportDto fromEntity(final QuestionSet questionSet) {
            var dto = new QuestionExportDto();
            dto.setQuestion(questionSet.getQuestion());
            dto.setAnswer1(questionSet.getAnswer1());
            dto.setAnswer2(questionSet.getAnswer2());
            dto.setAnswer3(questionSet.getAnswer3());
            dto.setAnswer4(questionSet.getAnswer4());
            dto.setCorrectOptions(questionSet.getCorrectOptions());
            dto.setQuestionType(questionSet.getQuestionType());
            dto.setTimeLimit(questionSet.getTimeLimit());
            dto.setPoints(questionSet.getPoints());
            dto.setAcceptableAnswers(questionSet.getAcceptableAnswers());
            dto.setImageUrl(questionSet.getImageUrl());
            dto.setVideoUrl(questionSet.getVideoUrl());
            return dto;
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
}
