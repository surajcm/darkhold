package com.quiz.darkhold.score.controller;

import com.quiz.darkhold.game.model.CurrentScore;
import com.quiz.darkhold.game.model.ScoreResult;
import com.quiz.darkhold.game.service.GameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ScoreController {

    private final Logger logger = LogManager.getLogger(ScoreController.class);

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public ScoreController(final GameService gameService,
                           final SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * show the till now score and display who are the top 3 players.
     *
     * @param model   model
     * @param quizPin the quiz PIN
     * @return to scoreboard
     */
    @PostMapping("/scoreboard")
    public String scoreCheck(final Model model,
                             @RequestParam(value = "quizPin", required = false) final String quizPin) {
        logger.info("On to the scoreboard screen for game: {}", quizPin);
        var scores = gameService.getCurrentScore();
        var previousScores = gameService.getPreviousScores();
        logger.info("Current score count: {}", scores.size());

        // Build enhanced score results with rank and streak info
        List<ScoreResult> scoreResults = buildScoreResults(scores, previousScores);

        // Also keep the simple score for backwards compatibility
        var score = new CurrentScore();
        score.setScore(scores);
        model.addAttribute("score", score);
        model.addAttribute("scoreResults", scoreResults);
        model.addAttribute("quizPin", quizPin);
        return "scoreboard";
    }

    private List<ScoreResult> buildScoreResults(final Map<String, Integer> scores,
                                                 final Map<String, Integer> prevScores) {
        List<ScoreResult> results = new ArrayList<>();
        Map<String, Integer> previousRanks = calculateRanks(prevScores);
        List<Map.Entry<String, Integer>> sortedEntries = sortScores(scores);
        int rank = 1;
        for (var entry : sortedEntries) {
            results.add(createScoreResult(entry, prevScores, previousRanks, sortedEntries.size(), rank++));
        }
        return results;
    }

    private ScoreResult createScoreResult(final Map.Entry<String, Integer> entry,
                                           final Map<String, Integer> prevScores,
                                           final Map<String, Integer> prevRanks,
                                           final int defaultRank, final int rank) {
        String username = entry.getKey();
        int previousScore = prevScores.getOrDefault(username, 0);
        int prevRank = prevRanks.getOrDefault(username, defaultRank);
        ScoreResult result = new ScoreResult(username, entry.getValue(), previousScore, rank, prevRank,
                gameService.getStreak(username));
        logScoreResult(result);
        return result;
    }

    private List<Map.Entry<String, Integer>> sortScores(final Map<String, Integer> scores) {
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();
    }

    private Map<String, Integer> calculateRanks(final Map<String, Integer> scores) {
        java.util.Map<String, Integer> ranks = new java.util.HashMap<>();
        List<Map.Entry<String, Integer>> sorted = sortScores(scores);
        int rank = 1;
        for (var entry : sorted) {
            ranks.put(entry.getKey(), rank++);
        }
        return ranks;
    }

    private void logScoreResult(final ScoreResult result) {
        logger.info("Score: {} -> {} (delta: {}, rank: {} from {}, streak: {})",
                result.getUsername(), result.getCurrentScore(), result.getScoreDelta(),
                result.getRank(), result.getPreviousRank(), result.getStreak());
    }

    /**
     * Signal that next question is ready.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game PIN
     */
    @MessageMapping("/next_question")
    public void scoresFetch(final String pin) {
        logger.info("On to ready_for_question for game: {}", pin);
        messagingTemplate.convertAndSend("/topic/" + pin + "/ready_for_question", true);
    }
}
