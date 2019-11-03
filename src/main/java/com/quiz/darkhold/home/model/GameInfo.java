package com.quiz.darkhold.home.model;

import java.util.StringJoiner;

public class GameInfo {
    String gamePin;
    String name;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", GameInfo.class.getSimpleName() + "[", "]")
                .add("gamePin='" + gamePin + "'")
                .add("name='" + name + "'")
                .toString();
    }
}
