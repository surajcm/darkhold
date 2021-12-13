function selectQuiz(elem) {
    console.log('triggering quiz of ' + elem.parentElement.parentElement.id);
    document.getElementById('challenges').value = elem.parentElement.parentElement.id;
    document.forms[0].action = "/preconfigure";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].method = "get";
    document.forms[0].action = "/";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}

function markQuizForDeletion(elem) {
    console.log('triggering quiz of ' + elem.parentElement.parentElement.id);
    document.getElementById('challenges').value = elem.parentElement.parentElement.id;
}

function deleteQuiz() {
    let challenge = document.getElementById('challenges').value;
    console.log('deleting quiz of ' + challenge);
    let formData = new FormData();
    formData.append('challenge', challenge);
    let xhr = new XMLHttpRequest();
    xhr.open('DELETE', '/delete_challenge', true);
    xhr.onload = function (e) {
        if (xhr.status === 200) {
            console.log(xhr.responseText);
            if (xhr.responseText === 'true') {
                let challengeRow = document.getElementById(challenge);
                deleteChallengeCard(challengeRow);
            } else {
                alert('An error occurred!');
            }
        } else {
            alert('An error occurred!');
        }
    };
    xhr.send(formData);
}

function deleteChallengeCard(elem) {
    elem.innerHTML = "";
    //$('#confirmModal').modal('hide');
    document.getElementById("confirmModal").modal('hide');
}