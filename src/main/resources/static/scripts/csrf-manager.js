/**
 * CSRF Token Manager for Darkhold
 * Handles CSRF token extraction and injection for AJAX requests and WebSocket connections
 */

const CsrfManager = (() => {
    let csrfToken = null;
    let csrfHeader = null;

    /**
     * Initialize CSRF manager by reading token from cookie
     */
    function init() {
        // Read CSRF token from cookie (set by CookieCsrfTokenRepository)
        csrfToken = getCookie('XSRF-TOKEN');
        csrfHeader = 'X-XSRF-TOKEN';

        if (!csrfToken) {
            console.warn('CSRF token not found in cookies');
        }
    }

    /**
     * Get cookie value by name
     */
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) {
            return parts.pop().split(';').shift();
        }
        return null;
    }

    /**
     * Get CSRF token
     */
    function getToken() {
        if (!csrfToken) {
            init();
        }
        return csrfToken;
    }

    /**
     * Get CSRF header name
     */
    function getHeaderName() {
        return csrfHeader;
    }

    /**
     * Add CSRF token to XMLHttpRequest
     */
    function addTokenToXHR(xhr) {
        const token = getToken();
        if (token) {
            xhr.setRequestHeader(csrfHeader, token);
        }
    }

    /**
     * Add CSRF token to fetch request headers
     */
    function addTokenToFetch(headers = {}) {
        const token = getToken();
        if (token) {
            headers[csrfHeader] = token;
        }
        return headers;
    }

    /**
     * Get CSRF token as form parameter for WebSocket connections
     * STOMP requires token in CONNECT frame
     */
    function getTokenForWebSocket() {
        return {
            parameterName: '_csrf',
            token: getToken()
        };
    }

    /**
     * Get CSRF headers for STOMP connection
     * Returns object with X-CSRF-TOKEN header
     */
    function getHeadersForStomp() {
        const token = getToken();
        return token ? { 'X-CSRF-TOKEN': token } : {};
    }

    /**
     * Create hidden input field with CSRF token for forms
     */
    function createHiddenInput() {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = '_csrf';
        input.value = getToken();
        return input;
    }

    /**
     * Add CSRF token to all forms on page load
     */
    function protectForms() {
        document.querySelectorAll('form').forEach(form => {
            // Skip forms that already have CSRF token
            if (form.querySelector('input[name="_csrf"]')) {
                return;
            }

            // Add CSRF token to form
            form.appendChild(createHiddenInput());
        });
    }

    // Auto-initialize on script load
    init();

    // Expose public API
    return {
        getToken,
        getHeaderName,
        addTokenToXHR,
        addTokenToFetch,
        getTokenForWebSocket,
        getHeadersForStomp,
        createHiddenInput,
        protectForms,
        refresh: init
    };
})();

// Auto-protect forms when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        CsrfManager.protectForms();
    });
} else {
    CsrfManager.protectForms();
}
