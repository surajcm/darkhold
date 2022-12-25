function uploadPDF() {
    let uploadButton = document.getElementById('btnSubmit');
    let progress = document.getElementById('progress');
    let progressDiv = document.getElementById('progressdiv');
    progress.style.display = "block";
    uploadButton.innerHTML = 'Uploading...';

    const fileSelect = document.getElementById('upload');
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;

    let files = fileSelect.files;
    let formData = new FormData();
    let file = files[0];

    formData.append('upload', file, file.name);
    formData.append('title', title);
    formData.append('description', description);

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/upload_challenge', true);
    xhr.upload.onprogress = function (e) {
        update_progress(e);
    }
    xhr.onload = function (e) {
        if (xhr.status === 200) {
            console.log(xhr.responseText);
            let challengeResponse = JSON.parse(xhr.responseText);
            uploadButton.innerHTML = 'Submit';
            progressDiv.innerHTML = "<h3 style='color:green' >" + challengeResponse.message + "</h3>";
            progress.style.display = "none";
            uploadButton.style.display = "none";
            viewChallenge(uploadButton.parentElement, challengeResponse.challengeId);
        } else {
            alert('An error occurred!');
        }
    };
    xhr.send(formData);
}

function update_progress(e) {
    let uploadButton = document.getElementById('btnSubmit');
    let progress = document.getElementById('progress');
    if (e.lengthComputable) {
        let percentage = Math.round((e.loaded / e.total) * 100);
        progress.value = percentage;
        uploadButton.innerHTML = 'Upload ' + percentage + '%';
        console.log("percent " + percentage + '%');
    } else {
        console.log("Unable to compute progress information since the total size is unknown");
    }
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

function viewChallenge(elem, challengeId) {
    let viewButton = document.createElement('input');
    viewButton.setAttribute("class", "btn btn-primary");
    viewButton.setAttribute("value", "View Challenge");
    viewButton.setAttribute("type", "button");
    viewButton.setAttribute("id", "btnView");
    viewButton.onclick = function () {
        goToChallengePage(challengeId);
    };
    elem.appendChild(viewButton);
}

function goToChallengePage(challengeId) {
    console.log("ready to got to challenge page");
    document.getElementById('challenges').value = challengeId;
    document.forms[0].action = "/preconfigure";
    document.forms[0].submit();
}