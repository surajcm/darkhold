package com.quiz.darkhold.user.service;

import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;

import java.util.List;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    List<User> listAll();

    List<Role> listRoles();

    Boolean isEmailUnique(String email);
}

