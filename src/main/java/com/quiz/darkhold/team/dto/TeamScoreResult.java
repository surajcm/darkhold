package com.quiz.darkhold.team.dto;

import com.quiz.darkhold.game.model.ScoreResult;
import java.util.ArrayList;
import java.util.List;

public class TeamScoreResult {
    private String teamName;
    private String color;
    private Integer totalScore;
    private Integer previousScore;
    private Integer scoreDelta;
    private Integer rank;
    private Integer previousRank;
    private Integer rankChange;
    private List<ScoreResult> individualScores;

    public TeamScoreResult() {
        this.individualScores = new ArrayList<>();
        this.totalScore = 0;
        this.previousScore = 0;
    }

    public TeamScoreResult(final String teamName, final String color,
                           final Integer totalScore, final Integer rank) {
        this.teamName = teamName;
        this.color = color;
        this.totalScore = totalScore;
        this.rank = rank;
        this.individualScores = new ArrayList<>();
        this.previousScore = 0;
        this.previousRank = rank;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(final Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(final Integer previousScore) {
        this.previousScore = previousScore;
    }

    public Integer getScoreDelta() {
        return scoreDelta;
    }

    public void setScoreDelta(final Integer scoreDelta) {
        this.scoreDelta = scoreDelta;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(final Integer rank) {
        this.rank = rank;
    }

    public Integer getPreviousRank() {
        return previousRank;
    }

    public void setPreviousRank(final Integer previousRank) {
        this.previousRank = previousRank;
    }

    public Integer getRankChange() {
        return rankChange;
    }

    public void setRankChange(final Integer rankChange) {
        this.rankChange = rankChange;
    }

    public List<ScoreResult> getIndividualScores() {
        return individualScores;
    }

    public void setIndividualScores(final List<ScoreResult> individualScores) {
        this.individualScores = individualScores;
    }

    public void addIndividualScore(final ScoreResult scoreResult) {
        this.individualScores.add(scoreResult);
    }

    public boolean isRankUp() {
        return rankChange != null && rankChange > 0;
    }

    public boolean isRankDown() {
        return rankChange != null && rankChange < 0;
    }

    public int getMemberCount() {
        return individualScores.size();
    }
}
