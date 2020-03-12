
function startGame() {
    document.forms[0].action="/game";
    document.forms[0].submit();
}

function updateTextOrEndGame(message) {
    console.log(message);
    if(message == "END_GAME" ) {
        // lets end the game
        endGame();
    }
    var div_body = document.getElementById('div_body');
    div_body.innerHTML = "";
    var para = document.createElement("p");
    var node = document.createTextNode(message);
    para.appendChild(node);
    div_body.appendChild(para);
    setTimeout(function(){ startGame(); }, 3000);
}

function connect() {
    var name = document.getElementById('name').value;
    var socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/question_fetch", {}, name);
        stompClient.subscribe('/topic/question_read', function (greeting) {
            updateTextOrEndGame(JSON.parse(greeting.body).startGame);
        });
    });
}

function endGame() {
    document.forms[0].action="/final";
    document.forms[0].submit();
}