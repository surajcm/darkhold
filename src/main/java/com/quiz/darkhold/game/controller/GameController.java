package com.quiz.darkhold.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @PostMapping("/game")
    public String startGame() {
        logger.info("on to game");
        return "game";
    }
}
