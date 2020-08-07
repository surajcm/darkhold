package com.quiz.darkhold.preview.service;

import com.quiz.darkhold.challenge.entity.Challenge;
import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.entity.Game;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.model.PreviewInfo;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.repository.CurrentGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PreviewServiceTest {
    private final PreviewService previewService = new PreviewService();
    private final ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    private final GameRepository gameRepository = Mockito.mock(GameRepository.class);
    private final CurrentGame currentGame = Mockito.mock(CurrentGame.class);

    @BeforeEach
    public void setup() {
        Whitebox.setInternalState(previewService, "challengeRepository", challengeRepository);
        Whitebox.setInternalState(previewService, "gameRepository", gameRepository);
        Whitebox.setInternalState(previewService, "currentGame", currentGame);
    }

    @Test
    void fetchQuestions() {
        when(challengeRepository.getOne(anyLong())).thenReturn(mockChallenge());
        String challengeId = "1234";
        PreviewInfo previewInfo = previewService.fetchQuestions(challengeId);
        Assertions.assertEquals(challengeId, previewInfo.getChallengeId());
    }

    @Test
    void fetchQuestionsFromPin() {
        String challengeId = "1234";
        when(gameRepository.findByPin(anyString())).thenReturn(mockGame(challengeId));
        when(challengeRepository.findById(anyLong())).thenReturn(mockOptionalChallenge());
        PreviewInfo previewInfo = previewService.fetchQuestionsFromPin(challengeId);
        Assertions.assertEquals(challengeId, previewInfo.getChallengeId());
    }

    @Test
    void generateQuizPin() {
        String user = "USER";
        when(gameRepository.findByPin(anyString())).thenReturn(mockGame("1234"));
        PublishInfo publishInfo = previewService.generateQuizPin("1234", user);
        Assertions.assertEquals(user, publishInfo.getModerator());
    }

    @Test
    void getActiveChallenge() {
        String challengeId = "1234";
        when(gameRepository.findByGameStatusNot(anyString())).thenReturn(mockGames(challengeId));
        PublishInfo publishInfo = previewService.getActiveChallenge();
        Assertions.assertEquals(challengeId, publishInfo.getPin());
    }

    private Optional<Challenge> mockOptionalChallenge() {
        return Optional.of(mockChallenge());
    }

    private Challenge mockChallenge() {
        Challenge challenge = new Challenge();
        challenge.setQuestionSets(new ArrayList<>());
        challenge.setTitle("hello");
        return challenge;
    }

    private Game mockGame(final String challengeId) {
        Game game = new Game();
        game.setChallengeId(challengeId);
        return game;
    }

    private List<Game> mockGames(final String challengeId) {
        Game game = new Game();
        game.setPin(challengeId);
        List<Game> games = new ArrayList<>();
        games.add(game);
        return games;
    }
}