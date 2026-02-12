package com.quiz.darkhold.challenge.dto;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ChallengeExportDto Tests")
class ChallengeExportDtoTest {

    @Test
    @DisplayName("Should create from entity with questions")
    void fromEntityWithQuestions() {
        var qs = createTestQuestionSet();
        var challenge = new Challenge();
        challenge.setTitle("Math Quiz");
        challenge.setDescription("Basic math");
        challenge.setQuestionSets(List.of(qs));

        var dto = ChallengeExportDto.fromEntity(challenge);

        assertEquals("Math Quiz", dto.getTitle());
        assertEquals("Basic math", dto.getDescription());
        assertEquals(1, dto.getQuestions().size());
    }

    @Test
    @DisplayName("Should map question answer fields")
    void shouldMapAnswerFields() {
        var qs = createTestQuestionSet();
        var challenge = new Challenge();
        challenge.setQuestionSets(List.of(qs));
        var dto = ChallengeExportDto.fromEntity(challenge);

        var questionDto = dto.getQuestions().get(0);
        assertEquals("What is 2+2?", questionDto.getQuestion());
        assertEquals("4", questionDto.getAnswer1());
        assertEquals("3", questionDto.getAnswer2());
        assertEquals("5", questionDto.getAnswer3());
        assertEquals("6", questionDto.getAnswer4());
        assertEquals("A", questionDto.getCorrectOptions());
    }

    @Test
    @DisplayName("Should map question metadata fields")
    void shouldMapMetadataFields() {
        var qs = createTestQuestionSet();
        qs.setAcceptableAnswers("four");
        var challenge = new Challenge();
        challenge.setQuestionSets(List.of(qs));
        var dto = ChallengeExportDto.fromEntity(challenge);

        var questionDto = dto.getQuestions().get(0);
        assertEquals(QuestionType.MULTIPLE_CHOICE, questionDto.getQuestionType());
        assertEquals(20, questionDto.getTimeLimit());
        assertEquals(1000, questionDto.getPoints());
        assertEquals("four", questionDto.getAcceptableAnswers());
        assertEquals("img.png", questionDto.getImageUrl());
        assertEquals("vid.mp4", questionDto.getVideoUrl());
    }

    private QuestionSet createTestQuestionSet() {
        var qs = new QuestionSet();
        qs.setQuestion("What is 2+2?");
        qs.setAnswer1("4");
        qs.setAnswer2("3");
        qs.setAnswer3("5");
        qs.setAnswer4("6");
        qs.setCorrectOptions("A");
        qs.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        qs.setTimeLimit(20);
        qs.setPoints(1000);
        qs.setImageUrl("img.png");
        qs.setVideoUrl("vid.mp4");
        return qs;
    }

    @Test
    @DisplayName("Should handle null question sets")
    void fromEntityNullQuestions() {
        var challenge = new Challenge();
        challenge.setTitle("Empty");
        challenge.setDescription("No questions");
        challenge.setQuestionSets(null);

        var dto = ChallengeExportDto.fromEntity(challenge);

        assertEquals("Empty", dto.getTitle());
        assertTrue(dto.getQuestions().isEmpty());
    }

    @Test
    @DisplayName("Should support setters on ChallengeExportDto")
    void setters() {
        var dto = new ChallengeExportDto();
        dto.setTitle("T");
        dto.setDescription("D");
        dto.setQuestions(List.of());

        assertEquals("T", dto.getTitle());
        assertEquals("D", dto.getDescription());
        assertNotNull(dto.getQuestions());
    }

    @Test
    @DisplayName("Should support setters on QuestionExportDto basic fields")
    void questionExportSettersBasic() {
        var questionDto = new ChallengeExportDto.QuestionExportDto();
        questionDto.setQuestion("Q");
        questionDto.setAnswer1("A1");
        questionDto.setAnswer2("A2");
        questionDto.setAnswer3("A3");
        questionDto.setAnswer4("A4");
        questionDto.setCorrectOptions("A");

        assertEquals("Q", questionDto.getQuestion());
        assertEquals("A1", questionDto.getAnswer1());
        assertEquals("A2", questionDto.getAnswer2());
        assertEquals("A3", questionDto.getAnswer3());
        assertEquals("A4", questionDto.getAnswer4());
        assertEquals("A", questionDto.getCorrectOptions());
    }

    @Test
    @DisplayName("Should support setters on QuestionExportDto extended fields")
    void questionExportSettersExtended() {
        var questionDto = new ChallengeExportDto.QuestionExportDto();
        questionDto.setQuestionType(QuestionType.TRUE_FALSE);
        questionDto.setTimeLimit(10);
        questionDto.setPoints(500);
        questionDto.setAcceptableAnswers("alt");
        questionDto.setImageUrl("i.png");
        questionDto.setVideoUrl("v.mp4");

        assertEquals(QuestionType.TRUE_FALSE, questionDto.getQuestionType());
        assertEquals(10, questionDto.getTimeLimit());
        assertEquals(500, questionDto.getPoints());
        assertEquals("alt", questionDto.getAcceptableAnswers());
        assertEquals("i.png", questionDto.getImageUrl());
        assertEquals("v.mp4", questionDto.getVideoUrl());
    }
}
