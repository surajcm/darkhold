package com.quiz.darkhold.game.service;

import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.entity.GameStatus;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameCleanupScheduler Tests")
class GameCleanupSchedulerTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CurrentGame currentGame;

    private GameCleanupScheduler scheduler;

    @BeforeEach
    void setUp() throws Exception {
        scheduler = new GameCleanupScheduler(gameRepository, currentGame);
        setField(scheduler, "expirationHours", 24);
        setField(scheduler, "waitingExpirationHours", 2);
    }

    private void setField(final Object target, final String fieldName, final Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Nested
    @DisplayName("cleanupExpiredGames tests")
    class CleanupExpiredGamesTests {

        @Test
        @DisplayName("Should clean up expired WAITING games")
        void shouldCleanUpExpiredWaitingGames() {
            Game waitingGame = createGame("11111", GameStatus.WAITING.name(),
                    LocalDateTime.now().minusHours(3));
            when(gameRepository.findByGameStatusNot(GameStatus.FINISHED.name()))
                    .thenReturn(List.of(waitingGame));

            scheduler.cleanupExpiredGames();

            verify(gameRepository).save(waitingGame);
            verify(currentGame).stopTheGame("11111");
        }

        @Test
        @DisplayName("Should clean up expired ACTIVE games")
        void shouldCleanUpExpiredActiveGames() {
            Game activeGame = createGame("22222", GameStatus.STARTED.name(),
                    LocalDateTime.now().minusHours(25));
            when(gameRepository.findByGameStatusNot(GameStatus.FINISHED.name()))
                    .thenReturn(List.of(activeGame));

            scheduler.cleanupExpiredGames();

            verify(gameRepository).save(activeGame);
            verify(currentGame).stopTheGame("22222");
        }

        @Test
        @DisplayName("Should preserve non-expired games")
        void shouldPreserveNonExpiredGames() {
            Game recentGame = createGame("33333", GameStatus.STARTED.name(),
                    LocalDateTime.now().minusMinutes(30));
            when(gameRepository.findByGameStatusNot(GameStatus.FINISHED.name()))
                    .thenReturn(List.of(recentGame));

            scheduler.cleanupExpiredGames();

            verify(gameRepository, never()).save(any());
            verify(currentGame, never()).stopTheGame(any());
        }

        @Test
        @DisplayName("Should handle empty game list")
        void shouldHandleEmptyGameList() {
            when(gameRepository.findByGameStatusNot(GameStatus.FINISHED.name()))
                    .thenReturn(new ArrayList<>());

            scheduler.cleanupExpiredGames();

            verify(gameRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should skip games with null createdOn")
        void shouldSkipGamesWithNullCreatedOn() {
            Game nullDateGame = createGame("44444", GameStatus.STARTED.name(), null);
            when(gameRepository.findByGameStatusNot(GameStatus.FINISHED.name()))
                    .thenReturn(List.of(nullDateGame));

            scheduler.cleanupExpiredGames();

            verify(gameRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cleanupGame tests")
    class CleanupGameTests {

        @Test
        @DisplayName("Should return false when game not found")
        void shouldReturnFalseWhenGameNotFound() {
            when(gameRepository.findByPin("99999")).thenReturn(null);

            boolean result = scheduler.cleanupGame("99999");

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when already FINISHED")
        void shouldReturnFalseWhenAlreadyFinished() {
            Game finishedGame = createGame("11111", GameStatus.FINISHED.name(),
                    LocalDateTime.now().minusHours(1));
            when(gameRepository.findByPin("11111")).thenReturn(finishedGame);

            boolean result = scheduler.cleanupGame("11111");

            assertFalse(result);
            verify(gameRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should clean up and return true for active game")
        void shouldCleanUpAndReturnTrue() {
            Game activeGame = createGame("22222", GameStatus.STARTED.name(),
                    LocalDateTime.now().minusHours(1));
            when(gameRepository.findByPin("22222")).thenReturn(activeGame);

            boolean result = scheduler.cleanupGame("22222");

            assertTrue(result);
            verify(gameRepository).save(activeGame);
            verify(currentGame).stopTheGame("22222");
        }

        @Test
        @DisplayName("Should set status to FINISHED on cleanup")
        void shouldSetStatusToFinished() {
            Game activeGame = createGame("22222", GameStatus.STARTED.name(),
                    LocalDateTime.now().minusHours(1));
            when(gameRepository.findByPin("22222")).thenReturn(activeGame);

            scheduler.cleanupGame("22222");

            assertTrue(activeGame.getGameStatus().equals(GameStatus.FINISHED.name()));
        }

        @Test
        @DisplayName("Should call currentGame stopTheGame on cleanup")
        void shouldCallStopTheGame() {
            Game activeGame = createGame("33333", GameStatus.WAITING.name(),
                    LocalDateTime.now());
            when(gameRepository.findByPin("33333")).thenReturn(activeGame);

            scheduler.cleanupGame("33333");

            verify(currentGame).stopTheGame("33333");
        }
    }

    // Helper methods

    private Game createGame(final String pin, final String status, final LocalDateTime createdOn) {
        Game game = new Game();
        game.setPin(pin);
        game.setGameStatus(status);
        game.setCreatedOn(createdOn);
        return game;
    }
}
