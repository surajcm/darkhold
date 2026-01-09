package com.quiz.darkhold.challenge.entity;

import com.quiz.darkhold.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "challenge")
public class Challenge implements Serializable {

    public static final long serialVersionUID = 4328743;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String title;

    @Column(length = 250, nullable = false)
    private String description;

    @Column(name = "challengeowner")
    private Long challengeOwner;

    @CreatedDate
    @Column(name = "createdOn")
    private LocalDateTime createdOn;

    @LastModifiedDate
    @Column(name = "modifiedOn")
    private LocalDateTime modifiedOn;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("displayOrder, id")
    private List<QuestionSet> questionSets;

    @ManyToOne
    @JoinColumn(name = "challengeOwner", referencedColumnName = "id", insertable = false, updatable = false)
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Long getChallengeOwner() {
        return challengeOwner;
    }

    public void setChallengeOwner(final Long challengeOwner) {
        this.challengeOwner = challengeOwner;
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

    public List<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(final List<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }
}
