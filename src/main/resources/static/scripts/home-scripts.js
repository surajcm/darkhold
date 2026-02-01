/**
 * Home page scripts for Darkhold
 * Handles PIN entry, name entry, and game joining
 */

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

function initialize_home() {
    const gamePin = document.getElementById("gamePin");
    const username = document.getElementById("username");

    if (gamePin) {
        gamePin.addEventListener('keydown', handlePinKeydown);
        // Auto-focus PIN input
        gamePin.focus();
    }

    if (username) {
        username.addEventListener('keydown', handleNameKeydown);
    }

    // Initialize alert close buttons
    initAlertCloseButtons();
}

function handlePinKeydown(e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        enterGame();
    }
}

function handleNameKeydown(e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        enterGame();
    }
}

function initAlertCloseButtons() {
    const closeButtons = document.querySelectorAll('.alert-close');
    closeButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const alert = this.closest('.alert-banner');
            if (alert) {
                alert.style.animation = 'fade-out 0.3s ease-out forwards';
                setTimeout(() => alert.remove(), 300);
            }
        });
    });
}

function enterGame() {
    const gamePinInput = document.getElementById("gamePin");
    const usernameInput = document.getElementById("username");
    const gamePin = gamePinInput.value.trim();
    const username = usernameInput.value.trim();
    const joinBtn = document.getElementById("joinBtn");

    // Clear previous errors
    clearError();

    // Validate PIN
    if (gamePin.length === 0) {
        showError("Please enter a game PIN");
        gamePinInput.focus();
        return;
    }

    // If we're in name entry step
    if (isNameStepActive()) {
        if (username.length === 0) {
            showError("Please enter your name");
            usernameInput.focus();
            return;
        }

        // Submit the form
        setLoadingState(true);
        document.forms[0].action = "/joinGame";
        document.forms[0].submit();
        return;
    }

    // Validate PIN format (should be numeric)
    if (!/^\d+$/.test(gamePin)) {
        showError("PIN should only contain numbers");
        gamePinInput.focus();
        return;
    }

    // Show loading state
    setLoadingState(true);

    // Verify PIN with server
    let xhr = new XMLHttpRequest();
    xhr.open('POST', "/enterGame", true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function() {
        setLoadingState(false);

        if (xhr.status === 200) {
            if (xhr.responseText === 'true') {
                // PIN is valid, show name entry
                showNameStep();
            } else {
                showError("Invalid PIN. Please check and try again.");
                gamePinInput.focus();
                gamePinInput.select();
            }
        } else {
            showError("Connection error. Please try again.");
            gamePinInput.focus();
        }
    };
    xhr.onerror = function() {
        setLoadingState(false);
        showError("Connection error. Please try again.");
        gamePinInput.focus();
    };
    xhr.send(encodeURI('gamePin=' + gamePin));
}

function isNameStepActive() {
    const nameStep = document.getElementById("name-step");
    return nameStep && nameStep.classList.contains('active');
}

function showNameStep() {
    const pinStep = document.getElementById("pin-step");
    const nameStep = document.getElementById("name-step");
    const usernameInput = document.getElementById("username");
    const gamePinInput = document.getElementById("gamePin");

    // Hide PIN step, show name step
    if (pinStep) {
        pinStep.classList.remove('active');
    }
    if (nameStep) {
        nameStep.classList.add('active');
    }

    // Make PIN readonly to prevent changes
    if (gamePinInput) {
        gamePinInput.readOnly = true;
    }

    // Focus name input
    if (usernameInput) {
        usernameInput.focus();
    }
}

function setLoadingState(loading) {
    const joinBtn = document.getElementById("joinBtn");
    if (joinBtn) {
        if (loading) {
            joinBtn.classList.add('loading');
            joinBtn.disabled = true;
        } else {
            joinBtn.classList.remove('loading');
            joinBtn.disabled = false;
        }
    }
}

function showError(message) {
    const messageDisp = document.getElementById("message_disp");
    if (messageDisp) {
        messageDisp.textContent = message;
        // Re-trigger animation
        messageDisp.style.animation = 'none';
        messageDisp.offsetHeight; // Trigger reflow
        messageDisp.style.animation = 'shake 0.4s ease-out';
    }
}

function clearError() {
    const messageDisp = document.getElementById("message_disp");
    if (messageDisp) {
        messageDisp.textContent = "";
    }
}

// Legacy function name support
function incorrectPin() {
    showError("Please enter a valid Game PIN!");
}

function getName() {
    showNameStep();
}
