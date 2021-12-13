function createChallenge() {
    document.forms[0].action = "/createChallenge";
    document.forms[0].submit();
}

function viewChallenge() {
    document.forms[0].action = "/viewChallenge";
    document.forms[0].submit();
}

function activeChallenge() {
    document.forms[0].action = "/activeChallenge";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}

function manageUsers() {
    document.forms[0].action = "/userManagement";
    document.forms[0].submit();
}

function manageGame() {
    document.forms[0].action = "/gameManagement";
    document.forms[0].submit();
}