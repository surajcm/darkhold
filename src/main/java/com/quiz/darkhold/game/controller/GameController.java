package com.quiz.darkhold.game.controller;

import com.quiz.darkhold.game.model.Challenge;
import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.QuestionOnGame;
import com.quiz.darkhold.game.model.StartTrigger;
import com.quiz.darkhold.game.model.UserResponse;
import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.preview.model.PublishInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@SuppressWarnings("unused")
@Controller
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @PostMapping("/interstitial")
    public String startInterstitial(Model model, @RequestParam("quiz_pin") String quiz_pin) {
        logger.info("On to interstitial :" + quiz_pin);
        return "interstitial";
    }

    @PostMapping("/question")
    public String question(Model model) {
        logger.info("On to question :");
        PublishInfo publishInfo = gameService.getActiveChallenge();
        int currentQuestionNumber = gameService.getCurrentQuestionNo(publishInfo.getPin());
        QuestionOnGame questionOnGame;
        if (currentQuestionNumber == -1) {
            questionOnGame = gameService.initialFetchAndUpdateNitrate(publishInfo.getPin());
        } else {
            questionOnGame = gameService.fetchAnotherQuestion(publishInfo.getPin(), currentQuestionNumber);
        }
        questionOnGame.setCurrentQuestionNumber(questionOnGame.getCurrentQuestionNumber() + 1);
        model.addAttribute("QuestionOnGame", questionOnGame);
        return "question";
    }

    @PostMapping("/game")
    public String startGame(Model model) {
        logger.info("On to game :");
        PublishInfo publishInfo = gameService.getActiveChallenge();
        int currentQuestionNumber = gameService.getCurrentQuestionNo(publishInfo.getPin());
        Challenge challenge = gameService.getCurrentQuestionSet(publishInfo.getPin(),
                currentQuestionNumber + 1);
        challenge.setQuestionNumber(challenge.getQuestionNumber() + 1);
        model.addAttribute("challenge", challenge);
        return "game";
    }

    @MessageMapping("/user")
    @SendTo("/topic/user")
    public UserResponse getGame(Game game) {
        logger.info("On to getGame :" + game);
        List<String> users = gameService.getAllParticipants(game.getPin());
        return new UserResponse(users);
    }

    @MessageMapping("/start")
    @SendTo("/topic/start")
    public StartTrigger startGame(String pin) {
        logger.info("On to startGame :" + pin);
        return new StartTrigger(pin);
    }
}
