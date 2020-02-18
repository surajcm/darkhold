package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.options.service.OptionsService;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.service.PreviewService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class OptionsController {
    private final Log log = LogFactory.getLog(OptionsController.class);
    @Autowired
    private OptionsService optionsService;

    @Autowired
    private PreviewService previewService;

    @PostMapping("/createChallenge")
    public String createChallenge() {
        log.info("into the createChallenge method");
        return "createchallenge";
    }

    @PostMapping("/viewChallenge")
    public String viewChallenges(Model model) {
        log.info("into the viewChallenge method");
        model.addAttribute("challengeInfo", optionsService.populateChallengeInfo());
        return "viewchallenges";
    }

    @PostMapping("/activeChallenge")
    public String activeChallenges(Model model, Principal principal) {
        log.info("into the activeChallenge method");
        PublishInfo publishInfo = previewService.getActiveChallenge(principal.getName());
        model.addAttribute("quizPin", publishInfo.getPin());
        //todo : find a way to get all users binded
        model.addAttribute("user", publishInfo.getUsername());
        log.info("activeChallenges method, quizPin : " + publishInfo.getPin());
        return "publish";
    }
}
