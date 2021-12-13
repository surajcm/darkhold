function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}

function toHome() {
    document.forms[0].action = "/home";
    document.forms[0].submit();
}