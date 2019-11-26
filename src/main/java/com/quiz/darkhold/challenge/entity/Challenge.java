package com.quiz.darkhold.challenge.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Set;

@Entity
@Table(name = "challenge")
public class Challenge implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(unique = true)
    private String title;

    @NotNull
    @Size(max = 250)
    @Column
    private String description;


    @Column(name = "createdOn")
    private OffsetDateTime createdOn;

    @Column(name = "modifiedOn")
    private OffsetDateTime modifiedOn;

    @OneToMany(mappedBy = "challenge")
    private Set<QuestionSet> questionSets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public OffsetDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(OffsetDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Set<QuestionSet> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(Set<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }

    /**
     * initialize / update date fields
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
