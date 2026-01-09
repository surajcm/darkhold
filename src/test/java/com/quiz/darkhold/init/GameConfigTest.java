package com.quiz.darkhold.init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("GameConfig Tests")
class GameConfigTest {

    @Test
    @DisplayName("Should have default timer of 20 seconds")
    void shouldHaveDefaultTimerSeconds() {
        var config = new GameConfig();
        assertEquals(20, config.getTimerSeconds());
    }

    @Test
    @DisplayName("Should have default PIN length of 5")
    void shouldHaveDefaultPinLength() {
        var config = new GameConfig();
        assertEquals(5, config.getPinLength());
    }

    @Test
    @DisplayName("Should allow setting timer seconds")
    void shouldAllowSettingTimerSeconds() {
        var config = new GameConfig();
        config.setTimerSeconds(30);
        assertEquals(30, config.getTimerSeconds());
    }

    @Test
    @DisplayName("Should allow setting PIN length")
    void shouldAllowSettingPinLength() {
        var config = new GameConfig();
        config.setPinLength(6);
        assertEquals(6, config.getPinLength());
    }

    @Test
    @DisplayName("Should handle custom timer value")
    void shouldHandleCustomTimerValue() {
        var config = new GameConfig();
        config.setTimerSeconds(60);
        assertEquals(60, config.getTimerSeconds());
    }

    @Test
    @DisplayName("Should handle custom PIN length")
    void shouldHandleCustomPinLength() {
        var config = new GameConfig();
        config.setPinLength(8);
        assertEquals(8, config.getPinLength());
    }
}
