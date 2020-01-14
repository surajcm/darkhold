package com.quiz.darkhold.game.model;

import java.util.List;

public class UserResponse {
    List<String> users;

    public UserResponse() {
    }

    public List<String> getUsers() {
        return users;
    }

    public UserResponse(List<String> users) {
        this.users = users;
    }
}
