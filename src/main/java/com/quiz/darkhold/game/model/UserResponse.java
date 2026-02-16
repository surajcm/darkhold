package com.quiz.darkhold.game.model;

import java.util.List;

public class UserResponse {
    private List<String> users;

    /**
     * Default constructor for object initialization.
     */
    public UserResponse() {
    }

    public List<String> getUsers() {
        return users;
    }

    public UserResponse(final List<String> users) {
        this.users = users;
    }
}
