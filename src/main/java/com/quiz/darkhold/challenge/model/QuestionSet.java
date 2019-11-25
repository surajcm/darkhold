package com.quiz.darkhold.challenge.model;

import java.util.List;
import java.util.StringJoiner;

public class QuestionSet {
    String question;
    String answer1;
    String answer2;
    String answer3;
    String answer4;
    List<Options> correctOption;

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

    public List<Options> getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(List<Options> correctOption) {
        this.correctOption = correctOption;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QuestionSet.class.getSimpleName() + "[", "]")
                .add("question='" + question + "'")
                .add("answer1='" + answer1 + "'")
                .add("answer2='" + answer2 + "'")
                .add("answer3='" + answer3 + "'")
                .add("answer4='" + answer4 + "'")
                .add("correctOption=" + correctOption)
                .toString();
    }
}
