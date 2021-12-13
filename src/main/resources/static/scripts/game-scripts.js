$(document).ready(function () {
    let clock2;
    let time_to_flip = document.getElementById('time_to_flip').value;
    clock2 = new FlipClock($('.clock2'), time_to_flip, {
        clockFace: 'Counter'
    });

    let timer = new FlipClock.Timer(clock2, {
        callbacks: {
            interval: function () {
                if (clock2.getTime().time <= 0) {
                    clock2.stop();
                    timer.stop();
                    callTimeOut();
                }
                clock2.decrement()
            }
        }
    });
    timer.start();

    $("span.flip-clock-divider").remove();
});

function callTimeOut() {
    isAnswerSelected = true;
    saveAnswerOnAjax();
    let nextButton = document.getElementById('nextButton');
    if (nextButton) {
        nextButton.setAttribute('style', 'display:block');
    }
    highlightGreen();
    highlightRedOnIncorrectSelection();
    // show a bar chart with this specific questions answer statistics
}

function highlightGreen() {
    let correctAnswer = document.getElementById('correctOptions').value;
    let correctDiv = "option" + correctAnswer;
    let toGreenChild = document.getElementById(correctDiv);
    if (toGreenChild) {
        toGreenChild.children[0].setAttribute('class', 'card text-white bg-success');
    }
}

function highlightRedOnIncorrectSelection() {
    let selectedOptions = document.getElementById('selectedOptions').value;
    if (selectedOptions === "correct") {
        // nothing to do
    } else {
        let selectedAnswer = document.getElementById('selectedAnswer').value;
        if (selectedAnswer) {
            let inCorrectDiv = "option" + selectedAnswer;
            let toRedChild = document.getElementById(inCorrectDiv);
            if (toRedChild) {
                toRedChild.children[0].setAttribute('class', 'card text-white bg-danger');
            }
        }
    }
}

let isAnswerSelected = false;
let startTime;
let endTime;

function waitAndShowAnswer(elem) {
    let roles = document.getElementById("roles").value;
    console.log("roles is " + roles);
    if (!roles.includes("ROLE_MODERATOR")) {
        if (!isAnswerSelected) {
            realWaitAndShowAnswer(elem);
            if (!isAnswerSelected) {
                isAnswerSelected = true;
            }
        } else {
            console.log('answer already selected !!!');
        }
    } else {
        console.log('This is ROLE_MODERATOR who cannot play');
    }
}

function realWaitAndShowAnswer(elem) {
    let correctAnswer = document.getElementById('correctOptions').value;
    console.log('correctAnswer ' + correctAnswer);
    elem.children[0].setAttribute('class', 'card text-white bg-dark');
    let selectedAnswer = elem.id.charAt(elem.id.length - 1);
    console.log('selectedAnswer ' + selectedAnswer);
    document.getElementById('selectedAnswer').value = selectedAnswer;
    // split with comma
    let answers = correctAnswer.split(",");
    let correct = false;
    for (let i = 0; i < answers.length; i++) {
        if (selectedAnswer === answers[i]) {
            correct = true;
        }
    }
    if (correct) {
        //alert("Selected answer is correct");
        document.getElementById('selectedOptions').value = "correct";
        hideOptions();
    } else {
        //alert("Selected answer is incorrect");
        document.getElementById('selectedOptions').value = "incorrect";
        hideOptions();
    }
    endTime = new Date().getTime();
    let timeTookForFirstClick = endTime - startTime;
    console.log('Execution time: ' + timeTookForFirstClick);
}

function hideOptions() {
    let optionA = document.getElementById("optionA");
    //optionA.setAttribute('style','display:none');
    optionA.setAttribute('style', 'opacity: 0.7');
    optionA.children[0].setAttribute('style', 'background: #CCC');
    optionA.children[0].setAttribute('style', 'cursor:default');
    let optionB = document.getElementById("optionB");
    //optionB.setAttribute('style','display:none');
    optionB.setAttribute('style', 'opacity: 0.7');
    optionB.children[0].setAttribute('style', 'background: #CCC');
    optionB.children[0].setAttribute('style', 'cursor:default');
    let optionC = document.getElementById("optionC");
    if (optionC != null) {
        //optionC.setAttribute('style','display:none');
        optionC.setAttribute('style', 'opacity: 0.7');
        optionC.children[0].setAttribute('style', 'background: #CCC');
        optionC.children[0].setAttribute('style', 'cursor:default');
    }
    let optionD = document.getElementById("optionD");
    if (optionD != null) {
        //optionD.setAttribute('style','display:none');
        optionD.setAttribute('style', 'opacity: 0.7');
        optionD.children[0].setAttribute('style', 'background: #CCC');
        optionD.children[0].setAttribute('style', 'cursor:default');
    }
    let answerSpace = document.getElementById("answerSpace");
    //<div class="spinner-grow" style="width: 3rem; height: 3rem;" role="status">
    //  <span class="sr-only">Loading...</span>
    //</div>
    let spinner = document.createElement('div');
    spinner.id = 'spinner';
    spinner.class = 'spinner-grow';
    spinner.style = "width: 3rem; height: 3rem;"
    spinner.role = "status"
    let loader = document.createElement('span');
    loader.class = "sr-only";
    spinner.appendChild(loader);
    answerSpace.appendChild(spinner);

}

function saveAnswerOnAjax() {
    let selectedOptions = document.getElementById("selectedOptions").value;
    console.log("selectedOptions is " + selectedOptions);
    let timeTookForFirstClick = endTime - startTime;
    console.log('Execution time: ' + timeTookForFirstClick);
    let username = document.getElementById("user").value;
    xhr = new XMLHttpRequest();
    xhr.open('POST', "/answer/");
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function () {
        if (xhr.status === 200) {
            console.log('Response is ' + xhr.responseText);
        } else if (xhr.status !== 200) {
            console.log('Request failed.  Returned status of ' + xhr.status);
        }
    };
    xhr.send(encodeURI('selectedOptions=' + selectedOptions
        + "&user=" + username + "&timeTook=" + timeTookForFirstClick));
}

function showScoreboard() {
    //var pin = document.getElementById('quizPin').value;
    //console.log('triggering quiz with pin '+pin);
    //document.getElementById('quiz_pin').value = pin;
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/fetch_scores");
    });
    //console.log("websocket magic...")
}

function connect() {
    let socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        //stompClient.send("/app/fetch_scores");
        stompClient.subscribe('/topic/read_scores', function (status) {
            console.log(status);
            gotoScoreBoard();
        });
    });
    startTime = new Date().getTime();
    endTime = new Date().getTime();
}

function gotoScoreBoard() {
    document.forms[0].action = "/scoreboard";
    document.forms[0].submit();
}