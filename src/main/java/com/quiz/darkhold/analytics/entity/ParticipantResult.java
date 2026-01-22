package com.quiz.darkhold.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing individual participant's performance in a game.
 */
@Entity
@Table(name = "participant_result")
public class ParticipantResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_result_id", nullable = false)
    private GameResult gameResult;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Integer finalScore;

    @Column(nullable = false)
    private Integer finalRank;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Integer incorrectAnswers;

    @Column(nullable = false)
    private Integer maxStreak;

    @Column
    private Integer averageAnswerTimeSeconds;

    @Column(length = 100)
    private String teamName;

    @Column(length = 20)
    private String teamColor;

    // Constructors
    public ParticipantResult() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public void setGameResult(final GameResult gameResult) {
        this.gameResult = gameResult;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(final Integer finalScore) {
        this.finalScore = finalScore;
    }

    public Integer getFinalRank() {
        return finalRank;
    }

    public void setFinalRank(final Integer finalRank) {
        this.finalRank = finalRank;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(final Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(final Integer incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public Integer getMaxStreak() {
        return maxStreak;
    }

    public void setMaxStreak(final Integer maxStreak) {
        this.maxStreak = maxStreak;
    }

    public Integer getAverageAnswerTimeSeconds() {
        return averageAnswerTimeSeconds;
    }

    public void setAverageAnswerTimeSeconds(final Integer averageAnswerTimeSeconds) {
        this.averageAnswerTimeSeconds = averageAnswerTimeSeconds;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(final String teamColor) {
        this.teamColor = teamColor;
    }

    /**
     * Calculate accuracy percentage.
     *
     * @return accuracy as percentage (0-100)
     */
    public double getAccuracyPercentage() {
        int total = correctAnswers + incorrectAnswers;
        if (total == 0) {
            return 0.0;
        }
        return (correctAnswers * 100.0) / total;
    }
}
