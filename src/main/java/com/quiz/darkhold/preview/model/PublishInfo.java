package com.quiz.darkhold.preview.model;

import java.util.List;

public class PublishInfo {
    private String moderator;
    private String pin;
    private List<String> users;

    public String getModerator() {
        return moderator;
    }

    public void setModerator(String moderator) {
        this.moderator = moderator;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
