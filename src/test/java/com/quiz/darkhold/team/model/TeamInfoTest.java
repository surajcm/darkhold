package com.quiz.darkhold.team.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TeamInfo Tests")
class TeamInfoTest {

    @Test
    @DisplayName("Default constructor should initialize empty members and zero score")
    void defaultConstructor() {
        var info = new TeamInfo();

        assertNotNull(info.getMembers());
        assertTrue(info.getMembers().isEmpty());
        assertEquals(0, info.getScore());
        assertEquals(0, info.getMemberCount());
    }

    @Test
    @DisplayName("Full constructor should set name and color")
    void fullConstructor() {
        var info = new TeamInfo("Wolves", "#333");

        assertEquals("Wolves", info.getName());
        assertEquals("#333", info.getColor());
        assertNotNull(info.getMembers());
        assertEquals(0, info.getScore());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void settersAndGetters() {
        var info = new TeamInfo();
        info.setName("Hawks");
        info.setColor("blue");
        info.setScore(250);
        info.setMembers(new ArrayList<>(List.of("Alice", "Bob")));

        assertEquals("Hawks", info.getName());
        assertEquals("blue", info.getColor());
        assertEquals(250, info.getScore());
        assertEquals(2, info.getMembers().size());
    }

    @Test
    @DisplayName("addMember should add unique member")
    void addMember() {
        var info = new TeamInfo("A", "red");
        info.addMember("Alice");
        info.addMember("Bob");

        assertEquals(2, info.getMemberCount());
        assertTrue(info.getMembers().contains("Alice"));
        assertTrue(info.getMembers().contains("Bob"));
    }

    @Test
    @DisplayName("addMember should prevent duplicates")
    void addMemberDuplicate() {
        var info = new TeamInfo("A", "red");
        info.addMember("Alice");
        info.addMember("Alice");

        assertEquals(1, info.getMemberCount());
    }

    @Test
    @DisplayName("removeMember should remove existing member")
    void removeMember() {
        var info = new TeamInfo("A", "red");
        info.addMember("Alice");
        info.addMember("Bob");
        info.removeMember("Alice");

        assertEquals(1, info.getMemberCount());
        assertTrue(info.getMembers().contains("Bob"));
    }

    @Test
    @DisplayName("removeMember should handle non-existent member gracefully")
    void removeMemberNotFound() {
        var info = new TeamInfo("A", "red");
        info.addMember("Alice");
        info.removeMember("Charlie");

        assertEquals(1, info.getMemberCount());
    }

    @Test
    @DisplayName("getMemberCount should reflect current members list")
    void getMemberCount() {
        var info = new TeamInfo();
        assertEquals(0, info.getMemberCount());

        info.addMember("User1");
        info.addMember("User2");
        info.addMember("User3");
        assertEquals(3, info.getMemberCount());
    }
}
