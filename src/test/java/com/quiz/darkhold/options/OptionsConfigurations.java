package com.quiz.darkhold.options;

import com.quiz.darkhold.challenge.repository.ChallengeRepository;
import com.quiz.darkhold.game.repository.GameRepository;
import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.preview.repository.CurrentGameSessionRepository;
import com.quiz.darkhold.user.repository.UserRepository;
import com.quiz.darkhold.options.controller.OptionsController;
import com.quiz.darkhold.options.service.OptionsService;
import com.quiz.darkhold.preview.repository.CurrentGame;
import com.quiz.darkhold.preview.service.PreviewService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class OptionsConfigurations {
    @Bean
    public OptionsController optionsController() {
        return new OptionsController(
                Mockito.mock(OptionsService.class),
                Mockito.mock(PreviewService.class),
                Mockito.mock(GameService.class));
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
    public CurrentGameSessionRepository currentGameSessionRepository() {
        return Mockito.mock(CurrentGameSessionRepository.class);
    }

}
