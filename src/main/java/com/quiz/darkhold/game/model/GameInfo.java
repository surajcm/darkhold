package com.quiz.darkhold.game.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO for displaying game information in the dashboard.
 */
public class GameInfo {
    private String pin;
    private String status;
    private String challengeName;
    private String challengeId;
    private LocalDateTime createdOn;
    private int participantCount;
    private String gameMode;

    public GameInfo() {
    }

    public GameInfo(final String pin, final String status, final String challengeName,
                    final String challengeId, final LocalDateTime createdOn,
                    final int participantCount, final String gameMode) {
        this.pin = pin;
        this.status = status;
        this.challengeName = challengeName;
        this.challengeId = challengeId;
        this.createdOn = createdOn;
        this.participantCount = participantCount;
        this.gameMode = gameMode;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(final String pin) {
        this.pin = pin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(final String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(final String challengeId) {
        this.challengeId = challengeId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(final int participantCount) {
        this.participantCount = participantCount;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(final String gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Get formatted creation date.
     *
     * @return formatted date string
     */
    public String getFormattedCreatedOn() {
        if (createdOn == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return createdOn.format(formatter);
    }

    /**
     * Get status badge class for UI styling.
     *
     * @return CSS class name
     */
    public String getStatusBadgeClass() {
        return switch (status) {
            case "WAITING" -> "badge-info";
            case "IN_PROGRESS" -> "badge-success";
            case "PAUSED" -> "badge-warning";
            case "FINISHED" -> "badge-secondary";
            default -> "badge-light";
        };
    }

    /**
     * Get display name for game mode.
     *
     * @return display name
     */
    public String getGameModeDisplay() {
        return "PRACTICE".equals(gameMode) ? "Practice" : "Multiplayer";
    }
}
