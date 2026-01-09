package com.quiz.darkhold.game.model;

/**
 * Enum representing the status of an active game session.
 */
public enum GameStatus {
    /**
     * Game is in the lobby, waiting for players to join.
     */
    WAITING,

    /**
     * Game is actively in progress.
     */
    ACTIVE,

    /**
     * Game is temporarily paused by the moderator.
     */
    PAUSED,

    /**
     * Game has ended (either completed or ended early).
     */
    ENDED
}
