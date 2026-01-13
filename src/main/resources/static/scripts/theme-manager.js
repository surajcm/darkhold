/**
 * Theme Manager - Darkhold Quiz Application
 * Milestone 10: UI/UX Polish
 *
 * Manages theme switching between light and dark modes.
 * Persists user preference in localStorage.
 */

const ThemeManager = (function() {
    'use strict';

    // Configuration
    const STORAGE_KEY = 'darkhold_theme';
    const THEMES = {
        LIGHT: 'light',
        DARK: 'dark'
    };
    const DEFAULT_THEME = THEMES.LIGHT;

    // Current theme state
    let currentTheme = DEFAULT_THEME;

    /**
     * Initialize the theme system
     * - Loads saved theme from localStorage
     * - Detects system preference if no saved theme
     * - Applies theme to document
     */
    function init() {
        // Try to load saved theme
        const savedTheme = getSavedTheme();

        if (savedTheme) {
            currentTheme = savedTheme;
        } else {
            // Check system preference
            currentTheme = getSystemPreference();
        }

        applyTheme(currentTheme);

        console.log(`[ThemeManager] Initialized with theme: ${currentTheme}`);
    }

    /**
     * Get saved theme from localStorage
     * @returns {string|null} The saved theme or null
     */
    function getSavedTheme() {
        try {
            return localStorage.getItem(STORAGE_KEY);
        } catch (error) {
            console.warn('[ThemeManager] localStorage not available:', error);
            return null;
        }
    }

    /**
     * Save theme to localStorage
     * @param {string} theme - The theme to save
     */
    function saveTheme(theme) {
        try {
            localStorage.setItem(STORAGE_KEY, theme);
        } catch (error) {
            console.warn('[ThemeManager] Failed to save theme:', error);
        }
    }

    /**
     * Detect system color scheme preference
     * @returns {string} THEMES.LIGHT or THEMES.DARK
     */
    function getSystemPreference() {
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            return THEMES.DARK;
        }
        return THEMES.LIGHT;
    }

    /**
     * Apply theme to the document
     * @param {string} theme - The theme to apply
     */
    function applyTheme(theme) {
        const html = document.documentElement;

        // Set data-theme attribute on html element
        html.setAttribute('data-theme', theme);

        // Update current theme
        currentTheme = theme;

        // Save to localStorage
        saveTheme(theme);

        // Dispatch custom event for other components to react
        window.dispatchEvent(new CustomEvent('themechange', {
            detail: { theme: theme }
        }));

        console.log(`[ThemeManager] Applied theme: ${theme}`);
    }

    /**
     * Toggle between light and dark themes
     * @returns {string} The new theme
     */
    function toggle() {
        const newTheme = currentTheme === THEMES.LIGHT ? THEMES.DARK : THEMES.LIGHT;
        applyTheme(newTheme);
        return newTheme;
    }

    /**
     * Set a specific theme
     * @param {string} theme - The theme to set (light or dark)
     * @returns {boolean} True if successful
     */
    function setTheme(theme) {
        if (theme !== THEMES.LIGHT && theme !== THEMES.DARK) {
            console.error(`[ThemeManager] Invalid theme: ${theme}`);
            return false;
        }
        applyTheme(theme);
        return true;
    }

    /**
     * Get the current theme
     * @returns {string} Current theme
     */
    function getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Check if current theme is dark
     * @returns {boolean} True if dark theme is active
     */
    function isDark() {
        return currentTheme === THEMES.DARK;
    }

    /**
     * Check if current theme is light
     * @returns {boolean} True if light theme is active
     */
    function isLight() {
        return currentTheme === THEMES.LIGHT;
    }

    /**
     * Listen to system theme changes (optional enhancement)
     */
    function watchSystemPreference() {
        if (window.matchMedia) {
            const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');

            // Modern browsers
            if (darkModeQuery.addEventListener) {
                darkModeQuery.addEventListener('change', function(e) {
                    // Only auto-switch if user hasn't manually set a preference
                    if (!getSavedTheme()) {
                        const newTheme = e.matches ? THEMES.DARK : THEMES.LIGHT;
                        applyTheme(newTheme);
                    }
                });
            }
            // Legacy browsers
            else if (darkModeQuery.addListener) {
                darkModeQuery.addListener(function(e) {
                    if (!getSavedTheme()) {
                        const newTheme = e.matches ? THEMES.DARK : THEMES.LIGHT;
                        applyTheme(newTheme);
                    }
                });
            }
        }
    }

    // Public API
    return {
        init: init,
        toggle: toggle,
        setTheme: setTheme,
        getCurrentTheme: getCurrentTheme,
        isDark: isDark,
        isLight: isLight,
        THEMES: THEMES
    };
})();

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        ThemeManager.init();
    });
} else {
    // DOM already loaded
    ThemeManager.init();
}
