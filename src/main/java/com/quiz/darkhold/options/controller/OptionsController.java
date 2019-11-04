package com.quiz.darkhold.options.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OptionsController {
    private final Log log = LogFactory.getLog(OptionsController.class);

    @PostMapping("/createChallenge")
    public String createChallenge() {
        log.info("into the createChallenge method");
        return "createchallenge";
    }

    @PostMapping("/viewChallenge")
    public String viewChallenges() {
        log.info("into the viewChallenge method");
        return "viewchallenges";
    }
}
