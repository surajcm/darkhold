package com.quiz.darkhold.game.service;

import com.quiz.darkhold.game.model.GameMode;
import com.quiz.darkhold.game.model.GameStatus;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import com.quiz.darkhold.team.service.TeamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Tests")
class GameServiceTest {

    @Mock
    private CurrentGame currentGame;

    @Mock
    private PreviewService previewService;

    @Mock
    private TeamService teamService;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(currentGame, previewService, teamService);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    private void setSessionPin(final String pin) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true).setAttribute("gamePin", pin);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Nested
    @DisplayName("saveAndGetAllParticipants tests")
    class SaveAndGetAllParticipantsTests {

        @Test
        @DisplayName("Should save user and return participants")
        void shouldSaveUserAndReturnParticipants() {
            List<String> expected = List.of("host", "player1");
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(expected);

            List<String> result = gameService.saveAndGetAllParticipants("12345", "player1");

            verify(currentGame).saveUserToActiveGame("12345", "player1");
            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("getAllParticipants tests")
    class GetAllParticipantsTests {

        @Test
        @DisplayName("Should delegate to currentGame with explicit pin")
        void shouldDelegateToCurrentGame() {
            List<String> expected = List.of("host", "player1");
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(expected);

            List<String> result = gameService.getAllParticipants("12345");

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("getCurrentQuestionPointer(pin) tests")
    class GetCurrentQuestionPointerWithPinTests {

        @Test
        @DisplayName("Should delegate to currentGame with explicit pin")
        void shouldDelegateToCurrentGame() {
            QuestionPointer pointer = new QuestionPointer();
            pointer.setCurrentQuestionNumber(3);
            when(currentGame.getCurrentQuestionPointer("12345")).thenReturn(pointer);

            QuestionPointer result = gameService.getCurrentQuestionPointer("12345");

            assertEquals(3, result.getCurrentQuestionNumber());
        }
    }

    @Nested
    @DisplayName("calculateScoreWithStreak tests")
    class CalculateScoreWithStreakTests {

        @Test
        @DisplayName("Should return correct score with no streak")
        void shouldReturnCorrectScoreWithNoStreak() {
            // streak=0 -> multiplier=1, score = (1000 * 800 * 1) / 1000 = 800
            int score = gameService.calculateScoreWithStreak(1000, 800, 0);

            assertEquals(800, score);
        }

        @Test
        @DisplayName("Should apply 2x multiplier for streak of 2")
        void shouldApplyTwoXMultiplierForStreakOfTwo() {
            // streak=2 -> multiplier=2, score = (1000 * 800 * 2) / 1000 = 1600
            int score = gameService.calculateScoreWithStreak(1000, 800, 2);

            assertEquals(1600, score);
        }

        @Test
        @DisplayName("Should apply 3x multiplier for streak of 4")
        void shouldApplyThreeXMultiplierForStreakOfFour() {
            // streak=4 -> multiplier=3, score = (1000 * 500 * 3) / 1000 = 1500
            int score = gameService.calculateScoreWithStreak(1000, 500, 4);

            assertEquals(1500, score);
        }

        @Test
        @DisplayName("Should apply 4x multiplier for streak of 6+")
        void shouldApplyFourXMultiplierForHighStreak() {
            // streak=6 -> multiplier=4, score = (1000 * 500 * 4) / 1000 = 2000
            int score = gameService.calculateScoreWithStreak(1000, 500, 6);

            assertEquals(2000, score);
        }
    }

    @Nested
    @DisplayName("Session-dependent methods tests")
    class SessionDependentMethodsTests {

        @Test
        @DisplayName("getCurrentScore should use session pin")
        void getCurrentScoreShouldUseSessionPin() {
            setSessionPin("12345");
            Map<String, Integer> expected = Map.of("player1", 500);
            when(currentGame.getCurrentScore("12345")).thenReturn(expected);

            Map<String, Integer> result = gameService.getCurrentScore();

            assertEquals(expected, result);
        }

        @Test
        @DisplayName("findModerator should use session pin")
        void findModeratorShouldUseSessionPin() {
            setSessionPin("12345");
            when(currentGame.findModerator("12345")).thenReturn("host");

            String result = gameService.findModerator();

            assertEquals("host", result);
        }

        @Test
        @DisplayName("setGameStatus should use session pin")
        void setGameStatusShouldUseSessionPin() {
            setSessionPin("12345");

            gameService.setGameStatus(GameStatus.ACTIVE);

            verify(currentGame).setGameStatus("12345", GameStatus.ACTIVE);
        }

        @Test
        @DisplayName("getGameStatus should use session pin")
        void getGameStatusShouldUseSessionPin() {
            setSessionPin("12345");
            when(currentGame.getGameStatus("12345")).thenReturn(GameStatus.ACTIVE);

            GameStatus status = gameService.getGameStatus();

            assertEquals(GameStatus.ACTIVE, status);
        }

        @Test
        @DisplayName("pauseGame should use session pin")
        void pauseGameShouldUseSessionPin() {
            setSessionPin("12345");
            when(currentGame.pauseGame("12345")).thenReturn(true);

            boolean result = gameService.pauseGame();

            assertTrue(result);
        }

        @Test
        @DisplayName("resumeGame should use session pin")
        void resumeGameShouldUseSessionPin() {
            setSessionPin("12345");
            when(currentGame.resumeGame("12345")).thenReturn(5000L);

            long elapsed = gameService.resumeGame();

            assertEquals(5000L, elapsed);
        }

        @Test
        @DisplayName("kickPlayer should use session pin")
        void kickPlayerShouldUseSessionPin() {
            setSessionPin("12345");
            when(currentGame.removeUserFromGame("12345", "badPlayer")).thenReturn(true);

            boolean result = gameService.kickPlayer("badPlayer");

            assertTrue(result);
        }

        @Test
        @DisplayName("getParticipantCount should use session pin")
        void getParticipantCountShouldUseSessionPin() {
            setSessionPin("12345");
            when(currentGame.getParticipantCount("12345")).thenReturn(5);

            int count = gameService.getParticipantCount();

            assertEquals(5, count);
        }

        @Test
        @DisplayName("getGameMode should use session pin")
        void getGameModeShouldUseSessionPin() {
            setSessionPin("12345");
            when(currentGame.getGameMode("12345")).thenReturn(GameMode.PRACTICE);

            GameMode mode = gameService.getGameMode();

            assertEquals(GameMode.PRACTICE, mode);
        }
    }

    @Nested
    @DisplayName("isTeamMode tests")
    class IsTeamModeTests {

        @Test
        @DisplayName("Should delegate to teamService with explicit pin")
        void shouldDelegateToTeamServiceWithExplicitPin() {
            when(teamService.isTeamMode("12345")).thenReturn(true);

            assertTrue(gameService.isTeamMode("12345"));
        }

        @Test
        @DisplayName("Should return false when not team mode")
        void shouldReturnFalseWhenNotTeamMode() {
            when(teamService.isTeamMode("12345")).thenReturn(false);

            assertFalse(gameService.isTeamMode("12345"));
        }
    }

    @Nested
    @DisplayName("getTeamScores tests")
    class GetTeamScoresTests {

        @Test
        @DisplayName("Should delegate to teamService with explicit pin")
        void shouldDelegateToTeamServiceWithExplicitPin() {
            Map<String, Integer> expected = Map.of("Team Red", 1000);
            when(teamService.calculateTeamScores("12345")).thenReturn(expected);

            Map<String, Integer> result = gameService.getTeamScores("12345");

            assertEquals(expected, result);
        }
    }
}
