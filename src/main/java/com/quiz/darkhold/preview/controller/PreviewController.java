package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.preview.service.PreviewService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@SuppressWarnings("unused")
@Controller
public class PreviewController {
    private final Log log = LogFactory.getLog(PreviewController.class);

    @Autowired
    private PreviewService previewService;

    /**
     * on to the preview page with the selected challenge.
     *
     * @param model      model
     * @param challenges selected one
     * @return preview page
     */
    @PostMapping("/preconfigure")
    public String preconfigure(final Model model, @RequestParam("challenges") final String challenges) {
        log.info("Into the preconfigure method : " + challenges);
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
        log.info("Into publish method : " + challengeId);
        var publishInfo = previewService.generateQuizPin(challengeId, principal.getName());
        model.addAttribute("quizPin", publishInfo.getPin());
        model.addAttribute("user", publishInfo.getModerator());
        log.info("publish method, quizPin : " + publishInfo.getPin());
        return "publish";
    }
}
