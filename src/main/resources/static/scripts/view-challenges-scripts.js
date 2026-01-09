function selectQuiz(elem) {
    console.log('selected quiz of ' + elem.parentElement.parentElement.id);
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
    console.log('triggering deletion of quiz # ' + elem.parentElement.parentElement.id);
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
                console.log('An error occurred!');
            }
        } else {
            console.log('An error occurred!');
        }
    };
    xhr.send(formData);
}

function deleteChallengeCard(elem) {
    elem.innerHTML = "";
    document.getElementById('closeConfirmModal').click();
}

function duplicateChallenge(elem) {
    let challengeId = elem.parentElement.parentElement.id;
    console.log('duplicating challenge # ' + challengeId);
    fetch('/duplicate_challenge/' + challengeId, {method: 'POST'})
        .then(response => response.json())
        .then(data => {
            if (data.challengeId && data.challengeId > 0) {
                alert(data.message);
                location.reload();
            } else {
                alert('Error duplicating challenge');
            }
        })
        .catch(error => alert('Error: ' + error.message));
}