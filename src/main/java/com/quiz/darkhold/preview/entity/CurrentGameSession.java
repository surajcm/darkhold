package com.quiz.darkhold.preview.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "current_game_session")
public class CurrentGameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pin;

    @Column(nullable = false)
    private String moderator;

    @Column(columnDefinition = "TEXT")
    private String usersJson;

    @Column(columnDefinition = "TEXT")
    private String questionsJson;

    @Column(nullable = false)
    private Integer currentQuestionNo;

    @Column(columnDefinition = "TEXT")
    private String scoresJson;

    private static final Logger logger = LogManager.getLogger(CurrentGameSession.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Constructors
    public CurrentGameSession() {
    }

    public CurrentGameSession(final String pin, final String moderator) {
        this.pin = pin;
        this.moderator = moderator;
        this.currentQuestionNo = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(final String pin) {
        this.pin = pin;
    }

    public String getModerator() {
        return moderator;
    }

    public void setModerator(final String moderator) {
        this.moderator = moderator;
    }

    public String getUsersJson() {
        return usersJson;
    }

    public void setUsersJson(final String usersJson) {
        this.usersJson = usersJson;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(final String questionsJson) {
        this.questionsJson = questionsJson;
    }

    public Integer getCurrentQuestionNo() {
        return currentQuestionNo;
    }

    public void setCurrentQuestionNo(final Integer currentQuestionNo) {
        this.currentQuestionNo = currentQuestionNo;
    }

    public String getScoresJson() {
        return scoresJson;
    }

    public void setScoresJson(final String scoresJson) {
        this.scoresJson = scoresJson;
    }

    // Helper methods for JSON serialization
    @SuppressWarnings("unchecked")
    public List<String> getUsersList() {
        if (usersJson == null || usersJson.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(usersJson, List.class);
        } catch (JsonProcessingException ex) {
            logger.error("Error deserializing users list", ex);
            return null;
        }
    }

    public void setUsersList(final List<String> users) {
        if (users == null) {
            this.usersJson = null;
        } else {
            try {
                this.usersJson = objectMapper.writeValueAsString(users);
            } catch (JsonProcessingException ex) {
                logger.error("Error serializing users list", ex);
            }
        }
    }

    public List<QuestionSet> getQuestionsList() {
        if (questionsJson == null || questionsJson.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(questionsJson, new TypeReference<List<QuestionSet>>() {});
        } catch (JsonProcessingException ex) {
            logger.error("Error deserializing questions list", ex);
            return null;
        }
    }

    public void setQuestionsList(final List<QuestionSet> questions) {
        if (questions == null) {
            this.questionsJson = null;
        } else {
            try {
                this.questionsJson = objectMapper.writeValueAsString(questions);
            } catch (JsonProcessingException ex) {
                logger.error("Error serializing questions list", ex);
            }
        }
    }

    public Map<String, Integer> getScoresMap() {
        if (scoresJson == null || scoresJson.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(scoresJson, new TypeReference<Map<String, Integer>>() {});
        } catch (JsonProcessingException ex) {
            logger.error("Error deserializing scores map", ex);
            return null;
        }
    }

    public void setScoresMap(final Map<String, Integer> scores) {
        if (scores == null) {
            this.scoresJson = null;
        } else {
            try {
                this.scoresJson = objectMapper.writeValueAsString(scores);
            } catch (JsonProcessingException ex) {
                logger.error("Error serializing scores map", ex);
            }
        }
    }
}

