package com.quiz.darkhold.home.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.StringJoiner;

public class GameInfo {

    @NotBlank(message = "Game PIN is required")
    @Pattern(regexp = "^[0-9]+$", message = "Game PIN must contain only digits")
    @Size(min = 4, max = 10, message = "Game PIN must be between 4 and 10 digits")
    private String gamePin;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    private String message;
    private List<String> users;
    private String moderator;

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

    public String getModerator() {
        return moderator;
    }

    public void setModerator(final String moderator) {
        this.moderator = moderator;
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
