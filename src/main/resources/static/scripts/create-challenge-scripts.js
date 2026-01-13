function uploadPDF() {
    const uploadButton = document.getElementById('btnSubmit');
    const fileSelect = document.getElementById('upload');
    const title = document.getElementById('title').value.trim();
    const description = document.getElementById('description').value.trim();
    const resultDiv = document.getElementById('resultMessage');
    const files = fileSelect.files;

    // Validate title
    if (!title) {
        resultDiv.innerHTML = '<div class="alert alert-danger">Please enter a challenge title.</div>';
        return;
    }

    // Check if file is present
    const hasFile = files && files.length > 0;

    if (hasFile) {
        // Scenario 1: Upload Excel file with questions
        uploadWithExcel(uploadButton, files[0], title, description, resultDiv);
    } else {
        // Scenario 2: Create empty challenge without file
        createEmptyChallenge(uploadButton, title, description, resultDiv);
    }
}

function uploadWithExcel(uploadButton, file, title, description, resultDiv) {
    const progress = document.getElementById('progress');
    const progressDiv = document.getElementById('progressdiv');

    // Show progress bar and update button
    progressDiv.style.display = "block";
    uploadButton.disabled = true;
    uploadButton.innerHTML = 'Uploading...';

    const formData = new FormData();
    formData.append('upload', file, file.name);
    formData.append('title', title);
    formData.append('description', description);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/upload_challenge', true);

    // Track upload progress
    xhr.upload.onprogress = function (e) {
        if (e.lengthComputable) {
            const percentage = Math.round((e.loaded / e.total) * 100);
            progress.style.width = percentage + '%';
            progress.setAttribute('aria-valuenow', percentage);
            progress.textContent = percentage + '%';
            uploadButton.innerHTML = 'Uploading ' + percentage + '%';
        }
    };

    xhr.onload = function () {
        if (xhr.status === 200) {
            const challengeResponse = JSON.parse(xhr.responseText);
            progressDiv.style.display = "none";
            uploadButton.style.display = "none";

            // Show success message
            resultDiv.innerHTML = '<div class="alert alert-success">' + challengeResponse.message + '</div>';

            // Add "View Challenge" button
            viewChallenge(resultDiv, challengeResponse.challengeId);
        } else {
            progressDiv.style.display = "none";
            resultDiv.innerHTML = '<div class="alert alert-danger">An error occurred while uploading the file.</div>';
            uploadButton.disabled = false;
            uploadButton.innerHTML = 'Create Challenge';
        }
    };

    xhr.onerror = function () {
        progressDiv.style.display = "none";
        resultDiv.innerHTML = '<div class="alert alert-danger">Network error occurred.</div>';
        uploadButton.disabled = false;
        uploadButton.innerHTML = 'Create Challenge';
    };

    xhr.send(formData);
}

function createEmptyChallenge(uploadButton, title, description, resultDiv) {
    uploadButton.disabled = true;
    uploadButton.innerHTML = 'Creating...';

    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);

    fetch('/save_challenge', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.challengeId && data.challengeId > 0) {
            resultDiv.innerHTML = '<div class="alert alert-success">' + data.message + '</div>';
            uploadButton.style.display = 'none';

            // Add "Add Questions" button
            const editBtn = document.createElement('a');
            editBtn.href = '/edit_challenge/' + data.challengeId;
            editBtn.className = 'btn btn-success';
            editBtn.innerHTML = '<i class="fas fa-plus-circle"></i> Add Questions';
            resultDiv.appendChild(editBtn);
        } else {
            resultDiv.innerHTML = '<div class="alert alert-danger">' + data.message + '</div>';
            uploadButton.disabled = false;
            uploadButton.innerHTML = 'Create Challenge';
        }
    })
    .catch(error => {
        resultDiv.innerHTML = '<div class="alert alert-danger">An error occurred: ' + error.message + '</div>';
        uploadButton.disabled = false;
        uploadButton.innerHTML = 'Create Challenge';
    });
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
    const viewButton = document.createElement('a');
    viewButton.setAttribute("class", "btn btn-primary ms-2");
    viewButton.setAttribute("href", "/preconfigure?challenges=" + challengeId);
    viewButton.innerHTML = '<i class="fas fa-eye"></i> View Challenge';
    elem.appendChild(viewButton);
}

function goToChallengePage(challengeId) {
    console.log("ready to got to challenge page");
    document.getElementById('challenges').value = challengeId;
    document.forms[0].action = "/preconfigure";
    document.forms[0].submit();
}