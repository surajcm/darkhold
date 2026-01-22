package com.quiz.darkhold.team.model;

import java.util.ArrayList;
import java.util.List;

public class TeamInfo {
    private String name;
    private String color;
    private List<String> members;
    private Integer score;

    public TeamInfo() {
        this.members = new ArrayList<>();
        this.score = 0;
    }

    public TeamInfo(final String name, final String color) {
        this.name = name;
        this.color = color;
        this.members = new ArrayList<>();
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(final List<String> members) {
        this.members = members;
    }

    public void addMember(final String username) {
        if (!this.members.contains(username)) {
            this.members.add(username);
        }
    }

    public void removeMember(final String username) {
        this.members.remove(username);
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(final Integer score) {
        this.score = score;
    }

    public int getMemberCount() {
        return members.size();
    }
}
