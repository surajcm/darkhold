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
}

function updateAssignment() {
    let select = document.getElementById('assignmentSelect');
    document.getElementById('assignment_method').value = select.value;
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