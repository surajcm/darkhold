package com.quiz.darkhold.analytics.controller;

import com.quiz.darkhold.analytics.entity.GameResult;
import com.quiz.darkhold.analytics.service.ResultService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for viewing past game results and analytics.
 */
@Controller
public class PastGamesController {

    private static final Logger logger = LogManager.getLogger(PastGamesController.class);
    private final ResultService resultService;

    public PastGamesController(final ResultService resultService) {
        this.resultService = resultService;
    }

    /**
     * Display past games for the current moderator.
     *
     * @param model     Spring MVC model
     * @param principal authenticated user
     * @return view name
     */
    @GetMapping("/past-games")
    public String showPastGames(final Model model, final Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String moderator = principal.getName();
        List<GameResult> pastGames = resultService.getGameResultsByModerator(moderator);

        model.addAttribute("pastGames", pastGames);
        model.addAttribute("moderator", moderator);
        return "pastgames";
    }

    /**
     * Display detailed results for a specific game.
     *
     * @param id        game result ID
     * @param model     Spring MVC model
     * @param principal authenticated user
     * @return view name
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    @GetMapping("/game-result/{id}")
    public String showGameResult(@PathVariable final Long id, final Model model, final Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        GameResult gameResult = resultService.getGameResultById(id);
        if (gameResult == null) {
            logger.warn("Game result not found: {}", id);
            return "redirect:/past-games";
        }

        // Verify the user is the moderator of this game
        if (!gameResult.getModerator().equals(principal.getName())) {
            logger.warn("User {} attempted to access game result for moderator {}",
                    principal.getName(), gameResult.getModerator());
            return "redirect:/past-games";
        }

        model.addAttribute("gameResult", gameResult);
        return "gameresult";
    }

    /**
     * Export game results to CSV.
     *
     * @param id       game result ID
     * @param response HTTP response
     * @param principal authenticated user
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    @GetMapping("/game-result/{id}/export-csv")
    @ResponseBody
    public void exportGameResultCsv(@PathVariable final Long id,
                                     final HttpServletResponse response,
                                     final Principal principal) {
        if (principal == null) {
            return;
        }

        GameResult gameResult = resultService.getGameResultById(id);
        if (gameResult == null || !gameResult.getModerator().equals(principal.getName())) {
            return;
        }

        response.setContentType("text/csv");
        String filename = "game-result-" + gameResult.getPin() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter writer = response.getWriter()) {
            writeCsvContent(writer, gameResult);
        } catch (IOException ex) {
            logger.error("Error exporting CSV", ex);
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private void writeCsvContent(final PrintWriter writer, final GameResult gameResult) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        writer.println("Game Results Export");
        writer.println();
        writer.println("PIN," + gameResult.getPin());
        writer.println("Challenge," + gameResult.getChallengeName());
        writer.println("Mode," + gameResult.getGameMode());
        writer.println("Completed," + gameResult.getCompletedAt().format(formatter));
        writer.println("Duration (minutes)," + gameResult.getDurationMinutes());
        writer.println("Total Questions," + gameResult.getTotalQuestions());
        writer.println("Participants," + gameResult.getParticipantCount());
        writer.println();

        writer.println("Participant Results");
        writer.println("Rank,Username,Score,Correct,Incorrect,Max Streak,Accuracy %");
        gameResult.getParticipantResults().forEach(pr -> {
            String line = String.format("%d,%s,%d,%d,%d,%d,%.1f",
                    pr.getFinalRank(),
                    pr.getUsername(),
                    pr.getFinalScore(),
                    pr.getCorrectAnswers(),
                    pr.getIncorrectAnswers(),
                    pr.getMaxStreak(),
                    pr.getAccuracyPercentage());
            writer.println(line);
        });

        writer.println();
        writer.println("Question Results");
        writer.println("Number,Question,Type,Correct,Incorrect,Timeout,Success Rate %,Difficulty");
        gameResult.getQuestionResults().forEach(qr -> {
            String line = String.format("%d,\"%s\",%s,%d,%d,%d,%.1f,%s",
                    qr.getQuestionNumber(),
                    qr.getQuestionText().replace("\"", "\"\""),
                    qr.getQuestionType(),
                    qr.getCorrectCount(),
                    qr.getIncorrectCount(),
                    qr.getTimeoutCount(),
                    qr.getSuccessRatePercentage(),
                    qr.getDifficultyLevel());
            writer.println(line);
        });
    }
}
