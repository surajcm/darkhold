package com.quiz.darkhold.team.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TeamConfig Tests")
class TeamConfigTest {

    @Test
    @DisplayName("Default constructor should set BALANCED and empty map")
    void defaultConstructor() {
        var config = new TeamConfig();

        assertEquals(TeamAssignmentMethod.BALANCED, config.getAssignmentMethod());
        assertNotNull(config.getTeamNames());
        assertTrue(config.getTeamNames().isEmpty());
    }

    @Test
    @DisplayName("Full constructor should set count and method")
    void fullConstructor() {
        var config = new TeamConfig(4, TeamAssignmentMethod.RANDOM);

        assertEquals(4, config.getTeamCount());
        assertEquals(TeamAssignmentMethod.RANDOM, config.getAssignmentMethod());
        assertNotNull(config.getTeamNames());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void settersAndGetters() {
        var config = new TeamConfig();
        config.setTeamCount(3);
        config.setAssignmentMethod(TeamAssignmentMethod.MANUAL);
        config.setTeamNames(new HashMap<>(Map.of("red", "Wolves")));

        assertEquals(3, config.getTeamCount());
        assertEquals(TeamAssignmentMethod.MANUAL, config.getAssignmentMethod());
        assertEquals(1, config.getTeamNames().size());
        assertEquals("Wolves", config.getTeamNames().get("red"));
    }

    @Test
    @DisplayName("addTeamName should add to map")
    void addTeamName() {
        var config = new TeamConfig();
        config.addTeamName("blue", "Eagles");
        config.addTeamName("red", "Hawks");

        assertEquals(2, config.getTeamNames().size());
        assertEquals("Eagles", config.getTeamNames().get("blue"));
        assertEquals("Hawks", config.getTeamNames().get("red"));
    }

    @Test
    @DisplayName("addTeamName should overwrite existing color")
    void addTeamNameOverwrite() {
        var config = new TeamConfig();
        config.addTeamName("blue", "Eagles");
        config.addTeamName("blue", "Sharks");

        assertEquals(1, config.getTeamNames().size());
        assertEquals("Sharks", config.getTeamNames().get("blue"));
    }
}
