package com.quiz.darkhold.analytics.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a completed game session with all results.
 */
@Entity
@Table(name = "game_result")
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    private String challengeId;

    @Column(nullable = false)
    private String challengeName;

    @Column(nullable = false)
    private String moderator;

    @Column(nullable = false)
    private String gameMode;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer participantCount;

    @CreatedDate
    @Column(name = "startedAt", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completedAt", nullable = false)
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column
    private String winnerUsername;

    @Column
    private Integer winnerScore;

    @OneToMany(mappedBy = "gameResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantResult> participantResults = new ArrayList<>();

    @OneToMany(mappedBy = "gameResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionResult> questionResults = new ArrayList<>();

    // Constructors
    public GameResult() {
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

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(final String challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(final String challengeName) {
        this.challengeName = challengeName;
    }

    public String getModerator() {
        return moderator;
    }

    public void setModerator(final String moderator) {
        this.moderator = moderator;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(final String gameMode) {
        this.gameMode = gameMode;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(final Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(final Integer participantCount) {
        this.participantCount = participantCount;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(final LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(final LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(final Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public void setWinnerUsername(final String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public Integer getWinnerScore() {
        return winnerScore;
    }

    public void setWinnerScore(final Integer winnerScore) {
        this.winnerScore = winnerScore;
    }

    public List<ParticipantResult> getParticipantResults() {
        return participantResults;
    }

    public void setParticipantResults(final List<ParticipantResult> participantResults) {
        this.participantResults = participantResults;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(final List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }
}
