package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.preview.service.PreviewService;
import com.quiz.darkhold.util.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@SuppressWarnings("unused")
@Controller
public class PreviewController {
    private final Logger log = LogManager.getLogger(PreviewController.class);

    private final PreviewService previewService;

    public PreviewController(final PreviewService previewService) {
        this.previewService = previewService;
    }

    /**
     * on to the preview page with the selected challenge.
     *
     * @param model      model
     * @param challenges selected one
     * @return preview page
     */
    @PostMapping("/preconfigure")
    public String preconfigure(final Model model, @RequestParam("challenges") final String challenges) {
        var sanitizedChallenges = CommonUtils.sanitizedString(challenges);
        log.info("Into the preconfigure method : {}", sanitizedChallenges);
        var previewInfo = previewService.fetchQuestions(challenges);
        model.addAttribute("previewInfo", previewInfo);
        return "preview";
    }

    /**
     * publish the game.
     *
     * @param model       model
     * @param challengeId of game
     * @param principal   auth
     * @return publish page
     */
    @PostMapping("/publish")
    public String publish(final Model model,
                          @RequestParam("challenge_id") final String challengeId,
                          final Principal principal) {
        var sanitizedChallengeId = CommonUtils.sanitizedString(challengeId);
        log.info("Into publish method : {}", sanitizedChallengeId);
        var publishInfo = previewService.generateQuizPin(challengeId, principal.getName());
        model.addAttribute("quizPin", publishInfo.getPin());
        model.addAttribute("user", publishInfo.getModerator());
        log.info("publish method, quizPin : {}", publishInfo.getPin());
        return "publish";
    }
}
