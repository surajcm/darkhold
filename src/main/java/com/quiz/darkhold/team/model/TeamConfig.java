package com.quiz.darkhold.team.model;

import java.util.HashMap;
import java.util.Map;

public class TeamConfig {
    private Integer teamCount;
    private TeamAssignmentMethod assignmentMethod;
    private Map<String, String> teamNames;

    public TeamConfig() {
        this.teamNames = new HashMap<>();
        this.assignmentMethod = TeamAssignmentMethod.BALANCED;
    }

    public TeamConfig(final Integer teamCount, final TeamAssignmentMethod assignmentMethod) {
        this.teamCount = teamCount;
        this.assignmentMethod = assignmentMethod;
        this.teamNames = new HashMap<>();
    }

    public Integer getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(final Integer teamCount) {
        this.teamCount = teamCount;
    }

    public TeamAssignmentMethod getAssignmentMethod() {
        return assignmentMethod;
    }

    public void setAssignmentMethod(final TeamAssignmentMethod assignmentMethod) {
        this.assignmentMethod = assignmentMethod;
    }

    public Map<String, String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(final Map<String, String> teamNames) {
        this.teamNames = teamNames;
    }

    public void addTeamName(final String color, final String name) {
        this.teamNames.put(color, name);
    }
}
