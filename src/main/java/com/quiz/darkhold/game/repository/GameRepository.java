package com.quiz.darkhold.game.repository;

import com.quiz.darkhold.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByPin(String pin);

    List<Game> findByGameStatusNot(String gameStatus);

    /**
     * Find all games for a specific moderator with a specific status.
     *
     * @param moderator the moderator username
     * @param gameStatus the game status
     * @return list of games
     */
    List<Game> findByModeratorAndGameStatus(String moderator, String gameStatus);

    /**
     * Find all games for a specific moderator that are not in a specific status.
     *
     * @param moderator the moderator username
     * @param gameStatus the game status to exclude
     * @return list of games
     */
    List<Game> findByModeratorAndGameStatusNot(String moderator, String gameStatus);

    /**
     * Find all active games (not finished) for a specific moderator, ordered by creation date.
     *
     * @param moderator the moderator username
     * @param gameStatus the game status to exclude (typically FINISHED)
     * @return list of games ordered by creation date descending
     */
    List<Game> findByModeratorAndGameStatusNotOrderByCreatedOnDesc(String moderator, String gameStatus);

    /**
     * Check if a PIN already exists.
     *
     * @param pin the PIN to check
     * @return true if exists, false otherwise
     */
    boolean existsByPin(String pin);
}
