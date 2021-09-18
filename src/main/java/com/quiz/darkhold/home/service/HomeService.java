package com.quiz.darkhold.home.service;

import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {
    private final GameRepository gameRepository;

    private final CurrentGame currentGame;

    public HomeService(final GameRepository gameRepository, final CurrentGame currentGame) {
        this.gameRepository = gameRepository;
        this.currentGame = currentGame;
    }

    /**
     * Verifies whether the entered pin is valid or not.
     *
     * @param pin pin
     * @return true or false
     */
    public Boolean validateGamePin(final String pin) {
        var game = gameRepository.findByPin(pin);
        if (game != null && (game.getGameStatus().equalsIgnoreCase(GameStatus.WAITING.name())
                || game.getGameStatus().equalsIgnoreCase(GameStatus.STARTED.name()))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<String> participantsInActiveQuiz(final String pin) {
        return currentGame.getActiveUsersInGame(pin);
    }

}
