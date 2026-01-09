package com.quiz.darkhold.game.model;

/**
 * Enhanced score result DTO with rank changes, streak info, and score deltas.
 * Used for scoreboard display with animations and detailed feedback.
 */
public class ScoreResult {

    private String username;
    private int currentScore;
    private int previousScore;
    private int scoreDelta;
    private int rank;
    private int previousRank;
    private int rankChange;
    private int streak;
    private int streakMultiplier;
    private int basePoints;
    private int bonusPoints;

    public ScoreResult() {
    }

    /**
     * Builder-style constructor for creating a complete ScoreResult.
     */
    public ScoreResult(final String username, final int currentScore, final int previousScore,
                       final int rank, final int previousRank, final int streak) {
        this.username = username;
        this.currentScore = currentScore;
        this.previousScore = previousScore;
        this.scoreDelta = currentScore - previousScore;
        this.rank = rank;
        this.previousRank = previousRank;
        this.rankChange = previousRank - rank; // Positive means moved up
        this.streak = streak;
        this.streakMultiplier = calculateStreakMultiplier(streak);
    }

    /**
     * Calculate the streak multiplier based on consecutive correct answers.
     * 0-1: 1x, 2-3: 2x, 4-5: 3x, 6+: 4x
     */
    public static int calculateStreakMultiplier(final int streak) {
        if (streak <= 1) {
            return 1;
        } else if (streak <= 3) {
            return 2;
        } else if (streak <= 5) {
            return 3;
        } else {
            return 4;
        }
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(final int currentScore) {
        this.currentScore = currentScore;
    }

    public int getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(final int previousScore) {
        this.previousScore = previousScore;
    }

    public int getScoreDelta() {
        return scoreDelta;
    }

    public void setScoreDelta(final int scoreDelta) {
        this.scoreDelta = scoreDelta;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public int getPreviousRank() {
        return previousRank;
    }

    public void setPreviousRank(final int previousRank) {
        this.previousRank = previousRank;
    }

    public int getRankChange() {
        return rankChange;
    }

    public void setRankChange(final int rankChange) {
        this.rankChange = rankChange;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(final int streak) {
        this.streak = streak;
        this.streakMultiplier = calculateStreakMultiplier(streak);
    }

    public int getStreakMultiplier() {
        return streakMultiplier;
    }

    public void setStreakMultiplier(final int streakMultiplier) {
        this.streakMultiplier = streakMultiplier;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(final int basePoints) {
        this.basePoints = basePoints;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(final int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    /**
     * Checks if the player moved up in rank.
     */
    public boolean isRankUp() {
        return rankChange > 0;
    }

    /**
     * Checks if the player moved down in rank.
     */
    public boolean isRankDown() {
        return rankChange < 0;
    }

    /**
     * Checks if the player has an active streak (2+ correct).
     */
    public boolean hasStreak() {
        return streak >= 2;
    }

    /**
     * Returns display string for streak (e.g., "x3" for 3-streak).
     */
    public String getStreakDisplay() {
        if (streak < 2) {
            return "";
        }
        return "x" + streakMultiplier;
    }
}
