package com.quiz.darkhold.home.service;

import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {
    @Autowired
    private GameRepository gameRepository;

    public Boolean validateGamePin(String pin) {
        Game game = gameRepository.findByPin(pin);
        if (game != null && (game.getGameStatus().equalsIgnoreCase(GameStatus.WAITING.name())
                || game.getGameStatus().equalsIgnoreCase(GameStatus.STARTED.name()))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
