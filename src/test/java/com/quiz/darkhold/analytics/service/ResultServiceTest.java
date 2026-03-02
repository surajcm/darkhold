package com.quiz.darkhold.analytics.service;

import com.quiz.darkhold.analytics.entity.GameResult;
import com.quiz.darkhold.analytics.entity.ParticipantResult;
import com.quiz.darkhold.analytics.entity.QuestionResult;
import com.quiz.darkhold.analytics.repository.GameResultRepository;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.team.model.TeamInfo;
import com.quiz.darkhold.team.repository.TeamResultRepository;
import com.quiz.darkhold.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResultService Tests")
class ResultServiceTest {

    @Mock
    private GameResultRepository gameResultRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CurrentGame currentGame;

    @Mock
    private TeamService teamService;

    @Mock
    private TeamResultRepository teamResultRepository;

    private ResultService resultService;

    @BeforeEach
    void setUp() {
        resultService = new ResultService(
                gameResultRepository, gameRepository, currentGame, teamService, teamResultRepository);
    }

    @Nested
    @DisplayName("saveGameResult tests")
    class SaveGameResultTests {

        @Test
        @DisplayName("Should return null when game not found")
        void shouldReturnNullWhenGameNotFound() {
            when(gameRepository.findByPin("99999")).thenReturn(null);

            GameResult result = resultService.saveGameResult("99999", "Quiz");

            assertNull(result);
            verify(gameResultRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create GameResult with correct PIN and challenge name")
        void shouldCreateGameResultWithCorrectFields() {
            Game game = createTestGame("12345", "moderator", false);
            setupBasicMocks("12345", game);

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "My Quiz");

            GameResult saved = captor.getValue();
            assertEquals("12345", saved.getPin());
            assertEquals("My Quiz", saved.getChallengeName());
        }

        @Test
        @DisplayName("Should set moderator from game")
        void shouldSetModeratorFromGame() {
            Game game = createTestGame("12345", "host", false);
            setupBasicMocks("12345", game);

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            assertEquals("host", captor.getValue().getModerator());
        }

        @Test
        @DisplayName("Should set moderator to Unknown when null")
        void shouldSetModeratorToUnknownWhenNull() {
            Game game = createTestGame("12345", null, false);
            setupBasicMocks("12345", game);

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            assertEquals("Unknown", captor.getValue().getModerator());
        }

        @Test
        @DisplayName("Should set participant count excluding moderator")
        void shouldSetParticipantCountExcludingModerator() {
            Game game = createTestGame("12345", "host", false);
            List<String> users = List.of("host", "player1", "player2");
            when(gameRepository.findByPin("12345")).thenReturn(game);
            when(currentGame.getQuestionsOnAPin("12345")).thenReturn(new ArrayList<>());
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(users);
            when(currentGame.getCurrentScore("12345")).thenReturn(new HashMap<>());
            lenient().when(currentGame.findModerator("12345")).thenReturn("host");

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            assertEquals(2, captor.getValue().getParticipantCount());
        }

        @Test
        @DisplayName("Should set winner as highest scorer")
        void shouldSetWinnerAsHighestScorer() {
            Game game = createTestGame("12345", "host", false);
            setupGameWithScores("12345", game);

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            GameResult saved = captor.getValue();
            assertEquals("player1", saved.getWinnerUsername());
            assertEquals(500, saved.getWinnerScore());
        }

        @Test
        @DisplayName("Should add participant results sorted by score descending")
        void shouldAddParticipantResultsSortedByScore() {
            Game game = createTestGame("12345", "host", false);
            setupGameWithScores("12345", game);

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            List<ParticipantResult> results = captor.getValue().getParticipantResults();
            assertEquals(2, results.size());
            assertEquals("player1", results.get(0).getUsername());
            assertEquals("player2", results.get(1).getUsername());
            assertEquals(1, results.get(0).getFinalRank());
            assertEquals(2, results.get(1).getFinalRank());
        }

        @Test
        @DisplayName("Should skip moderator from participant results")
        void shouldSkipModeratorFromParticipantResults() {
            Game game = createTestGame("12345", "host", false);
            setupGameWithScores("12345", game);

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            List<ParticipantResult> results = captor.getValue().getParticipantResults();
            assertTrue(results.stream().noneMatch(pr -> pr.getUsername().equals("host")));
        }

        @Test
        @DisplayName("Should handle null scores gracefully")
        void shouldHandleNullScoresGracefully() {
            Game game = createTestGame("12345", "host", false);
            when(gameRepository.findByPin("12345")).thenReturn(game);
            when(currentGame.getQuestionsOnAPin("12345")).thenReturn(new ArrayList<>());
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(List.of("host"));
            when(currentGame.getCurrentScore("12345")).thenReturn(null);
            lenient().when(currentGame.findModerator("12345")).thenReturn("host");

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            assertTrue(captor.getValue().getParticipantResults().isEmpty());
        }

        @Test
        @DisplayName("Should handle empty scores gracefully")
        void shouldHandleEmptyScoresGracefully() {
            Game game = createTestGame("12345", "host", false);
            when(gameRepository.findByPin("12345")).thenReturn(game);
            when(currentGame.getQuestionsOnAPin("12345")).thenReturn(new ArrayList<>());
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(List.of("host"));
            when(currentGame.getCurrentScore("12345")).thenReturn(new HashMap<>());
            lenient().when(currentGame.findModerator("12345")).thenReturn("host");

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            assertTrue(captor.getValue().getParticipantResults().isEmpty());
        }

        @Test
        @DisplayName("Should set question results with correct numbering")
        void shouldSetQuestionResultsWithCorrectNumbering() {
            Game game = createTestGame("12345", "host", false);
            List<QuestionSet> questions = createTestQuestions(3);
            when(gameRepository.findByPin("12345")).thenReturn(game);
            when(currentGame.getQuestionsOnAPin("12345")).thenReturn(questions);
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(List.of("host"));
            when(currentGame.getCurrentScore("12345")).thenReturn(new HashMap<>());
            lenient().when(currentGame.findModerator("12345")).thenReturn("host");

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            List<QuestionResult> questionResults = captor.getValue().getQuestionResults();
            assertEquals(3, questionResults.size());
            assertEquals(1, questionResults.get(0).getQuestionNumber());
            assertEquals(2, questionResults.get(1).getQuestionNumber());
            assertEquals(3, questionResults.get(2).getQuestionNumber());
        }

        @Test
        @DisplayName("Should handle null questions gracefully")
        void shouldHandleNullQuestionsGracefully() {
            Game game = createTestGame("12345", "host", false);
            when(gameRepository.findByPin("12345")).thenReturn(game);
            when(currentGame.getQuestionsOnAPin("12345")).thenReturn(new ArrayList<>());
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(List.of("host"));
            when(currentGame.getCurrentScore("12345")).thenReturn(new HashMap<>());
            lenient().when(currentGame.findModerator("12345")).thenReturn("host");

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            assertTrue(captor.getValue().getQuestionResults().isEmpty());
        }

        @Test
        @DisplayName("Should set team info when teamMode is true")
        void shouldSetTeamInfoWhenTeamModeIsTrue() {
            Game game = createTestGame("12345", "host", true);
            Map<String, Integer> scores = new LinkedHashMap<>();
            scores.put("host", 0);
            scores.put("player1", 500);
            when(gameRepository.findByPin("12345")).thenReturn(game);
            when(currentGame.getQuestionsOnAPin("12345")).thenReturn(new ArrayList<>());
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(List.of("host", "player1"));
            when(currentGame.getCurrentScore("12345")).thenReturn(scores);
            when(currentGame.findModerator("12345")).thenReturn("host");
            when(currentGame.getStreak("12345", "player1")).thenReturn(2);

            TeamInfo redTeam = new TeamInfo("Team Red", "red");
            when(teamService.getTeams("12345")).thenReturn(List.of(redTeam));
            when(teamService.getPlayerTeam("12345", "player1")).thenReturn("Team Red");
            when(teamService.calculateTeamScores("12345")).thenReturn(Map.of("Team Red", 500));

            ArgumentCaptor<GameResult> captor = ArgumentCaptor.forClass(GameResult.class);
            when(gameResultRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            List<ParticipantResult> results = captor.getValue().getParticipantResults();
            assertEquals(1, results.size());
            assertEquals("Team Red", results.get(0).getTeamName());
            assertEquals("red", results.get(0).getTeamColor());
        }

        @Test
        @DisplayName("Should call addTeamResults for team mode games")
        void shouldCallAddTeamResultsForTeamModeGames() {
            Game game = createTestGame("12345", "host", true);
            when(gameRepository.findByPin("12345")).thenReturn(game);
            when(currentGame.getQuestionsOnAPin("12345")).thenReturn(new ArrayList<>());
            when(currentGame.getActiveUsersInGame("12345")).thenReturn(List.of("host"));
            when(currentGame.getCurrentScore("12345")).thenReturn(new HashMap<>());
            lenient().when(currentGame.findModerator("12345")).thenReturn("host");

            TeamInfo redTeam = new TeamInfo("Team Red", "red");
            redTeam.addMember("player1");
            when(teamService.getTeams("12345")).thenReturn(List.of(redTeam));
            when(teamService.calculateTeamScores("12345")).thenReturn(Map.of("Team Red", 500));
            when(gameResultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            resultService.saveGameResult("12345", "Quiz");

            verify(teamResultRepository).save(any());
        }
    }

    @Nested
    @DisplayName("getGameResultsByModerator tests")
    class GetGameResultsByModeratorTests {

        @Test
        @DisplayName("Should delegate to repository")
        void shouldDelegateToRepository() {
            List<GameResult> expected = List.of(new GameResult());
            when(gameResultRepository.findByModeratorOrderByCompletedAtDesc("host")).thenReturn(expected);

            List<GameResult> result = resultService.getGameResultsByModerator("host");

            assertEquals(expected, result);
            verify(gameResultRepository).findByModeratorOrderByCompletedAtDesc("host");
        }
    }

    @Nested
    @DisplayName("getGameResultById tests")
    class GetGameResultByIdTests {

        @Test
        @DisplayName("Should return result when found")
        void shouldReturnResultWhenFound() {
            GameResult expected = new GameResult();
            expected.setId(1L);
            when(gameResultRepository.findById(1L)).thenReturn(Optional.of(expected));

            GameResult result = resultService.getGameResultById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should return null when not found")
        void shouldReturnNullWhenNotFound() {
            when(gameResultRepository.findById(999L)).thenReturn(Optional.empty());

            GameResult result = resultService.getGameResultById(999L);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getGameResultsByChallenge tests")
    class GetGameResultsByChallengeTests {

        @Test
        @DisplayName("Should delegate to repository")
        void shouldDelegateToRepository() {
            List<GameResult> expected = List.of(new GameResult());
            when(gameResultRepository.findByChallengeIdOrderByCompletedAtDesc("ch1")).thenReturn(expected);

            List<GameResult> result = resultService.getGameResultsByChallenge("ch1");

            assertEquals(expected, result);
        }
    }

    // Helper methods

    private Game createTestGame(final String pin, final String moderator, final boolean teamMode) {
        Game game = new Game();
        game.setPin(pin);
        game.setModerator(moderator);
        game.setTeamMode(teamMode);
        game.setChallengeId("challenge1");
        game.setGameMode("MULTIPLAYER");
        game.setCreatedOn(LocalDateTime.now().minusMinutes(30));
        return game;
    }

    private void setupBasicMocks(final String pin, final Game game) {
        when(gameRepository.findByPin(pin)).thenReturn(game);
        when(currentGame.getQuestionsOnAPin(pin)).thenReturn(new ArrayList<>());
        when(currentGame.getActiveUsersInGame(pin)).thenReturn(List.of("host"));
        when(currentGame.getCurrentScore(pin)).thenReturn(new HashMap<>());
        lenient().when(currentGame.findModerator(pin)).thenReturn(game.getModerator());
    }

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    private void setupGameWithScores(final String pin, final Game game) {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("host", 0);
        scores.put("player1", 500);
        scores.put("player2", 300);

        when(gameRepository.findByPin(pin)).thenReturn(game);
        when(currentGame.getQuestionsOnAPin(pin)).thenReturn(new ArrayList<>());
        when(currentGame.getActiveUsersInGame(pin)).thenReturn(List.of("host", "player1", "player2"));
        when(currentGame.getCurrentScore(pin)).thenReturn(scores);
        when(currentGame.findModerator(pin)).thenReturn("host");
        when(currentGame.getStreak(eq(pin), any())).thenReturn(0);
    }

    private List<QuestionSet> createTestQuestions(final int count) {
        List<QuestionSet> questions = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            QuestionSet qs = new QuestionSet();
            qs.setQuestion("Question " + i);
            qs.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            questions.add(qs);
        }
        return questions;
    }
}
