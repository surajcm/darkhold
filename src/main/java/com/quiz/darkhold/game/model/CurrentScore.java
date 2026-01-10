package com.quiz.darkhold.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentScore {
    private Map<String, Integer> score = new HashMap<>();
    private List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>();

    public Map<String, Integer> getScore() {
        return score;
    }

    public void setScore(final Map<String, Integer> score) {
        this.score = score;
        // Auto-generate sorted list when scores are set
        this.sortedScores = score.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();
    }

    public List<Map.Entry<String, Integer>> getSortedScores() {
        return sortedScores;
    }
}
