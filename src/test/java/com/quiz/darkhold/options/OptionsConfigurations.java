package com.quiz.darkhold.options;

import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.login.repository.UserRepository;
import com.quiz.darkhold.options.controller.OptionsController;
import com.quiz.darkhold.options.service.OptionsService;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import org.dizitart.no2.NitriteCollection;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class OptionsConfigurations {
    @Bean
    public OptionsController optionsController() {
        return new OptionsController(
                Mockito.mock(OptionsService.class),
                Mockito.mock(PreviewService.class));
    }

    @Bean
    public OptionsService optionsService() {
        return Mockito.mock(OptionsService.class);
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
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public CurrentGame currentGame() {
        return Mockito.mock(CurrentGame.class);
    }

    @Bean
    public NitriteCollection nitriteCollection() {
        return Mockito.mock(NitriteCollection.class);
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return Mockito.mock(FlywayMigrationStrategy.class);
    }
}
