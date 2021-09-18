package com.quiz.darkhold.home.service;

import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HomeServiceTest {
    private final GameRepository gameRepository = Mockito.mock(GameRepository.class);
    private final CurrentGame currentGame = Mockito.mock(CurrentGame.class);
    private final HomeService homeService = new HomeService(gameRepository, currentGame);

    @Test
    void validateGamePinSuccess() {
        when(gameRepository.findByPin(anyString())).thenReturn(mockGame());
        Assertions.assertTrue(homeService.validateGamePin("1234"));
    }

    @Test
    void validateGamePinFailure() {
        when(gameRepository.findByPin(anyString())).thenReturn(null);
        Assertions.assertFalse(homeService.validateGamePin("1234"));
    }

    @Test
    void verifyParticipantsInActiveQuiz() {
        Assertions.assertNotNull(homeService.participantsInActiveQuiz("1234"));
    }

    private Game mockGame() {
        var game = new Game();
        game.setGameStatus(GameStatus.STARTED.name());
        return game;
    }

}