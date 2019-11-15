package com.quiz.darkhold.home.model;

import java.util.StringJoiner;

public class GameInfo {
    String gamePin;
    String name;
    String message;

    public String getGamePin() {
        return gamePin;
    }

    public void setGamePin(String gamePin) {
        this.gamePin = gamePin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GameInfo.class.getSimpleName() + "[", "]")
                .add("gamePin='" + gamePin + "'")
                .add("name='" + name + "'")
                .add("message='" + message + "'")
                .toString();
    }
}
