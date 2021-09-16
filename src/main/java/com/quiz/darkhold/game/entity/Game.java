package com.quiz.darkhold.game.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "createdOn")
    private LocalDateTime createdOn;

    @LastModifiedDate
    @Column(name = "modifiedOn")
    private LocalDateTime modifiedOn;

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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(final LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

}
