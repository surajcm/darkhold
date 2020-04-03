package com.quiz.darkhold.game.model;

import java.util.HashMap;
import java.util.Map;

public class CurrentScore {
    private Map<String, Integer> score = new HashMap<>();

    public Map<String, Integer> getScore() {
        return score;
    }

    public void setScore(final Map<String, Integer> score) {
        this.score = score;
    }
}
