function toOptions() {
    document.forms[0].action="/options";
    document.forms[0].submit();
}

function connect() {
    var name_val = document.getElementById('name').value;
    var pin_val = document.getElementById('quizPin').value;
    var socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/user", {}, JSON.stringify({'name': name_val, 'pin': pin_val}));
        //listen...
        stompClient.subscribe('/topic/user', function (greeting) {
            showGreeting(JSON.parse(greeting.body).users);
        });

        stompClient.subscribe('/topic/start', function (greeting) {
            gotoMyGame();
        });
    });
}

function showGreeting(message) {
    console.log(message);
    var new_tbody = document.createElement('tbody');
    var tableRef = document.getElementById('conversation').getElementsByTagName('tbody')[0];
    tableRef.innerHTML = "";
    for (i = 0; i < message.length; i++) {
        var newRow = tableRef.insertRow(tableRef.rows.length);
        newRow.innerHTML = message[i];
    }
}

function gotoMyGame() {
    document.forms[0].action="/interstitial";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action="/logout";
    document.forms[0].submit();
}