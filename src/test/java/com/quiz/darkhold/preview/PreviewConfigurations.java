package com.quiz.darkhold.preview;

import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.preview.controller.PreviewController;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import org.dizitart.no2.NitriteCollection;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class PreviewConfigurations {
    @Bean
    public PreviewController previewController() {
        return new PreviewController(Mockito.mock(PreviewService.class));
    }

    @Bean
    public PreviewService previewService() {
        return Mockito.mock(PreviewService.class);
    }

    @Bean
    public ChallengeRepository challengeRepository() {
        return Mockito.mock(ChallengeRepository.class);
    }

    @Bean
    public GameRepository gameRepository() {
        return Mockito.mock(GameRepository.class);
    }

    @Bean
    public CurrentGame currentGame() {
        return Mockito.mock(CurrentGame.class);
    }

    @Bean
    public NitriteCollection nitriteCollection() {
        return Mockito.mock(NitriteCollection.class);
    }
}
