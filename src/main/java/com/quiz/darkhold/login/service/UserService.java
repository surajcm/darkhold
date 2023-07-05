package com.quiz.darkhold.login.service;

import com.quiz.darkhold.login.entity.User;

import java.util.List;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    List<User> listAll();
}

