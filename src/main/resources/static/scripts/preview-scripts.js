function logOut() {
    document.forms[0].method = "get";
    document.forms[0].action = "/";
    document.forms[0].submit();
}

function toChallenges() {
    document.forms[0].action = "/viewChallenge";
    document.forms[0].submit();
}

function toHome() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/";
    document.forms[0].submit();
}

function publish() {
    let challengeId = document.getElementById('challengeId').value;
    console.log('triggering quiz of ' + challengeId);
    document.getElementById('challenge_id').value = challengeId;
    // Collect custom team names if any
    document.getElementById('team_names').value = collectTeamNames();
    document.forms[0].action = "/publish";
    document.forms[0].submit();
}

function toggleTeamConfig() {
    let checkbox = document.getElementById('teamModeCheckbox');
    let panel = document.getElementById('teamConfigPanel');
    let teamModeInput = document.getElementById('team_mode');

    if (checkbox.checked) {
        panel.style.display = 'block';
        teamModeInput.value = 'true';
    } else {
        panel.style.display = 'none';
        teamModeInput.value = 'false';
    }
}

function updateTeamCount() {
    let select = document.getElementById('teamCountSelect');
    document.getElementById('team_count').value = select.value;
    // Update visible team name rows if custom names panel is visible
    let panel = document.getElementById('customNamesPanel');
    if (panel && panel.style.display !== 'none') {
        updateVisibleTeamNameRows();
    }
}

function updateAssignment() {
    let select = document.getElementById('assignmentSelect');
    document.getElementById('assignment_method').value = select.value;
}

function toggleCustomNames() {
    let panel = document.getElementById('customNamesPanel');
    let toggle = document.getElementById('customNamesToggle');
    if (panel.style.display === 'none') {
        panel.style.display = 'block';
        toggle.textContent = '- Hide team names';
        updateVisibleTeamNameRows();
    } else {
        panel.style.display = 'none';
        toggle.textContent = '+ Customize team names';
    }
}

function updateVisibleTeamNameRows() {
    let count = parseInt(document.getElementById('teamCountSelect').value);
    let colors = ['red', 'blue', 'green', 'yellow', 'purple', 'orange'];
    colors.forEach((color, index) => {
        let row = document.getElementById('teamName_' + color + '_row');
        if (row) {
            row.style.display = index < count ? 'flex' : 'none';
        }
    });
}

function collectTeamNames() {
    let names = {};
    let colors = ['red', 'blue', 'green', 'yellow', 'purple', 'orange'];
    let count = parseInt(document.getElementById('teamCountSelect').value);
    for (let i = 0; i < count; i++) {
        let input = document.getElementById('teamName_' + colors[i]);
        if (input && input.value.trim()) {
            names[colors[i]] = input.value.trim();
        }
    }
    return JSON.stringify(names);
}

function startPractice() {
    let challengeId = document.getElementById('challengeId').value;
    console.log('starting practice mode for challenge ' + challengeId);
    document.getElementById('challenge_id').value = challengeId;
    document.forms[0].action = "/start_practice";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}