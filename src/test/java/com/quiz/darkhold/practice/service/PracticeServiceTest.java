package com.quiz.darkhold.practice.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.preview.entity.CurrentGameSession;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGameSessionRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PracticeService Tests")
class PracticeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private CurrentGameSessionRepository sessionRepository;

    @Mock
    private HttpSession httpSession;

    private PracticeService practiceService;

    @BeforeEach
    void setUp() {
        practiceService = new PracticeService(challengeRepository, sessionRepository);
    }

    @Nested
    @DisplayName("initializePracticeGame tests")
    class InitializePracticeGameTests {

        @Test
        @DisplayName("Should return PublishInfo with PRACTICE- prefixed pin")
        void shouldReturnPublishInfoWithPracticePrefixedPin() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            PublishInfo result = practiceService.initializePracticeGame("1", "player1", httpSession);

            assertNotNull(result);
            assertTrue(result.getPin().startsWith("PRACTICE-"));
        }

        @Test
        @DisplayName("Should set moderator to playerName in PublishInfo")
        void shouldSetModeratorToPlayerName() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            PublishInfo result = practiceService.initializePracticeGame("1", "testPlayer", httpSession);

            assertEquals("testPlayer", result.getModerator());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when challenge not found")
        void shouldThrowWhenChallengeNotFound() {
            when(challengeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> practiceService.initializePracticeGame("999", "player1", httpSession));
        }

        @Test
        @DisplayName("Should save session with GameMode PRACTICE")
        void shouldSaveSessionWithPracticeGameMode() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            practiceService.initializePracticeGame("1", "player1", httpSession);

            ArgumentCaptor<CurrentGameSession> captor = ArgumentCaptor.forClass(CurrentGameSession.class);
            verify(sessionRepository).save(captor.capture());
            assertEquals(com.quiz.darkhold.game.model.GameMode.PRACTICE, captor.getValue().getGameMode());
        }

        @Test
        @DisplayName("Should set users list with playerName")
        void shouldSetUsersListWithPlayerName() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            practiceService.initializePracticeGame("1", "player1", httpSession);

            ArgumentCaptor<CurrentGameSession> captor = ArgumentCaptor.forClass(CurrentGameSession.class);
            verify(sessionRepository).save(captor.capture());
            List<String> users = captor.getValue().getUsersList();
            assertNotNull(users);
            assertEquals(1, users.size());
            assertEquals("player1", users.get(0));
        }

        @Test
        @DisplayName("Should set currentQuestionNo to 0")
        void shouldSetCurrentQuestionNoToZero() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            practiceService.initializePracticeGame("1", "player1", httpSession);

            ArgumentCaptor<CurrentGameSession> captor = ArgumentCaptor.forClass(CurrentGameSession.class);
            verify(sessionRepository).save(captor.capture());
            assertEquals(0, captor.getValue().getCurrentQuestionNo());
        }

        @Test
        @DisplayName("Should set gameStatus to ACTIVE")
        void shouldSetGameStatusToActive() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            practiceService.initializePracticeGame("1", "player1", httpSession);

            ArgumentCaptor<CurrentGameSession> captor = ArgumentCaptor.forClass(CurrentGameSession.class);
            verify(sessionRepository).save(captor.capture());
            assertEquals(com.quiz.darkhold.game.model.GameStatus.ACTIVE, captor.getValue().getGameStatus());
        }

        @Test
        @DisplayName("Should store practiceId in HttpSession as gamePin")
        void shouldStorePracticeIdInHttpSession() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            PublishInfo result = practiceService.initializePracticeGame("1", "player1", httpSession);

            verify(httpSession).setAttribute(eq("gamePin"), eq(result.getPin()));
        }

        @Test
        @DisplayName("Should load questions from challenge")
        void shouldLoadQuestionsFromChallenge() {
            Challenge challenge = createTestChallenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            practiceService.initializePracticeGame("1", "player1", httpSession);

            ArgumentCaptor<CurrentGameSession> captor = ArgumentCaptor.forClass(CurrentGameSession.class);
            verify(sessionRepository).save(captor.capture());
            assertNotNull(captor.getValue().getQuestionsJson());
        }
    }

    // Helper methods

    private Challenge createTestChallenge() {
        Challenge challenge = new Challenge();
        challenge.setId(1L);
        challenge.setTitle("Test Challenge");
        challenge.setDescription("Test Description");
        QuestionSet qs = new QuestionSet();
        qs.setQuestion("What is 2+2?");
        qs.setAnswer1("3");
        qs.setAnswer2("4");
        qs.setAnswer3("5");
        qs.setAnswer4("6");
        challenge.setQuestionSets(new ArrayList<>(List.of(qs)));
        return challenge;
    }
}
