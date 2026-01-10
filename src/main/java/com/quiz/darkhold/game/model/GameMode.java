package com.quiz.darkhold.game.model;

/**
 * Represents the mode of a quiz game.
 */
public enum GameMode {
    /**
     * Standard multiplayer mode where moderator controls the game
     * but does not earn points.
     */
    MULTIPLAYER,

    /**
     * Single-player practice mode where the player both controls
     * the game and earns points.
     */
    PRACTICE
}
