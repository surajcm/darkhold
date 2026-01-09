function startGame() {
    let pin = document.getElementById('quizPin').value;
    console.log('triggering quiz with pin ' + pin);
    document.getElementById('quiz_pin').value = pin;
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        // Send PIN directly for PIN-scoped topic
        stompClient.send("/app/start", {}, pin);
        // Subscribe to PIN-scoped topic for concurrent game support
        stompClient.subscribe('/topic/' + pin + '/start', function (greeting) {
            gotoMyGame();
        });
    });
}

function gotoMyGame() {
    document.forms[0].action = "/interstitial";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}

function toOptions() {
    document.forms[0].action = "/options";
    document.forms[0].submit();
}

function toHome() {
    document.forms[0].method = 'get';
    document.forms[0].action = "/";
    document.forms[0].submit();
}

function connect() {
    let pin = document.getElementById('quizPin').value;
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // Subscribe to PIN-scoped topic for concurrent game support
        stompClient.subscribe('/topic/' + pin + '/user', function (greeting) {
            showGreeting(JSON.parse(greeting.body).users);
        });
    });
}

function showGreeting(message) {
    console.log(message);
    let tableRef = document.getElementById('conversation').getElementsByTagName('tbody')[0];
    tableRef.innerHTML = "";
    for (let i = 0; i < message.length; i++) {
        let newRow = tableRef.insertRow(tableRef.rows.length);
        newRow.innerHTML = message[i];
    }
}