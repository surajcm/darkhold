package com.quiz.darkhold.game.repository;

import com.quiz.darkhold.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository  extends JpaRepository<Game, Long> {

}
