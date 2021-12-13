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
            uploadButton.innerHTML = 'Submit';
            progressDiv.innerHTML = "<h3 style='color:green' >" + xhr.responseText + "</h3>";
            progress.style.display = "none";
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