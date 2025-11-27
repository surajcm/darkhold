package com.quiz.darkhold.challenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.StringJoiner;

@Entity
@Table(name = "question_set")
public class QuestionSet implements Serializable {

    public static final long serialVersionUID = 4328743;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column
    private String answer1;

    @Column
    private String answer2;

    @Column
    private String answer3;

    @Column
    private String answer4;

    @Column
    private String correctOptions;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = true)
    private Challenge challenge;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(final String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(final String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(final String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(final String answer4) {
        this.answer4 = answer4;
    }

    public String getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(final String correctOptions) {
        this.correctOptions = correctOptions;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(final Challenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QuestionSet.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("question='" + question + "'")
                .add("answer1='" + answer1 + "'")
                .add("answer2='" + answer2 + "'")
                .add("answer3='" + answer3 + "'")
                .add("answer4='" + answer4 + "'")
                .add("correctOptions='" + correctOptions + "'")
                .toString();
    }
}
