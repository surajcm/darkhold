package com.quiz.darkhold.preview.model;

import java.util.List;

public class PublishInfo {
    private String moderator;
    private String pin;
    private List<String> users;

    public String getModerator() {
        return moderator;
    }

    public void setModerator(final String moderator) {
        this.moderator = moderator;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(final String pin) {
        this.pin = pin;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(final List<String> users) {
        this.users = users;
    }
}
