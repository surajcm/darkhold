package com.quiz.darkhold.init;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for game settings.
 * Loaded from application.properties with prefix "darkhold.game".
 */
@Configuration
@ConfigurationProperties(prefix = "darkhold.game")
public class GameConfig {

    /**
     * Timer duration in seconds for answering questions.
     * Default: 20 seconds.
     */
    private int timerSeconds = 20;

    /**
     * Length of the generated game PIN.
     * Default: 5 digits.
     */
    private int pinLength = 5;

    public int getTimerSeconds() {
        return timerSeconds;
    }

    public void setTimerSeconds(final int timerSeconds) {
        this.timerSeconds = timerSeconds;
    }

    public int getPinLength() {
        return pinLength;
    }

    public void setPinLength(final int pinLength) {
        this.pinLength = pinLength;
    }
}
