package com.quiz.darkhold.user.service;

import com.quiz.darkhold.user.entity.Role;
import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    User save(User user);

    User findByUsername(String username);

    List<User> listAll();

    List<Role> listRoles();

    Boolean isEmailUnique(Long id, String email);

    User get(Long id) throws UserNotFoundException;

    void delete(final Long id) throws UserNotFoundException;

    void updateUserEnabledStatus(final Long id, final boolean enabled);
}

