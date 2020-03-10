function loading() {
    fetchQuestions();
    setTimeout(function(){ startGame(); }, 3000);
}
function startGame() {
    document.forms[0].action="/game";
    document.forms[0].submit();
}
function fetchQuestions() {
    var formData = new FormData();
    var name = document.getElementById('name').value;
    formData.append('user', name);
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/question_on_game', true);
    xhr.onload = function (e) {
        if (xhr.status === 200) {
            console.log(xhr.responseText);
            if(xhr.responseText == "END_GAME" ) {
                // lets end the game
                endGame();
            }
            var div_body = document.getElementById('div_body');
            div_body.innerHTML = "";
            var para = document.createElement("p");
            var node = document.createTextNode(xhr.responseText);
            para.appendChild(node);
            div_body.appendChild(para);
        } else {
            alert('An error occurred!');
        }
    };
    xhr.send(formData);
}

function endGame() {
    document.forms[0].action="/final";
    document.forms[0].submit();
}