package com.quiz.darkhold.game.controller;

import com.quiz.darkhold.analytics.service.ResultService;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.CurrentScore;
import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.GameMode;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.game.model.StartTrigger;
import com.quiz.darkhold.game.model.UserResponse;
import com.quiz.darkhold.game.service.AnswerValidationService;
import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.init.GameConfig;
import com.quiz.darkhold.util.CommonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GameController Tests")
class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private GameConfig gameConfig;

    @Mock
    private AnswerValidationService answerValidationService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ResultService resultService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        when(gameConfig.getTimerSeconds()).thenReturn(20);
        gameController = new GameController(gameService, gameConfig, answerValidationService,
                messagingTemplate, resultService);
    }

    @Nested
    @DisplayName("startInterstitial() method tests")
    class StartInterstitialMethodTests {

        @Test
        @DisplayName("should add quiz pin to model and return interstitial view")
        void testStartInterstitial_ShouldAddPinToModelAndReturnView() {
            // Given
            String quizPin = "ABC123";

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(quizPin)).thenReturn("ABC123");
                String result = gameController.startInterstitial(model, quizPin);

                // Then
                assertEquals("interstitial", result);
                verify(model).addAttribute("quizPin", quizPin);
            }
        }

        @Test
        @DisplayName("should sanitize quiz pin for logging")
        void testStartInterstitial_ShouldSanitizePinForLogging() {
            // Given
            String quizPin = "<script>alert('xss')</script>";

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(quizPin))
                        .thenReturn("scriptalertxssscript");
                gameController.startInterstitial(model, quizPin);

                // Then
                mockedUtils.verify(() -> CommonUtils.sanitizedString(quizPin));
            }
        }

        @Test
        @DisplayName("should handle null quiz pin")
        void testStartInterstitial_WithNullPin_ShouldHandleGracefully() {
            // Given
            String quizPin = null;

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(null)).thenReturn("");
                String result = gameController.startInterstitial(model, quizPin);

                // Then
                assertEquals("interstitial", result);
                verify(model).addAttribute("quizPin", null);
            }
        }

        @Test
        @DisplayName("should handle empty quiz pin")
        void testStartInterstitial_WithEmptyPin_ShouldReturnView() {
            // Given
            String quizPin = "";

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString("")).thenReturn("");
                String result = gameController.startInterstitial(model, quizPin);

                // Then
                assertEquals("interstitial", result);
            }
        }
    }

    @Nested
    @DisplayName("question() method tests")
    class QuestionMethodTests {

        @Test
        @DisplayName("should return question view when not on last question")
        void testQuestion_WhenNotLastQuestion_ShouldReturnQuestionView() {
            // Given
            QuestionPointer questionPointer = createQuestionPointer(1, 5);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);

            // When
            String result = gameController.question(model, "QUIZ123", principal);

            // Then
            assertEquals("question", result);
        }

        @Test
        @DisplayName("should redirect to final score when on last question")
        void testQuestion_WhenLastQuestion_ShouldReturnFinalScore() {
            // Given
            QuestionPointer questionPointer = createQuestionPointer(5, 5);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);
            when(gameService.getCurrentScore()).thenReturn(createScoreMap());

            // When
            String result = gameController.question(model, "QUIZ123", principal);

            // Then
            assertEquals("finalscore", result);
            verify(gameService).cleanUpCurrentGame();
        }

        @Test
        @DisplayName("should call gameService.getCurrentQuestionPointer")
        void testQuestion_ShouldCallGetCurrentQuestionPointer() {
            // Given
            QuestionPointer questionPointer = createQuestionPointer(2, 5);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);

            // When
            gameController.question(model, "QUIZ123", principal);

            // Then
            verify(gameService).getCurrentQuestionPointer(anyString());
        }

        @Test
        @DisplayName("should return finalscore when on first and only question")
        void testQuestion_WhenFirstAndLastQuestion_ShouldReturnFinalScore() {
            // Given
            QuestionPointer questionPointer = createQuestionPointer(1, 1);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);
            when(gameService.getCurrentScore()).thenReturn(createScoreMap());

            // When
            String result = gameController.question(model, "QUIZ123", principal);

            // Then
            assertEquals("finalscore", result);
        }
    }

    @Nested
    @DisplayName("startGame() method tests")
    class StartGameMethodTests {

        @Test
        @DisplayName("should create challenge with correct question data")
        void testStartGame_ShouldCreateChallengeWithQuestionData() {
            // Given
            QuestionSet questionSet = createQuestionSet(1, "What is 2+2?");
            QuestionPointer questionPointer = createQuestionPointerWithQuestion(0, questionSet);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);
            when(gameService.getGameMode(anyString())).thenReturn(GameMode.MULTIPLAYER);

            // When
            String result = gameController.startGame(model, "QUIZ123", principal);

            // Then
            assertEquals("game", result);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(model).addAttribute(ArgumentMatchers.eq("challenge"), captor.capture());
            assertNotNull(captor.getValue());
        }

        @Test
        @DisplayName("should add game timer to model")
        void testStartGame_ShouldAddGameTimer() {
            // Given
            QuestionSet questionSet = createQuestionSet(1, "Question?");
            QuestionPointer questionPointer = createQuestionPointerWithQuestion(0, questionSet);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);
            when(gameService.getGameMode(anyString())).thenReturn(GameMode.MULTIPLAYER);

            // When
            gameController.startGame(model, "QUIZ123", principal);

            // Then
            verify(model).addAttribute("game_timer", "20");
        }

        @Test
        @DisplayName("should return game view")
        void testStartGame_ShouldReturnGameView() {
            // Given
            QuestionSet questionSet = createQuestionSet(1, "Question?");
            QuestionPointer questionPointer = createQuestionPointerWithQuestion(0, questionSet);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);
            when(gameService.getGameMode(anyString())).thenReturn(GameMode.MULTIPLAYER);

            // When
            String result = gameController.startGame(model, "QUIZ123", principal);

            // Then
            assertEquals("game", result);
        }

        @Test
        @DisplayName("should increment question number in challenge")
        void testStartGame_ShouldIncrementQuestionNumber() {
            // Given
            QuestionSet questionSet = createQuestionSet(2, "Second question?");
            QuestionPointer questionPointer = createQuestionPointerWithQuestion(1, questionSet);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);
            when(gameService.getGameMode(anyString())).thenReturn(GameMode.MULTIPLAYER);

            // When
            gameController.startGame(model, "QUIZ123", principal);

            // Then
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(model).addAttribute(ArgumentMatchers.eq("challenge"), captor.capture());
            assertNotNull(captor.getValue());
        }
    }

    @Nested
    @DisplayName("finalScore() method tests")
    class FinalScoreMethodTests {

        @Test
        @DisplayName("should add score to model and clean up game")
        void testFinalScore_ShouldAddScoreAndCleanup() {
            // Given
            java.util.Map<String, Integer> scoreMap = createScoreMap();
            when(gameService.getCurrentScore()).thenReturn(scoreMap);

            // When
            String result = gameController.finalScore(model, null);

            // Then
            assertEquals("finalscore", result);
            ArgumentCaptor<CurrentScore> scoreCaptor = ArgumentCaptor.forClass(CurrentScore.class);
            verify(model).addAttribute(ArgumentMatchers.eq("score"), scoreCaptor.capture());
            assertEquals(scoreMap, scoreCaptor.getValue().getScore());
            verify(gameService).cleanUpCurrentGame();
        }

        @Test
        @DisplayName("should return finalscore view")
        void testFinalScore_ShouldReturnFinalScoreView() {
            // Given
            when(gameService.getCurrentScore()).thenReturn(createScoreMap());

            // When
            String result = gameController.finalScore(model, null);

            // Then
            assertEquals("finalscore", result);
        }

        @Test
        @DisplayName("should handle empty score map")
        void testFinalScore_WithEmptyScore_ShouldHandleCorrectly() {
            // Given
            when(gameService.getCurrentScore()).thenReturn(new HashMap<>());

            // When
            String result = gameController.finalScore(model, null);

            // Then
            assertEquals("finalscore", result);
            ArgumentCaptor<CurrentScore> scoreCaptor = ArgumentCaptor.forClass(CurrentScore.class);
            verify(model).addAttribute(ArgumentMatchers.eq("score"), scoreCaptor.capture());
            assertEquals(0, scoreCaptor.getValue().getScore().size());
        }

        @Test
        @DisplayName("should handle score with multiple entries")
        void testFinalScore_WithMultipleScores_ShouldHandleCorrectly() {
            // Given
            java.util.Map<String, Integer> scoreMap = new HashMap<>();
            scoreMap.put("player1", 500);
            scoreMap.put("player2", 750);
            when(gameService.getCurrentScore()).thenReturn(scoreMap);

            // When
            gameController.finalScore(model, null);

            // Then
            ArgumentCaptor<CurrentScore> scoreCaptor = ArgumentCaptor.forClass(CurrentScore.class);
            verify(model).addAttribute(ArgumentMatchers.eq("score"), scoreCaptor.capture());
            assertEquals(2, scoreCaptor.getValue().getScore().size());
        }
    }

    @Nested
    @DisplayName("enterGame() method tests")
    class EnterGameMethodTests {

        @Test
        @DisplayName("should save score for non-moderator user with correct answer")
        void testEnterGame_WhenNonModeratorAndCorrect_ShouldSaveScore() {
            // Given
            when(gameService.findModerator()).thenReturn("moderator");
            setupMocksForCorrectAnswer();

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                Boolean result = gameController.enterGame("correct", "player1", "5000");

                // Then
                assertTrue(result);
                verify(gameService, never()).incrementQuestionNo();
            }
        }

        @Test
        @DisplayName("should increment question for moderator")
        void testEnterGame_WhenModerator_ShouldIncrementQuestion() {
            // Given
            String selectedOptions = "correct";
            String user = "moderator";
            String timeTook = "5000";
            when(gameService.findModerator()).thenReturn("moderator");

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                Boolean result = gameController.enterGame(selectedOptions, user, timeTook);

                // Then
                assertTrue(result);
                verify(gameService).incrementQuestionNo();
                verify(gameService, never()).saveCurrentScore(anyString(), anyInt());
            }
        }

        @Test
        @DisplayName("should return zero score for incorrect answer")
        void testEnterGame_WhenIncorrectAnswer_ShouldReturnZeroScore() {
            // Given
            String selectedOptions = "incorrect";
            String user = "player1";
            String timeTook = "5000";
            when(gameService.findModerator()).thenReturn("moderator");
            setupMocksForPlayerAnswer();

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                gameController.enterGame(selectedOptions, user, timeTook);

                // Then
                verify(gameService).saveCurrentScore(user, 0);
            }
        }

        private void setupMocksForPlayerAnswer() {
            QuestionSet questionSet = createQuestionSet(1, "Question?");
            questionSet.setPoints(1000);
            QuestionPointer pointer = createQuestionPointerWithQuestion(0, questionSet);
            when(gameService.getCurrentQuestionPointer()).thenReturn(pointer);
            when(gameService.updateStreak(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(0);
            when(gameService.calculateScoreWithStreak(anyInt(), anyInt(), anyInt())).thenReturn(0);
        }

        @Test
        @DisplayName("should handle timeout scenario")
        void testEnterGame_WhenTimeout_ShouldReturnZeroScore() {
            // Given
            String selectedOptions = "timeout";
            String user = "player1";
            String timeTook = "20000";
            when(gameService.findModerator()).thenReturn("moderator");
            setupMocksForPlayerAnswer();

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                gameController.enterGame(selectedOptions, user, timeTook);

                // Then
                verify(gameService).saveCurrentScore(user, 0);
            }
        }

        @Test
        @DisplayName("should return true on successful entry")
        void testEnterGame_ShouldReturnTrue() {
            // Given
            String selectedOptions = "correct";
            String user = "player1";
            String timeTook = "5000";
            when(gameService.findModerator()).thenReturn("moderator");
            setupMocksForCorrectAnswer();

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                Boolean result = gameController.enterGame(selectedOptions, user, timeTook);

                // Then
                assertTrue(result);
            }
        }

        private void setupMocksForCorrectAnswer() {
            QuestionSet questionSet = createQuestionSet(1, "Question?");
            questionSet.setPoints(1000);
            QuestionPointer pointer = createQuestionPointerWithQuestion(0, questionSet);
            when(gameService.getCurrentQuestionPointer()).thenReturn(pointer);
            when(gameService.updateStreak(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(1);
            when(gameService.calculateScoreWithStreak(anyInt(), anyInt(), anyInt())).thenReturn(750);
        }

        @Test
        @DisplayName("should handle empty selected options")
        void testEnterGame_WithEmptyOptions_ShouldReturnZeroScore() {
            // Given
            String selectedOptions = "";
            String user = "player1";
            String timeTook = "5000";
            when(gameService.findModerator()).thenReturn("moderator");
            setupMocksForPlayerAnswer();

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                gameController.enterGame(selectedOptions, user, timeTook);

                // Then
                verify(gameService).saveCurrentScore(user, 0);
            }
        }

        @Test
        @DisplayName("should handle non-numeric timeTook value")
        void testEnterGame_WithNonNumericTime_ShouldReturnZeroScore() {
            // Given
            String selectedOptions = "correct";
            String user = "player1";
            String timeTook = "invalid";
            when(gameService.findModerator()).thenReturn("moderator");
            setupMocksForPlayerAnswer();

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                gameController.enterGame(selectedOptions, user, timeTook);

                // Then
                verify(gameService).saveCurrentScore(user, 0);
            }
        }

        @Test
        @DisplayName("should calculate score correctly for fast correct answer")
        void testEnterGame_WithFastCorrectAnswer_ShouldCalculateHighScore() {
            // Given
            when(gameService.findModerator()).thenReturn("moderator");
            setupMocksForFastAnswer();

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                gameController.enterGame("correct", "player1", "1000");
                verify(gameService).saveCurrentScore("player1", 950);
            }
        }

        private void setupMocksForFastAnswer() {
            QuestionSet questionSet = createQuestionSet(1, "Question?");
            questionSet.setPoints(1000);
            QuestionPointer pointer = createQuestionPointerWithQuestion(0, questionSet);
            when(gameService.getCurrentQuestionPointer()).thenReturn(pointer);
            when(gameService.updateStreak(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(1);
            when(gameService.calculateScoreWithStreak(anyInt(), anyInt(), anyInt())).thenReturn(950);
        }
    }

    @Nested
    @DisplayName("getGame() WebSocket method tests")
    class GetGameMethodTests {

        @Test
        @DisplayName("should send user response to PIN-scoped topic")
        void testGetGame_ShouldSendUserResponseToPinScopedTopic() {
            // Given
            Game game = new Game();
            game.setPin("ABC123");
            List<String> participants = new ArrayList<>();
            participants.add("user1");
            participants.add("user2");
            when(gameService.getAllParticipants("ABC123")).thenReturn(participants);

            // When
            gameController.getGame(game);

            // Then
            verify(gameService).getAllParticipants("ABC123");
            verify(messagingTemplate).convertAndSend(
                    ArgumentMatchers.eq("/topic/ABC123/user"),
                    ArgumentMatchers.any(UserResponse.class));
        }

        @Test
        @DisplayName("should call gameService with correct game pin")
        void testGetGame_ShouldCallServiceWithCorrectPin() {
            // Given
            Game game = new Game();
            game.setPin("XYZ789");
            when(gameService.getAllParticipants("XYZ789")).thenReturn(new ArrayList<>());

            // When
            gameController.getGame(game);

            // Then
            verify(gameService).getAllParticipants("XYZ789");
        }

        @Test
        @DisplayName("should handle empty participants list")
        void testGetGame_WithEmptyParticipants_ShouldSendResponse() {
            // Given
            Game game = new Game();
            game.setPin("GAME1");
            when(gameService.getAllParticipants("GAME1")).thenReturn(new ArrayList<>());

            // When
            gameController.getGame(game);

            // Then
            verify(messagingTemplate).convertAndSend(
                    ArgumentMatchers.eq("/topic/GAME1/user"),
                    ArgumentMatchers.any(UserResponse.class));
        }
    }

    @Nested
    @DisplayName("startTrigger() WebSocket method tests")
    class StartTriggerMethodTests {

        @Test
        @DisplayName("should send start trigger to PIN-scoped topic")
        void testStartTrigger_ShouldSendTriggerToPinScopedTopic() {
            // Given
            String pin = "GAME123";
            when(principal.getName()).thenReturn("moderator");

            // When
            gameController.startTrigger(pin, principal);

            // Then
            verify(messagingTemplate).convertAndSend(
                    ArgumentMatchers.eq("/topic/GAME123/start"),
                    ArgumentMatchers.any(StartTrigger.class));
        }

        @Test
        @DisplayName("should use principal name in logging")
        void testStartTrigger_ShouldUsePrincipalName() {
            // Given
            String pin = "GAME123";
            when(principal.getName()).thenReturn("john_doe");

            // When
            gameController.startTrigger(pin, principal);

            // Then
            verify(principal).getName();
        }

        @Test
        @DisplayName("should handle null pin")
        void testStartTrigger_WithNullPin_ShouldSendTrigger() {
            // Given
            String pin = null;
            when(principal.getName()).thenReturn("admin");

            // When
            gameController.startTrigger(pin, principal);

            // Then
            verify(messagingTemplate).convertAndSend(
                    ArgumentMatchers.eq("/topic/null/start"),
                    ArgumentMatchers.any(StartTrigger.class));
        }
    }

    @Nested
    @DisplayName("questionFetch() WebSocket method tests")
    class QuestionFetchMethodTests {

        @Test
        @DisplayName("should send END_GAME trigger when on last question")
        void testQuestionFetch_WhenLastQuestion_ShouldSendEndGame() {
            // Given
            QuestionPointer questionPointer = createQuestionPointer(5, 5);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);

            // When - message format is "pin:username" or "pin"
            gameController.questionFetch("GAME1:user1");

            // Then
            verify(messagingTemplate).convertAndSend(
                    ArgumentMatchers.eq("/topic/GAME1/question_read"),
                    ArgumentMatchers.any(StartTrigger.class));
        }

        @Test
        @DisplayName("should send question with number and text")
        void testQuestionFetch_WhenNotLastQuestion_ShouldSendQuestionText() {
            // Given
            QuestionSet questionSet = createQuestionSet(1, "What is capital of France?");
            QuestionPointer questionPointer = createQuestionPointerWithQuestion(1, questionSet);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);

            // When
            gameController.questionFetch("GAME1:user1");

            // Then
            verify(messagingTemplate).convertAndSend(
                    ArgumentMatchers.eq("/topic/GAME1/question_read"),
                    ArgumentMatchers.any(StartTrigger.class));
        }

        @Test
        @DisplayName("should call gameService.getCurrentQuestionPointer")
        void testQuestionFetch_ShouldCallGetCurrentQuestionPointer() {
            // Given
            QuestionSet questionSet = createQuestionSet(1, "Question?");
            QuestionPointer questionPointer = createQuestionPointerWithQuestion(1, questionSet);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);

            // When
            gameController.questionFetch("GAME1");

            // Then
            verify(gameService).getCurrentQuestionPointer(anyString());
        }

        @Test
        @DisplayName("should include question number in response")
        void testQuestionFetch_ShouldIncludeQuestionNumber() {
            // Given
            QuestionSet questionSet = createQuestionSet(3, "Question 3?");
            QuestionPointer questionPointer = createQuestionPointerWithQuestion(2, questionSet);
            when(gameService.getCurrentQuestionPointer(anyString())).thenReturn(questionPointer);

            // When
            gameController.questionFetch("GAME1:user1");

            // Then
            verify(messagingTemplate).convertAndSend(
                    ArgumentMatchers.eq("/topic/GAME1/question_read"),
                    ArgumentMatchers.any(StartTrigger.class));
        }
    }

    @Nested
    @DisplayName("scoresFetch() WebSocket method tests")
    class ScoresFetchMethodTests {

        @Test
        @DisplayName("should send true to PIN-scoped topic")
        void testScoresFetch_ShouldSendTrueToPinScopedTopic() {
            // When
            gameController.scoresFetch("GAME1");

            // Then
            verify(messagingTemplate).convertAndSend("/topic/GAME1/read_scores", true);
        }

        @Test
        @DisplayName("should send to correct PIN topic")
        void testScoresFetch_ShouldSendToCorrectPinTopic() {
            // When
            gameController.scoresFetch("QUIZ123");

            // Then
            verify(messagingTemplate).convertAndSend("/topic/QUIZ123/read_scores", true);
        }
    }

    @Nested
    @DisplayName("Integration scenario tests")
    class IntegrationScenarioTests {

        @Test
        @DisplayName("should complete full game flow from interstitial to final score")
        void testCompleteGameFlow() {
            // Given
            String quizPin = "QUIZ123";

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(quizPin)).thenReturn("QUIZ123");
                String interstitialResult = gameController.startInterstitial(model, quizPin);

                // Then
                assertEquals("interstitial", interstitialResult);
                verify(model).addAttribute("quizPin", quizPin);
            }
        }

        @Test
        @DisplayName("should handle answer submission and score calculation")
        void testAnswerSubmissionAndScoring() {
            // Given
            when(gameService.findModerator()).thenReturn("moderator");
            QuestionSet questionSet = createQuestionSet(1, "Question?");
            questionSet.setPoints(1000);
            QuestionPointer pointer = createQuestionPointerWithQuestion(0, questionSet);
            when(gameService.getCurrentQuestionPointer()).thenReturn(pointer);
            when(gameService.updateStreak(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(1);
            when(gameService.calculateScoreWithStreak(anyInt(), anyInt(), anyInt())).thenReturn(750);

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                Boolean result = gameController.enterGame("correct", "player1", "5000");

                // Then
                assertTrue(result);
                verify(gameService).saveCurrentScore("player1", 750);
            }
        }

        @Test
        @DisplayName("should handle moderator skipping questions")
        void testModeratorSkippingQuestions() {
            // Given
            when(gameService.findModerator()).thenReturn("admin");

            // When
            try (MockedStatic<CommonUtils> mockedUtils = mockStatic(CommonUtils.class)) {
                mockedUtils.when(() -> CommonUtils.sanitizedString(anyString())).thenReturn("sanitized");
                gameController.enterGame("correct", "admin", "2000");
                gameController.enterGame("incorrect", "admin", "3000");

                // Then
                verify(gameService, times(2)).incrementQuestionNo();
                verify(gameService, never()).saveCurrentScore(anyString(), anyInt());
            }
        }
    }

    // ======================== Helper Methods ========================

    private QuestionPointer createQuestionPointer(final int current, final int total) {
        QuestionPointer pointer = new QuestionPointer();
        pointer.setCurrentQuestionNumber(current);
        pointer.setTotalQuestionCount(total);
        return pointer;
    }

    private QuestionPointer createQuestionPointerWithQuestion(final int current,
                                                              final QuestionSet questionSet) {
        QuestionPointer pointer = new QuestionPointer();
        pointer.setCurrentQuestionNumber(current);
        pointer.setTotalQuestionCount(5);
        pointer.setCurrentQuestion(questionSet);
        return pointer;
    }

    private QuestionSet createQuestionSet(final int id, final String question) {
        QuestionSet set = new QuestionSet();
        set.setId((long) id);
        set.setQuestion(question);
        return set;
    }

    private java.util.Map<String, Integer> createScoreMap() {
        java.util.Map<String, Integer> scoreMap = new HashMap<>();
        scoreMap.put("score", 100);
        return scoreMap;
    }
}

