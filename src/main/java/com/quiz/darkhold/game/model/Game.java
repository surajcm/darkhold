package com.quiz.darkhold.game.model;

import java.util.StringJoiner;

public class Game {

    private String name;
    private String pin;

    public Game() {
    }

    public Game(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(final String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Game.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("pin='" + pin + "'")
                .toString();
    }
}
