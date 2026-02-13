function logMeIn() {
    document.forms[0].action = "/logmein";
    document.forms[0].submit();
}

function toOptions() {
    window.location.href = "/options";
}

function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}

function toHome() {
    window.location.href = "/";
}

function toActiveGames() {
    window.location.href = "/my-active-games";
}

function toPastGames() {
    window.location.href = "/past-games";
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
 * Update theme icon based on current theme (both auth and anon icons)
 */
function updateThemeIcon(theme) {
    var icons = ['theme-icon', 'theme-icon-anon'];
    var className = theme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    icons.forEach(function(id) {
        var el = document.getElementById(id);
        if (el) {
            el.className = className;
        }
    });
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
    var codeElements = document.querySelectorAll('.language-code, .dh-lang-code');
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
 * Initialize mobile menu toggle
 */
function initMobileMenu() {
    var toggle = document.getElementById('navMobileToggle');
    var menu = document.getElementById('navMobileMenu');
    if (!toggle || !menu) return;

    toggle.addEventListener('click', function() {
        var isOpen = menu.classList.toggle('open');
        toggle.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
    });

    // Close menu when a link is clicked
    menu.querySelectorAll('.dh-mobile-link').forEach(function(link) {
        link.addEventListener('click', function() {
            menu.classList.remove('open');
            toggle.setAttribute('aria-expanded', 'false');
        });
    });
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

    // Initialize mobile menu
    initMobileMenu();
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
