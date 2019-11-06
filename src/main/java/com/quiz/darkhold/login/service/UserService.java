package com.quiz.darkhold.login.service;

import com.quiz.darkhold.login.entity.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}

