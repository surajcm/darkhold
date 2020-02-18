function uploadPDF() {

    var uploadButton = document.getElementById('btnSubmit');
    var progress = document.getElementById('progress');
    var progressdiv = document.getElementById('progressdiv');
    progress.style.display = "block";
    uploadButton.innerHTML = 'Uploading...';

    var fileSelect = document.getElementById('upload');
    var title = document.getElementById('title').value;
    var description = document.getElementById('description').value;

    var files = fileSelect.files;
    var formData = new FormData();
    var file = files[0];

    formData.append('upload', file, file.name);
    formData.append('title', title);
    formData.append('description', description);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/upload_challenge', true);
    xhr.upload.onprogress = function (e) {
        update_progress(e);
    }
    xhr.onload = function (e) {
        if (xhr.status === 200) {
            console.log(xhr.responseText);
            uploadButton.innerHTML = 'Submit';
            progressdiv.innerHTML = "<h3 style='color:green' >" + xhr.responseText + "</h3>";
            progress.style.display = "none";
        } else {
            alert('An error occurred!');
        }
    };
    xhr.send(formData);
}

function update_progress(e) {
    var uploadButton = document.getElementById('btnSubmit');
    var progress = document.getElementById('progress');
    if (e.lengthComputable) {
        var percentage = Math.round((e.loaded/e.total)*100);
        progress.value = percentage;
        uploadButton.innerHTML = 'Upload '+percentage+'%';
        console.log("percent " + percentage + '%' );
    } else {
        console.log("Unable to compute progress information since the total size is unknown");
    }
}

function toOptions() {
    document.forms[0].action="/options";
    document.forms[0].submit();
}
function logOut() {
    document.forms[0].action="/logout";
    document.forms[0].submit();
}