package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.options.service.OptionsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OptionsController {
    private final Log log = LogFactory.getLog(OptionsController.class);
    @Autowired
    private OptionsService mockChallengeInfo;

    @PostMapping("/createChallenge")
    public String createChallenge() {
        log.info("into the createChallenge method");
        return "createchallenge";
    }

    @PostMapping("/viewChallenge")
    public String viewChallenges(Model model) {
        log.info("into the viewChallenge method");
        model.addAttribute("challengeInfo", mockChallengeInfo.populateChallengeInfo());
        return "viewchallenges";
    }
}
