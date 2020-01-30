package com.quiz.darkhold.game.controller;

import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.QuestionOnGame;
import com.quiz.darkhold.game.model.StartTrigger;
import com.quiz.darkhold.game.model.UserResponse;
import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.service.PreviewService;
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
    private PreviewService previewService;

    @Autowired
    private GameService gameService;

    @PostMapping("/interstitial")
    public String startInterstitial(Model model, @RequestParam("quiz_pin") String quiz_pin) {
        logger.info("On to interstitial :"+ quiz_pin);
        return "interstitial";
    }

    @PostMapping("/question")
    public String question(Model model) {
        logger.info("On to question :");
        PublishInfo publishInfo = previewService.getActiveChallenge();
        String question;
        // get current question number
        // if question number == -1, fetch the questions and load it to nitrate
        int currentQuestionNumber = gameService.getCurrentQuestionNo(publishInfo.getPin());
        if (currentQuestionNumber == -1) {
            currentQuestionNumber = 10;
        } else {
            currentQuestionNumber = 0;

        }
        // get 0 the question and add it to model
        // if question number != -1, fetch question set from nitrate and
        question = "You must create an S3 bucket before uploading your data to S3.";
        QuestionOnGame questionOnGame = new QuestionOnGame();
        questionOnGame.setCurrentQuestionNumber(currentQuestionNumber);
        questionOnGame.setQuestion(question);
        model.addAttribute("QuestionOnGame", questionOnGame);
        return "question";
    }

    @PostMapping("/game")
    public String startGame(Model model) {
        logger.info("On to game :");
        PublishInfo publishInfo = previewService.getActiveChallenge();
        int currentQuestionNumber = gameService.getCurrentQuestionNo(publishInfo.getPin());
        //get next question number + increment current q#
        //put the question to the model
        return "game";
    }

    @MessageMapping("/user")
    @SendTo("/topic/user")
    public UserResponse getGame(Game game) {
        logger.info("On to getGame :"+ game);
        List<String> users = gameService.getAllParticipants(game.getPin());
        return new UserResponse(users);
    }

    @MessageMapping("/start")
    @SendTo("/topic/start")
    public StartTrigger startGame(String pin) {
        logger.info("On to startGame :"+ pin);
        return new StartTrigger(pin);
    }
}
