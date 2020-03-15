package com.quiz.darkhold.game.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @Column
    private String pin;

    @Column
    private String gameStatus;

    @Column
    private String challengeId;

    @Column(name = "createdOn")
    private OffsetDateTime createdOn;

    @Column(name = "modifiedOn")
    private OffsetDateTime modifiedOn;

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

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(final String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(final String challengeId) {
        this.challengeId = challengeId;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public OffsetDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(final OffsetDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    /**
     * initialize / update date fields.
     */
    @PrePersist
    @PreUpdate
    public void initializeDate() {
        if (this.id == null) {
            createdOn = OffsetDateTime.now(ZoneId.systemDefault());
        }
        modifiedOn = OffsetDateTime.now(ZoneId.systemDefault());
    }

}
