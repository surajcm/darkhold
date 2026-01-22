package com.quiz.darkhold.team.entity;

import com.quiz.darkhold.analytics.entity.GameResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "team_result")
public class TeamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_result_id", nullable = false)
    private GameResult gameResult;

    @Column(nullable = false, length = 100)
    private String teamName;

    @Column(nullable = false, length = 20)
    private String teamColor;

    @Column(nullable = false)
    private Integer finalScore;

    @Column(nullable = false)
    private Integer finalRank;

    @Column(nullable = false)
    private Integer memberCount;

    @Column(nullable = false)
    private Integer averageScorePerMember;

    public TeamResult() {
    }

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

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(final Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getAverageScorePerMember() {
        return averageScorePerMember;
    }

    public void setAverageScorePerMember(final Integer averageScorePerMember) {
        this.averageScorePerMember = averageScorePerMember;
    }
}
