/**
 * Audio Manager - Darkhold Quiz Application
 * Milestone 10: UI/UX Polish
 *
 * Manages sound effects for game events.
 * Handles volume control and user preferences.
 */

const AudioManager = (function() {
    'use strict';

    // Configuration
    const STORAGE_ENABLED_KEY = 'darkhold_sounds_enabled';
    const STORAGE_VOLUME_KEY = 'darkhold_volume';
    const DEFAULT_VOLUME = 0.5; // 50%
    const DEFAULT_ENABLED = true;

    // Sound file paths (to be sourced from freesound.org or similar)
    const SOUNDS = {
        CORRECT: '/sounds/correct.mp3',
        INCORRECT: '/sounds/incorrect.mp3',
        TICK: '/sounds/tick.mp3',
        COUNTDOWN: '/sounds/countdown.mp3',
        VICTORY: '/sounds/victory.mp3',
        DEFEAT: '/sounds/defeat.mp3',
        JOIN: '/sounds/join.mp3'
    };

    // Audio objects pool
    let audioPool = {};
    let enabled = DEFAULT_ENABLED;
    let volume = DEFAULT_VOLUME;
    let initialized = false;

    /**
     * Initialize the audio system
     */
    function init() {
        if (initialized) {
            console.log('[AudioManager] Already initialized');
            return;
        }

        // Load preferences
        loadPreferences();

        // Preload sounds
        preloadSounds();

        initialized = true;
        console.log(`[AudioManager] Initialized - Enabled: ${enabled}, Volume: ${volume}`);
    }

    /**
     * Load preferences from localStorage
     */
    function loadPreferences() {
        try {
            const savedEnabled = localStorage.getItem(STORAGE_ENABLED_KEY);
            const savedVolume = localStorage.getItem(STORAGE_VOLUME_KEY);

            if (savedEnabled !== null) {
                enabled = savedEnabled === 'true';
            }

            if (savedVolume !== null) {
                volume = parseFloat(savedVolume);
                if (isNaN(volume) || volume < 0 || volume > 1) {
                    volume = DEFAULT_VOLUME;
                }
            }
        } catch (error) {
            console.warn('[AudioManager] Failed to load preferences:', error);
        }
    }

    /**
     * Save preferences to localStorage
     */
    function savePreferences() {
        try {
            localStorage.setItem(STORAGE_ENABLED_KEY, enabled.toString());
            localStorage.setItem(STORAGE_VOLUME_KEY, volume.toString());
        } catch (error) {
            console.warn('[AudioManager] Failed to save preferences:', error);
        }
    }

    /**
     * Preload all sound files
     */
    function preloadSounds() {
        for (const [key, path] of Object.entries(SOUNDS)) {
            try {
                const audio = new Audio(path);
                audio.preload = 'auto';
                audio.volume = volume;
                audioPool[key] = audio;
            } catch (error) {
                console.warn(`[AudioManager] Failed to preload ${key}:`, error);
            }
        }
    }

    /**
     * Play a sound effect
     * @param {string} soundKey - Key from SOUNDS object
     * @param {Object} options - Optional settings (volume, loop)
     */
    function play(soundKey, options = {}) {
        if (!enabled) {
            return;
        }

        const audio = audioPool[soundKey];
        if (!audio) {
            console.warn(`[AudioManager] Sound not found: ${soundKey}`);
            return;
        }

        try {
            // Clone audio for simultaneous plays
            const sound = audio.cloneNode();
            sound.volume = options.volume !== undefined ? options.volume : volume;
            sound.loop = options.loop || false;

            // Play sound
            const playPromise = sound.play();

            if (playPromise !== undefined) {
                playPromise
                    .then(() => {
                        console.log(`[AudioManager] Playing: ${soundKey}`);
                    })
                    .catch((error) => {
                        // Browser blocked autoplay
                        console.warn(`[AudioManager] Playback prevented for ${soundKey}:`, error.message);
                    });
            }

            return sound;
        } catch (error) {
            console.error(`[AudioManager] Error playing ${soundKey}:`, error);
        }
    }

    /**
     * Play correct answer sound
     */
    function playCorrect() {
        return play('CORRECT');
    }

    /**
     * Play incorrect answer sound
     */
    function playIncorrect() {
        return play('INCORRECT');
    }

    /**
     * Play countdown tick sound
     */
    function playTick() {
        return play('TICK', { volume: volume * 0.3 }); // Quieter tick
    }

    /**
     * Play countdown warning sound (last 5 seconds)
     */
    function playCountdown() {
        return play('COUNTDOWN');
    }

    /**
     * Play victory sound
     */
    function playVictory() {
        return play('VICTORY');
    }

    /**
     * Play defeat sound
     */
    function playDefeat() {
        return play('DEFEAT');
    }

    /**
     * Play player join sound
     */
    function playJoin() {
        return play('JOIN', { volume: volume * 0.5 });
    }

    /**
     * Toggle sound on/off
     * @returns {boolean} New enabled state
     */
    function toggle() {
        enabled = !enabled;
        savePreferences();

        // Dispatch event for UI updates
        window.dispatchEvent(new CustomEvent('soundchange', {
            detail: { enabled: enabled }
        }));

        console.log(`[AudioManager] Sounds ${enabled ? 'enabled' : 'disabled'}`);
        return enabled;
    }

    /**
     * Enable sounds
     */
    function enable() {
        if (!enabled) {
            toggle();
        }
    }

    /**
     * Disable sounds
     */
    function disable() {
        if (enabled) {
            toggle();
        }
    }

    /**
     * Set volume level
     * @param {number} newVolume - Volume level (0.0 to 1.0)
     * @returns {boolean} Success
     */
    function setVolume(newVolume) {
        if (typeof newVolume !== 'number' || newVolume < 0 || newVolume > 1) {
            console.error('[AudioManager] Invalid volume level:', newVolume);
            return false;
        }

        volume = newVolume;
        savePreferences();

        // Update all preloaded audio volumes
        for (const audio of Object.values(audioPool)) {
            audio.volume = volume;
        }

        console.log(`[AudioManager] Volume set to: ${Math.round(volume * 100)}%`);
        return true;
    }

    /**
     * Get current volume level
     * @returns {number} Volume (0.0 to 1.0)
     */
    function getVolume() {
        return volume;
    }

    /**
     * Check if sounds are enabled
     * @returns {boolean} Enabled state
     */
    function isEnabled() {
        return enabled;
    }

    /**
     * Stop all currently playing sounds
     */
    function stopAll() {
        for (const audio of Object.values(audioPool)) {
            audio.pause();
            audio.currentTime = 0;
        }
    }

    // Public API
    return {
        init: init,
        play: play,
        playCorrect: playCorrect,
        playIncorrect: playIncorrect,
        playTick: playTick,
        playCountdown: playCountdown,
        playVictory: playVictory,
        playDefeat: playDefeat,
        playJoin: playJoin,
        toggle: toggle,
        enable: enable,
        disable: disable,
        setVolume: setVolume,
        getVolume: getVolume,
        isEnabled: isEnabled,
        stopAll: stopAll,
        SOUNDS: SOUNDS
    };
})();

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        AudioManager.init();
    });
} else {
    AudioManager.init();
}
