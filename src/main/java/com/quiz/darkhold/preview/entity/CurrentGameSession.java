package com.quiz.darkhold.preview.entity;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.GameMode;
import com.quiz.darkhold.game.model.GameStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
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

    @Column(columnDefinition = "TEXT")
    private String streakJson;

    @Column(columnDefinition = "TEXT")
    private String previousScoresJson;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GameStatus gameStatus = GameStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GameMode gameMode = GameMode.MULTIPLAYER;

    private Long pausedAt;

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
        } catch (JacksonException ex) {
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
            } catch (JacksonException ex) {
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
        } catch (JacksonException ex) {
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
            } catch (JacksonException ex) {
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
        } catch (JacksonException ex) {
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
            } catch (JacksonException ex) {
                logger.error("Error serializing scores map", ex);
            }
        }
    }

    // Streak map helpers
    public String getStreakJson() {
        return streakJson;
    }

    public void setStreakJson(final String streakJson) {
        this.streakJson = streakJson;
    }

    public Map<String, Integer> getStreakMap() {
        if (streakJson == null || streakJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(streakJson, new TypeReference<Map<String, Integer>>() { });
        } catch (JacksonException ex) {
            logger.error("Error deserializing streak map", ex);
            return new HashMap<>();
        }
    }

    public void setStreakMap(final Map<String, Integer> streaks) {
        if (streaks == null) {
            this.streakJson = null;
        } else {
            try {
                this.streakJson = objectMapper.writeValueAsString(streaks);
            } catch (JacksonException ex) {
                logger.error("Error serializing streak map", ex);
            }
        }
    }

    // Previous scores map helpers
    public String getPreviousScoresJson() {
        return previousScoresJson;
    }

    public void setPreviousScoresJson(final String previousScoresJson) {
        this.previousScoresJson = previousScoresJson;
    }

    public Map<String, Integer> getPreviousScoresMap() {
        if (previousScoresJson == null || previousScoresJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(previousScoresJson,
                    new TypeReference<Map<String, Integer>>() { });
        } catch (JacksonException ex) {
            logger.error("Error deserializing previous scores map", ex);
            return new HashMap<>();
        }
    }

    public void setPreviousScoresMap(final Map<String, Integer> previousScores) {
        if (previousScores == null) {
            this.previousScoresJson = null;
        } else {
            try {
                this.previousScoresJson = objectMapper.writeValueAsString(previousScores);
            } catch (JacksonException ex) {
                logger.error("Error serializing previous scores map", ex);
            }
        }
    }

    // Game status helpers
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameMode getGameMode() {
        return gameMode != null ? gameMode : GameMode.MULTIPLAYER;
    }

    public void setGameMode(final GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Long getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(final Long pausedAt) {
        this.pausedAt = pausedAt;
    }
}

