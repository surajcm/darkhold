package com.quiz.darkhold.home.model;

import java.util.List;
import java.util.StringJoiner;

public class GameInfo {
    private String gamePin;
    private String name;
    private String message;
    private List<String> users;

    public String getGamePin() {
        return gamePin;
    }

    public void setGamePin(final String gamePin) {
        this.gamePin = gamePin;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(final List<String> users) {
        this.users = users;
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
