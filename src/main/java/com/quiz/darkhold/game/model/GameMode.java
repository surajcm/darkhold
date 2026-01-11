package com.quiz.darkhold.game.model;

/**
 * Represents the mode of a quiz game.
 */
public enum GameMode {
    /**
     * Standard multiplayer mode where moderator controls the game
     * but does not earn points. Real-time synchronized gameplay.
     */
    MULTIPLAYER,

    /**
     * Single-player practice mode where the player both controls
     * the game and earns points. Shows correct answers immediately.
     * No pressure, instant feedback.
     */
    PRACTICE,

    /**
     * Self-paced mode with shareable link (no PIN required).
     * Players can start anytime within availability window.
     * Supports multiple attempts/retakes.
     */
    SELF_PACED,

    /**
     * Challenge mode with persistent leaderboard across all attempts.
     * Best scores tracked, time-based rankings, competitive gameplay.
     */
    CHALLENGE
}
