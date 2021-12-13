function logOut() {
    document.forms[0].method = "get";
    document.forms[0].action = "/";
    document.forms[0].submit();
}

function toChallenges() {
    document.forms[0].action = "/viewChallenge";
    document.forms[0].submit();
}

function publish() {
    let challengeId = document.getElementById('challengeId').value;
    console.log('triggering quiz of ' + challengeId);
    document.getElementById('challenge_id').value = challengeId;
    document.forms[0].action = "/publish";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}