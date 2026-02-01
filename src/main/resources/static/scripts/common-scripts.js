function logMeIn() {
    document.forms[0].action = "/logmein";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}

function toHome() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/";
    document.forms[0].submit();
}

function toActiveGames() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/my-active-games";
    document.forms[0].submit();
}

function toPastGames() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/past-games";
    document.forms[0].submit();
}

/**
 * Toggle theme between light and dark mode
 */
function toggleTheme() {
    if (typeof ThemeManager !== 'undefined') {
        const newTheme = ThemeManager.toggle();
        updateThemeIcon(newTheme);
    }
}

/**
 * Update theme icon based on current theme
 */
function updateThemeIcon(theme) {
    const themeIcon = document.getElementById('theme-icon');
    if (themeIcon) {
        if (theme === 'dark') {
            themeIcon.className = 'fas fa-sun'; // Show sun icon in dark mode
        } else {
            themeIcon.className = 'fas fa-moon'; // Show moon icon in light mode
        }
    }
}

/**
 * Toggle sound effects on/off
 */
function toggleSound() {
    if (typeof AudioManager !== 'undefined') {
        const enabled = AudioManager.toggle();
        updateSoundIcon(enabled);
    }
}

/**
 * Change the application language
 * @param {string} lang - Language code (en, es)
 */
function changeLanguage(lang) {
    if (typeof LanguageManager !== 'undefined') {
        LanguageManager.changeLanguage(lang);
    } else {
        // Fallback if LanguageManager is not loaded
        var url = new URL(window.location.href);
        url.searchParams.set('lang', lang);
        window.location.href = url.toString();
    }
}

/**
 * Update the language code display in the navbar
 * @param {string} lang - Current language code
 */
function updateLanguageDisplay(lang) {
    var codeElements = document.querySelectorAll('.language-code');
    var displayCode = lang ? lang.toUpperCase() : 'EN';
    codeElements.forEach(function(el) {
        el.textContent = displayCode;
    });
}

/**
 * Update sound icon based on enabled state
 */
function updateSoundIcon(enabled) {
    const soundIcon = document.getElementById('sound-icon');
    if (soundIcon) {
        soundIcon.className = enabled ? 'fas fa-volume-up' : 'fas fa-volume-mute';
    }
}

/**
 * Initialize UI components on page load
 */
function initializeUI() {
    // Update theme icon based on current theme
    if (typeof ThemeManager !== 'undefined') {
        var currentTheme = ThemeManager.getCurrentTheme();
        updateThemeIcon(currentTheme);
    }

    // Listen for theme changes
    window.addEventListener('themechange', function(e) {
        updateThemeIcon(e.detail.theme);
    });

    // Update sound icon based on current state
    if (typeof AudioManager !== 'undefined') {
        var soundEnabled = AudioManager.isEnabled();
        updateSoundIcon(soundEnabled);
        // Show sound toggle button
        var soundToggleBtn = document.getElementById('sound-toggle-btn');
        if (soundToggleBtn) {
            soundToggleBtn.style.display = '';
        }
    }

    // Update language display based on current language
    if (typeof LanguageManager !== 'undefined') {
        var currentLang = LanguageManager.getCurrentLanguage();
        updateLanguageDisplay(currentLang);
    } else {
        // Fallback: detect from URL or HTML lang attribute
        var urlParams = new URLSearchParams(window.location.search);
        var langFromUrl = urlParams.get('lang');
        var langFromHtml = document.documentElement.lang;
        var detectedLang = langFromUrl || langFromHtml || 'en';
        updateLanguageDisplay(detectedLang.substring(0, 2));
    }
}

// Initialize UI when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeUI);
} else {
    initializeUI();
}

/**
 * Register Service Worker for PWA support
 */
function registerServiceWorker() {
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', function() {
            navigator.serviceWorker.register('/service-worker.js')
                .then(function(registration) {
                    console.log('[ServiceWorker] Registered successfully:', registration.scope);
                })
                .catch(function(error) {
                    console.log('[ServiceWorker] Registration failed:', error);
                });
        });
    }
}

// Register service worker
registerServiceWorker();