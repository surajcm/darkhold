package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.options.service.OptionsService;
import com.quiz.darkhold.preview.service.PreviewService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OptionsController {
    private final Log log = LogFactory.getLog(OptionsController.class);
    private final OptionsService optionsService;

    private final PreviewService previewService;

    public OptionsController(final OptionsService optionsService, final PreviewService previewService) {
        this.optionsService = optionsService;
        this.previewService = previewService;
    }

    /**
     * on to create challenge page.
     *
     * @return create challenge page
     */
    @PostMapping("/createChallenge")
    public String createChallenge() {
        log.info("Into the createChallenge method");
        return "challenge/createchallenge";
    }

    /**
     * on to view challenge page, with a view of challenges.
     *
     * @param model model
     * @return view challenge page
     */
    @PostMapping("/viewChallenge")
    public String viewChallenges(final Model model,
                                 @AuthenticationPrincipal(expression = "email") final String email) {
        log.info("Into the viewChallenge method");
        log.info("email is : " +  email);
        model.addAttribute("challengeInfo", optionsService.populateChallengeInfo(email));
        return "challenge/viewchallenges";
    }

    /**
     * show the currently running challenge.
     *
     * @param model     model
     * @return publish page
     */
    @PostMapping("/activeChallenge")
    public String activeChallenges(final Model model) {
        log.info("Into the activeChallenge method");
        var publishInfo = previewService.getActiveChallenge();
        if (publishInfo != null) {
            model.addAttribute("quizPin", publishInfo.getPin());
            //todo : find a way to get all users binded
            model.addAttribute("user", publishInfo.getModerator());
            log.info("activeChallenges method, quizPin : " + publishInfo.getPin());
        }
        return "challenge/publish";
    }

    /**
     * on to create challenge page.
     *
     * @return create challenge page
     */
    @PostMapping("/userManagement")
    public String manageUsers() {
        log.info("Into the manageUsers method");
        return "user/usermanagement";
    }

    /**
     * on to create challenge page.
     *
     * @return create challenge page
     */
    @PostMapping("/gameManagement")
    public String manageGame() {
        log.info("Into the manageGame method");
        return "game/gameManagement";
    }
}
