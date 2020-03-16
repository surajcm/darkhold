function checkScore() {
    document.forms[0].action="/check_score";
    document.forms[0].submit();
}

function hitScore() {
    var socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/fetch_scores", {}, "");
    });
}

function connect() {
    var socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/read_scores', function (greeting) {
            alert(JSON.parse(greeting.body).startGame);
        });
    });
}