package com.quiz.darkhold.challenge.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.StringJoiner;

@Entity
@Table(name = "question_set")
public class QuestionSet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column
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

    @ManyToOne
    @JoinColumn(name="challenge_id", nullable=false)
    private Challenge challenge;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(String correctOptions) {
        this.correctOptions = correctOptions;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
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
