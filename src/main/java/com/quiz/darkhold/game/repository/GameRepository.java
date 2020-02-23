package com.quiz.darkhold.game.repository;

import com.quiz.darkhold.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByPin(String pin);

    List<Game> findByGameStatusNot(String gameStatus);
}
