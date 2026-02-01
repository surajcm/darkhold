/**
 * Language Manager - Darkhold Quiz Application
 *
 * Manages language switching and locale detection.
 * Works with Spring's LocaleChangeInterceptor using 'lang' parameter.
 */

const LanguageManager = (function() {
    'use strict';

    // Configuration
    const STORAGE_KEY = 'darkhold_language';
    const LANG_PARAM = 'lang';
    const SUPPORTED_LANGUAGES = {
        en: { code: 'en', name: 'English', nativeName: 'English' },
        es: { code: 'es', name: 'Spanish', nativeName: 'Espanol' }
    };
    const DEFAULT_LANGUAGE = 'en';

    // Current language state
    let currentLanguage = DEFAULT_LANGUAGE;

    /**
     * Initialize the language system
     * - Detects current language from URL, session, or localStorage
     * - Updates UI to reflect current language
     */
    function init() {
        // Priority: URL param > localStorage > browser preference > default
        const urlLang = getLanguageFromUrl();
        const savedLang = getSavedLanguage();
        const browserLang = getBrowserLanguage();

        if (urlLang && SUPPORTED_LANGUAGES[urlLang]) {
            currentLanguage = urlLang;
            saveLanguage(urlLang);
        } else if (savedLang && SUPPORTED_LANGUAGES[savedLang]) {
            currentLanguage = savedLang;
        } else if (browserLang && SUPPORTED_LANGUAGES[browserLang]) {
            currentLanguage = browserLang;
        } else {
            currentLanguage = DEFAULT_LANGUAGE;
        }

        console.log('[LanguageManager] Initialized with language: ' + currentLanguage);
    }

    /**
     * Get language from URL parameter
     * @returns {string|null} Language code or null
     */
    function getLanguageFromUrl() {
        var urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(LANG_PARAM);
    }

    /**
     * Get saved language from localStorage
     * @returns {string|null} The saved language or null
     */
    function getSavedLanguage() {
        try {
            return localStorage.getItem(STORAGE_KEY);
        } catch (error) {
            console.warn('[LanguageManager] localStorage not available:', error);
            return null;
        }
    }

    /**
     * Save language to localStorage
     * @param {string} lang - The language code to save
     */
    function saveLanguage(lang) {
        try {
            localStorage.setItem(STORAGE_KEY, lang);
        } catch (error) {
            console.warn('[LanguageManager] Failed to save language:', error);
        }
    }

    /**
     * Detect browser language preference
     * @returns {string|null} Browser language code (first 2 chars) or null
     */
    function getBrowserLanguage() {
        var browserLang = navigator.language || navigator.userLanguage;
        if (browserLang) {
            return browserLang.substring(0, 2).toLowerCase();
        }
        return null;
    }

    /**
     * Build URL with language parameter while preserving other params
     * @param {string} lang - The language code to set
     * @returns {string} URL with language parameter
     */
    function buildLanguageUrl(lang) {
        var url = new URL(window.location.href);
        url.searchParams.set(LANG_PARAM, lang);
        return url.toString();
    }

    /**
     * Change language and redirect to apply the change
     * @param {string} lang - The language code to switch to
     */
    function changeLanguage(lang) {
        if (!SUPPORTED_LANGUAGES[lang]) {
            console.error('[LanguageManager] Unsupported language: ' + lang);
            return;
        }

        if (lang === currentLanguage) {
            console.log('[LanguageManager] Already using language: ' + lang);
            return;
        }

        saveLanguage(lang);

        // Redirect to apply the language change via Spring's LocaleChangeInterceptor
        window.location.href = buildLanguageUrl(lang);
    }

    /**
     * Get the current language code
     * @returns {string} Current language code
     */
    function getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Get language info object
     * @param {string} lang - Language code
     * @returns {Object|null} Language info or null
     */
    function getLanguageInfo(lang) {
        return SUPPORTED_LANGUAGES[lang] || null;
    }

    /**
     * Get all supported languages
     * @returns {Object} Map of language codes to language info
     */
    function getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    /**
     * Check if a language is supported
     * @param {string} lang - Language code to check
     * @returns {boolean} True if supported
     */
    function isSupported(lang) {
        return !!SUPPORTED_LANGUAGES[lang];
    }

    // Public API
    return {
        init: init,
        changeLanguage: changeLanguage,
        getCurrentLanguage: getCurrentLanguage,
        getLanguageInfo: getLanguageInfo,
        getSupportedLanguages: getSupportedLanguages,
        isSupported: isSupported,
        buildLanguageUrl: buildLanguageUrl
    };
})();

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        LanguageManager.init();
    });
} else {
    // DOM already loaded
    LanguageManager.init();
}
