function getOptionsForm() {
    return document.getElementById('optionsForm') || document.forms[0];
}

function createChallenge() {
    window.location.href = "/create_challenge_form";
}

function viewChallenge() {
    window.location.href = "/view_challenges";
}

function activeChallenge() {
    window.location.href = "/activegames";
}

function logOut() {
    var form = getOptionsForm();
    form.action = "/logout";
    form.submit();
}

function toOptions() {
    window.location.href = "/options";
}

function toHome() {
    window.location.href = "/";
}

function manageUsers() {
    window.location.href = "/userManagement";
}

function manageGame() {
    window.location.href = "/gameManagement";
}

function toActiveGames() {
    window.location.href = "/my-active-games";
}

function toPastGames() {
    window.location.href = "/past-games";
}
