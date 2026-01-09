package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.options.service.OptionsService;
import com.quiz.darkhold.preview.service.PreviewService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class OptionsController {
    private final Log log = LogFactory.getLog(OptionsController.class);
    private final OptionsService optionsService;
    private final PreviewService previewService;
    private final GameService gameService;

    public OptionsController(final OptionsService optionsService,
                             final PreviewService previewService,
                             final GameService gameService) {
        this.optionsService = optionsService;
        this.previewService = previewService;
        this.gameService = gameService;
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
     * Show the currently running challenge with all participants.
     *
     * @param model model to populate with challenge data
     * @return publish page
     */
    @PostMapping("/activeChallenge")
    public String activeChallenges(final Model model) {
        log.info("Into the activeChallenge method");
        var publishInfo = previewService.getActiveChallenge();
        if (publishInfo != null && publishInfo.getPin() != null) {
            model.addAttribute("quizPin", publishInfo.getPin());
            model.addAttribute("user", publishInfo.getModerator());
            // Get all participants for the active game
            var participants = gameService.getAllParticipants(publishInfo.getPin());
            model.addAttribute("participants", participants != null ? participants : Collections.emptyList());
            log.info("activeChallenges method, quizPin: " + publishInfo.getPin()
                    + ", participants: " + (participants != null ? participants.size() : 0));
        }
        return "challenge/publish";
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
