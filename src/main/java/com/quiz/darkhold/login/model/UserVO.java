package com.quiz.darkhold.login.model;

import java.util.StringJoiner;

public class UserVO {
    private String userName;
    private String email;
    private String password;
    private ROLE role;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(final ROLE role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserVO.class.getSimpleName() + "[", "]")
                .add("userName='" + userName + "'")
                .add("email='" + email + "'")
                .add("password='" + password + "'")
                .add("role=" + role)
                .toString();
    }
}
