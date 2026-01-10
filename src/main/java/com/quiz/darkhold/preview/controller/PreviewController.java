package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.practice.service.PracticeService;
import com.quiz.darkhold.preview.service.PreviewService;
import com.quiz.darkhold.util.CommonUtils;
import jakarta.servlet.http.HttpSession;
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
    private final PracticeService practiceService;

    public PreviewController(final PreviewService previewService,
                            final PracticeService practiceService) {
        this.previewService = previewService;
        this.practiceService = practiceService;
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
        return "challenge/preview";
    }

    /**
     * publish the game.
     *
     * @param model       model
     * @param challengeId of game
     * @param principal   auth
     * @param session     HTTP session to store game PIN
     * @return publish page
     */
    @PostMapping("/publish")
    public String publish(final Model model,
                          @RequestParam("challenge_id") final String challengeId,
                          final Principal principal,
                          final HttpSession session) {
        var sanitizedChallengeId = CommonUtils.sanitizedString(challengeId);
        log.info("Into publish method : {}", sanitizedChallengeId);
        var publishInfo = previewService.generateQuizPin(challengeId, principal.getName());
        // Store PIN in session for concurrent game support (moderator)
        session.setAttribute("gamePin", publishInfo.getPin());
        log.info("Stored gamePin in session for moderator: {}", publishInfo.getPin());
        model.addAttribute("quizPin", publishInfo.getPin());
        model.addAttribute("user", publishInfo.getModerator());
        log.info("publish method, quizPin : {}", publishInfo.getPin());
        return "challenge/publish";
    }

    /**
     * Start a practice game (single-player mode).
     *
     * @param model       model
     * @param challengeId the challenge to practice
     * @param principal   authenticated user
     * @param session     HTTP session
     * @return redirect to interstitial page (skips waiting room)
     */
    @PostMapping("/start_practice")
    public String startPractice(final Model model,
                                @RequestParam("challenge_id") final String challengeId,
                                final Principal principal,
                                final HttpSession session) {
        var sanitizedChallengeId = CommonUtils.sanitizedString(challengeId);
        log.info("Starting practice mode for challenge: {} player: {}",
                sanitizedChallengeId, principal.getName());

        var publishInfo = practiceService.initializePracticeGame(
                challengeId, principal.getName(), session);

        model.addAttribute("quizPin", publishInfo.getPin());
        log.info("Practice game started with ID: {}", publishInfo.getPin());

        // Skip waiting room, go directly to interstitial
        return "interstitial";
    }
}
