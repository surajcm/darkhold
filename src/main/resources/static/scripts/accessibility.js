/**
 * Accessibility Utilities - Darkhold Quiz Application
 * Milestone 10: UI/UX Polish
 *
 * Provides keyboard navigation, screen reader support, and focus management.
 */

const A11y = (function() {
    'use strict';

    // Configuration
    let initialized = false;
    let liveRegion = null;

    /**
     * Initialize accessibility features
     */
    function init() {
        if (initialized) {
            return;
        }

        createLiveRegion();
        setupKeyboardNavigation();
        setupFocusManagement();
        setupSkipLinks();

        initialized = true;
        console.log('[A11y] Accessibility utilities initialized');
    }

    /**
     * Create ARIA live region for announcements
     */
    function createLiveRegion() {
        liveRegion = document.createElement('div');
        liveRegion.id = 'a11y-live-region';
        liveRegion.setAttribute('aria-live', 'polite');
        liveRegion.setAttribute('aria-atomic', 'true');
        liveRegion.setAttribute('class', 'sr-only');
        liveRegion.style.cssText = 'position: absolute; left: -10000px; width: 1px; height: 1px; overflow: hidden;';
        document.body.appendChild(liveRegion);
    }

    /**
     * Announce a message to screen readers
     * @param {string} message - Message to announce
     * @param {string} priority - 'polite' or 'assertive'
     */
    function announce(message, priority = 'polite') {
        if (!liveRegion) {
            createLiveRegion();
        }

        liveRegion.setAttribute('aria-live', priority);

        // Clear and re-announce to ensure it's read
        liveRegion.textContent = '';
        setTimeout(() => {
            liveRegion.textContent = message;
            console.log(`[A11y] Announced: ${message}`);
        }, 100);
    }

    /**
     * Setup keyboard navigation for interactive elements
     */
    function setupKeyboardNavigation() {
        // ESC key - Close modals
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' || e.keyCode === 27) {
                closeModals();
            }
        });

        // Add keyboard support to onclick divs
        document.addEventListener('DOMContentLoaded', function() {
            const clickableDivs = document.querySelectorAll('[onclick]:not(button):not(a)');
            clickableDivs.forEach(function(element) {
                // Make keyboard focusable
                if (!element.hasAttribute('tabindex')) {
                    element.setAttribute('tabindex', '0');
                }

                // Add keyboard event handlers
                element.addEventListener('keydown', function(e) {
                    if (e.key === 'Enter' || e.key === ' ' || e.keyCode === 13 || e.keyCode === 32) {
                        e.preventDefault();
                        element.click();
                    }
                });
            });
        });
    }

    /**
     * Close all open modals
     */
    function closeModals() {
        // Bootstrap 5 modals
        const modals = document.querySelectorAll('.modal.show');
        modals.forEach(function(modal) {
            const bsModal = bootstrap.Modal.getInstance(modal);
            if (bsModal) {
                bsModal.hide();
            }
        });
    }

    /**
     * Setup focus management for modals and dialogs
     */
    function setupFocusManagement() {
        document.addEventListener('shown.bs.modal', function(e) {
            const modal = e.target;
            const focusableElements = modal.querySelectorAll(
                'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
            );

            if (focusableElements.length > 0) {
                focusableElements[0].focus();
            }
        });
    }

    /**
     * Setup skip navigation links
     */
    function setupSkipLinks() {
        document.addEventListener('DOMContentLoaded', function() {
            // Check if skip link already exists
            if (document.getElementById('skip-to-main')) {
                return;
            }

            // Create skip link
            const skipLink = document.createElement('a');
            skipLink.id = 'skip-to-main';
            skipLink.href = '#main-content';
            skipLink.textContent = 'Skip to main content';
            skipLink.className = 'sr-only sr-only-focusable';
            skipLink.style.cssText = `
                position: absolute;
                top: -40px;
                left: 0;
                background: var(--color-primary);
                color: var(--color-text-inverse);
                padding: 8px 16px;
                z-index: 10000;
                text-decoration: none;
            `;

            // Show on focus
            skipLink.addEventListener('focus', function() {
                this.style.top = '0';
            });

            skipLink.addEventListener('blur', function() {
                this.style.top = '-40px';
            });

            // Handle click
            skipLink.addEventListener('click', function(e) {
                e.preventDefault();
                const main = document.querySelector('main, [role="main"], #main-content');
                if (main) {
                    main.setAttribute('tabindex', '-1');
                    main.focus();
                }
            });

            document.body.insertBefore(skipLink, document.body.firstChild);
        });
    }

    /**
     * Add ARIA label to element
     * @param {HTMLElement|string} element - Element or selector
     * @param {string} label - Aria label text
     */
    function addLabel(element, label) {
        const el = typeof element === 'string' ? document.querySelector(element) : element;
        if (el) {
            el.setAttribute('aria-label', label);
        }
    }

    /**
     * Add ARIA described by
     * @param {HTMLElement|string} element - Element or selector
     * @param {string} describedById - ID of describing element
     */
    function addDescribedBy(element, describedById) {
        const el = typeof element === 'string' ? document.querySelector(element) : element;
        if (el) {
            el.setAttribute('aria-describedby', describedById);
        }
    }

    /**
     * Mark element as busy/loading
     * @param {HTMLElement|string} element - Element or selector
     * @param {boolean} busy - Busy state
     */
    function setBusy(element, busy) {
        const el = typeof element === 'string' ? document.querySelector(element) : element;
        if (el) {
            el.setAttribute('aria-busy', busy.toString());
        }
    }

    /**
     * Set element's expanded state (for accordions, dropdowns)
     * @param {HTMLElement|string} element - Element or selector
     * @param {boolean} expanded - Expanded state
     */
    function setExpanded(element, expanded) {
        const el = typeof element === 'string' ? document.querySelector(element) : element;
        if (el) {
            el.setAttribute('aria-expanded', expanded.toString());
        }
    }

    /**
     * Make element screen reader only
     * @param {HTMLElement|string} element - Element or selector
     */
    function makeSrOnly(element) {
        const el = typeof element === 'string' ? document.querySelector(element) : element;
        if (el) {
            el.classList.add('sr-only');
        }
    }

    /**
     * Setup answer navigation with arrow keys
     * @param {string} containerSelector - Container with answer options
     */
    function setupAnswerNavigation(containerSelector) {
        const container = document.querySelector(containerSelector);
        if (!container) return;

        const answers = container.querySelectorAll('[data-answer], .answer-option, .option');
        if (answers.length === 0) return;

        let currentIndex = 0;

        // Make answers keyboard focusable
        answers.forEach((answer, index) => {
            answer.setAttribute('tabindex', index === 0 ? '0' : '-1');
            answer.setAttribute('role', 'button');
        });

        // Arrow key navigation
        container.addEventListener('keydown', function(e) {
            if (!['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(e.key)) {
                return;
            }

            e.preventDefault();

            // Update index based on key
            if (e.key === 'ArrowDown' || e.key === 'ArrowRight') {
                currentIndex = (currentIndex + 1) % answers.length;
            } else {
                currentIndex = (currentIndex - 1 + answers.length) % answers.length;
            }

            // Update tabindex and focus
            answers.forEach((answer, index) => {
                answer.setAttribute('tabindex', index === currentIndex ? '0' : '-1');
            });

            answers[currentIndex].focus();
        });

        // Enter or Space to select
        answers.forEach(function(answer) {
            answer.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    answer.click();
                }
            });
        });
    }

    // Public API
    return {
        init: init,
        announce: announce,
        addLabel: addLabel,
        addDescribedBy: addDescribedBy,
        setBusy: setBusy,
        setExpanded: setExpanded,
        makeSrOnly: makeSrOnly,
        setupAnswerNavigation: setupAnswerNavigation,
        closeModals: closeModals
    };
})();

// Auto-initialize
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        A11y.init();
    });
} else {
    A11y.init();
}
