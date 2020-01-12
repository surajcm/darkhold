package com.quiz.darkhold.game.controller;

import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.UserResponse;
import com.quiz.darkhold.game.service.GameService;
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

    @PostMapping("/game")
    public String startGame(Model model, @RequestParam("quiz_pin") String quiz_pin) {
        logger.info("On to game :"+ quiz_pin);
        return "game";
    }

    @MessageMapping("/user")
    @SendTo("/topic/user")
    public UserResponse getGame(Game game) {
        logger.info("On to getGame :"+ game);
        List<String> users = gameService.getAllParticipants(game.getPin());
        //put this in response
        return new UserResponse("Hi " + game.getName());
    }
}
