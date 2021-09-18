package com.quiz.darkhold.score.controller;

import com.quiz.darkhold.game.model.CurrentScore;
import com.quiz.darkhold.game.service.GameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ScoreController {

    private final Logger logger = LogManager.getLogger(ScoreController.class);

    private final GameService gameService;

    public ScoreController(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * show the till now score and display who are the top 3 players.
     *
     * @param model model
     * @return to scoreboard
     */
    @PostMapping("/scoreboard")
    public String scoreCheck(final Model model) {
        logger.info("On to the scoreboard screen");
        var score = new CurrentScore();
        var scores = gameService.getCurrentScore();
        scores.forEach((key, value) ->
                logger.info(new StringBuilder()
                        .append("key is:").append(key)
                        .append(", and value is :").append(value).toString()));
        score.setScore(scores);
        model.addAttribute("score", score);
        return "scoreboard";
    }

    @MessageMapping("/next_question")
    @SendTo("/topic/ready_for_question")
    public Boolean scoresFetch() {
        logger.info("On to ready_for_question");
        return Boolean.TRUE;
    }
}
