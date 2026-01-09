function askQuestion() {
    let pin = document.getElementById('quizPin').value;
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        // Send PIN for PIN-scoped topic
        stompClient.send("/app/next_question", {}, pin);
    });
}

function connect() {
    let pin = document.getElementById('quizPin').value;
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        // Subscribe to PIN-scoped topics for concurrent game support
        stompClient.subscribe('/topic/' + pin + '/ready_for_question', function (greeting) {
            console.log(greeting);
            gotoQuestionsPage();
        });
        // Subscribe to skip and end game events (PIN-scoped)
        stompClient.subscribe('/topic/' + pin + '/question_skipped', function (status) {
            console.log('Question skipped');
            gotoQuestionsPage();
        });
        stompClient.subscribe('/topic/' + pin + '/game_ended', function (status) {
            console.log('Game ended');
            gotoFinalScore();
        });
    });
}

function skipQuestion() {
    if (confirm('Skip this question and move to the next one?')) {
        let pin = document.getElementById('quizPin').value;
        stompClient.send("/app/skip_question", {}, pin);
    }
}

function endGameEarly() {
    if (confirm('Are you sure you want to end the game early? This will show the final scores.')) {
        let pin = document.getElementById('quizPin').value;
        stompClient.send("/app/end_game_early", {}, pin);
    }
}

function gotoFinalScore() {
    document.forms[0].action = "/final";
    document.forms[0].submit();
}

function gotoQuestionsPage() {
    document.forms[0].action = "/question";
    document.forms[0].submit();
}

function logOut() {
    document.forms[0].action = "/logout";
    document.forms[0].submit();
}

function toHome() {
    document.forms[0].action = "/home";
    document.forms[0].submit();
}