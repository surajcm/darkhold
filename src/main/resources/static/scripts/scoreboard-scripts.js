function askQuestion() {
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/next_question");
    });
}

function connect() {
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/ready_for_question', function (greeting) {
            console.log(greeting);
            gotoQuestionsPage();
        });
    });
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