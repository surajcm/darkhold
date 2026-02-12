function getOptionsForm() {
    return document.getElementById('optionsForm') || document.forms[0];
}

function createChallenge() {
    window.location.href = "/create_challenge_form";
}

function viewChallenge() {
    var form = getOptionsForm();
    form.action = "/viewChallenge";
    form.submit();
}

function activeChallenge() {
    var form = getOptionsForm();
    form.action = "/activeChallenge";
    form.submit();
}

function logOut() {
    var form = getOptionsForm();
    form.action = "/logout";
    form.submit();
}

function toOptions() {
    var form = getOptionsForm();
    form.action = "/options";
    form.submit();
}

function toHome() {
    var form = getOptionsForm();
    form.method = 'get';
    form.action = "/";
    form.submit();
}

function manageUsers() {
    var form = getOptionsForm();
    form.method = 'get';
    form.action = "/userManagement";
    form.submit();
}

function manageGame() {
    var form = getOptionsForm();
    form.action = "/gameManagement";
    form.submit();
}

function toActiveGames() {
    window.location.href = "/my-active-games";
}

function toPastGames() {
    window.location.href = "/past-games";
}
