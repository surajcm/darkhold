function startGame() {
    var pin = document.getElementById('quizPin').value;
    console.log('triggering quiz with pin '+pin);
    document.getElementById('quiz_pin').value = pin;
    var socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/start", {}, JSON.stringify({'pin': pin}));
        stompClient.subscribe('/topic/start', function (greeting) {
            gotoMyGame();
        });
    });
}


function gotoMyGame() {
    document.forms[0].action="/interstitial";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action="/logout";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action="/options";
    document.forms[0].submit();
}

function connect() {
    var socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/user', function (greeting) {
            showGreeting(JSON.parse(greeting.body).users);
        });
    });
}
function showGreeting(message) {
    console.log(message);
    var tableRef = document.getElementById('conversation').getElementsByTagName('tbody')[0];
    tableRef.innerHTML = "";
    for (i = 0; i < message.length; i++) {
        var newRow = tableRef.insertRow(tableRef.rows.length);
        newRow.innerHTML = message[i];
    }
}