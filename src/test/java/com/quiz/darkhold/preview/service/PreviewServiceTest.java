package com.quiz.darkhold.preview.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PreviewServiceTest {
    private final ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    private final GameRepository gameRepository = Mockito.mock(GameRepository.class);
    private final CurrentGame currentGame = Mockito.mock(CurrentGame.class);
    private final PreviewService previewService = new PreviewService(
            challengeRepository,
            gameRepository,
            currentGame);

    @Test
    void fetchQuestions() {
        when(challengeRepository.getById(anyLong())).thenReturn(mockChallenge());
        var challengeId = "1234";
        var previewInfo = previewService.fetchQuestions(challengeId);
        Assertions.assertEquals(challengeId, previewInfo.getChallengeId());
    }

    @Test
    void fetchQuestionsFromPin() {
        var challengeId = "1234";
        when(gameRepository.findByPin(anyString())).thenReturn(mockGame(challengeId));
        when(challengeRepository.findById(anyLong())).thenReturn(mockOptionalChallenge());
        var previewInfo = previewService.fetchQuestionsFromPin(challengeId);
        Assertions.assertEquals(challengeId, previewInfo.getChallengeId());
    }

    @Test
    void generateQuizPin() {
        var user = "USER";
        when(gameRepository.findByPin(anyString())).thenReturn(mockGame("1234"));
        var publishInfo = previewService.generateQuizPin("1234", user);
        Assertions.assertEquals(user, publishInfo.getModerator());
    }

    @Test
    void getActiveChallenge() {
        var challengeId = "1234";
        when(gameRepository.findByGameStatusNot(anyString())).thenReturn(mockGames(challengeId));
        var publishInfo = previewService.getActiveChallenge();
        Assertions.assertEquals(challengeId, publishInfo.getPin());
    }

    private Optional<Challenge> mockOptionalChallenge() {
        return Optional.of(mockChallenge());
    }

    private Challenge mockChallenge() {
        var challenge = new Challenge();
        challenge.setQuestionSets(new ArrayList<>());
        challenge.setTitle("hello");
        return challenge;
    }

    private Game mockGame(final String challengeId) {
        var game = new Game();
        game.setChallengeId(challengeId);
        return game;
    }

    private List<Game> mockGames(final String challengeId) {
        var game = new Game();
        game.setPin(challengeId);
        return List.of(game);
    }
}