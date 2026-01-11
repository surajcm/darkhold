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

function toActiveGames() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/my-active-games";
    document.forms[0].submit();
}

function toPastGames() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/past-games";
    document.forms[0].submit();
}