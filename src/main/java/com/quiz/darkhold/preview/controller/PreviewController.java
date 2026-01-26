package com.quiz.darkhold.preview.controller;

import com.quiz.darkhold.practice.service.PracticeService;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.service.PreviewService;
import com.quiz.darkhold.team.model.TeamAssignmentMethod;
import com.quiz.darkhold.team.model.TeamConfig;
import com.quiz.darkhold.team.service.TeamService;
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
    private final TeamService teamService;

    public PreviewController(final PreviewService previewService,
                            final PracticeService practiceService,
                            final TeamService teamService) {
        this.previewService = previewService;
        this.practiceService = practiceService;
        this.teamService = teamService;
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
     * @param model            model
     * @param challengeId      of game
     * @param teamMode         whether team mode is enabled
     * @param teamCount        number of teams (2-6)
     * @param assignmentMethod team assignment method
     * @param principal        auth
     * @param session          HTTP session to store game PIN
     * @return publish page
     */
    @PostMapping("/publish")
    public String publish(final Model model,
                          @RequestParam("challenge_id") final String challengeId,
                          @RequestParam(value = "team_mode", defaultValue = "false") final boolean teamMode,
                          @RequestParam(value = "team_count", defaultValue = "2") final int teamCount,
                          @RequestParam(value = "assignment_method", defaultValue = "BALANCED")
                              final String assignmentMethod,
                          final Principal principal,
                          final HttpSession session) {
        log.info("Into publish method : {} teamMode: {}", CommonUtils.sanitizedString(challengeId), teamMode);
        var publishInfo = previewService.generateQuizPin(challengeId, principal.getName(), teamMode);
        storeSessionAttributes(session, publishInfo.getPin(), teamMode);
        createTeamsIfEnabled(teamMode, teamCount, assignmentMethod, publishInfo.getPin());
        addPublishAttributes(model, publishInfo, teamMode, teamCount, assignmentMethod);
        return "challenge/publish";
    }

    private void storeSessionAttributes(final HttpSession session, final String pin, final boolean teamMode) {
        session.setAttribute("gamePin", pin);
        session.setAttribute("teamMode", teamMode);
        log.info("Stored gamePin in session for moderator: {}", pin);
    }

    private void createTeamsIfEnabled(final boolean teamMode, final int teamCount,
                                       final String assignmentMethod, final String pin) {
        if (teamMode) {
            TeamConfig teamConfig = new TeamConfig();
            teamConfig.setTeamCount(teamCount);
            teamConfig.setAssignmentMethod(TeamAssignmentMethod.valueOf(assignmentMethod));
            teamService.createTeams(pin, teamConfig);
            log.info("Created {} teams for game {}", teamCount, pin);
        }
    }

    private void addPublishAttributes(final Model model, final PublishInfo publishInfo,
                                       final boolean teamMode, final int teamCount, final String method) {
        model.addAttribute("quizPin", publishInfo.getPin());
        model.addAttribute("user", publishInfo.getModerator());
        model.addAttribute("teamMode", teamMode);
        model.addAttribute("teamCount", teamCount);
        model.addAttribute("assignmentMethod", method);
        log.info("publish method, quizPin : {}", publishInfo.getPin());
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
