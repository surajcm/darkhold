package com.quiz.darkhold.user.service;

public interface SecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password);

    boolean isAuthenticated();
}
