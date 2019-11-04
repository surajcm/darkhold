package com.quiz.darkhold.challenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChallengeController {
    private final Logger logger = LoggerFactory.getLogger(ChallengeController.class);

    @PostMapping("/publish")
    public String publish() {
        logger.info("on to publish");
        return "publish";
    }
}
