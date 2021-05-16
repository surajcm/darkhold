$(document).ready(function() {
    var clock2;
    clock2 = new FlipClock($('.clock2'), 20, {
            clockFace: 'Counter'
    });

    var timer = new FlipClock.Timer(clock2, {
        callbacks: {
            interval: function() {
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

    $( "span.flip-clock-divider" ).remove();
});

function callTimeOut() {
    isAnswerSelected = true;
    saveAnswerOnAjax();
    var nextButton = document.getElementById('nextButton');
    if (nextButton) {
        nextButton.setAttribute('style','display:block');
    }
    highlightGreen();
    highlightRedOnIncorrectSelection();
    // show a bar chart with this specific questions answer statistics
}

function highlightGreen() {
    var correctAnswer = document.getElementById('correctOptions').value;
    var correctDiv = "option" + correctAnswer;
    var toGreenChild = document.getElementById(correctDiv);
    if (toGreenChild) {
        toGreenChild.children[0].setAttribute('class','card text-white bg-success');
    }
}

function highlightRedOnIncorrectSelection() {
    if (document.getElementById('selectedOptions').value == "correct") {
        // nothing to do
    } else {
        var selectedAnswer = document.getElementById('selectedAnswer').value;
        if (selectedAnswer) {
            var inCorrectDiv = "option" + selectedAnswer;
            var toRedChild = document.getElementById(inCorrectDiv);
            if (toRedChild) {
                toRedChild.children[0].setAttribute('class','card text-white bg-danger');
            }
        }
    }
}

var isAnswerSelected = false;

function waitAndShowAnswer(elem) {
    if (!isAnswerSelected) {
        realWaitAndShowAnswer(elem);
        if (!isAnswerSelected) {
            isAnswerSelected = true;
        }
    } else {
        console.log('answer already selected !!!');
    }
}

function realWaitAndShowAnswer(elem) {
    var correctAnswer = document.getElementById('correctOptions').value;
    console.log('correctAnswer '+correctAnswer);
    elem.children[0].setAttribute('class','card text-white bg-dark');
    var selectedAnswer = elem.id.charAt(elem.id.length -1);
    console.log('selectedAnswer '+selectedAnswer);
    document.getElementById('selectedAnswer').value = selectedAnswer;
    // split with comma
    var answers = correctAnswer.split(",");
    var correct = false;
    for (i = 0; i < answers.length; i++) {
        if ( selectedAnswer == answers[i] ) {
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
}

function hideOptions() {
    var optionA = document.getElementById("optionA");
    //optionA.setAttribute('style','display:none');
    optionA.setAttribute('style','opacity: 0.7');
    optionA.children[0].setAttribute('style','background: #CCC');
    optionA.children[0].setAttribute('style','cursor:default');
    var optionB = document.getElementById("optionB");
    //optionB.setAttribute('style','display:none');
    optionB.setAttribute('style','opacity: 0.7');
    optionB.children[0].setAttribute('style','background: #CCC');
    optionB.children[0].setAttribute('style','cursor:default');
    var optionC = document.getElementById("optionC");
    if (optionC != null) {
        //optionC.setAttribute('style','display:none');
        optionC.setAttribute('style','opacity: 0.7');
        optionC.children[0].setAttribute('style','background: #CCC');
        optionC.children[0].setAttribute('style','cursor:default');
    }
    var optionD = document.getElementById("optionD");
    if(optionD != null) {
        //optionD.setAttribute('style','display:none');
        optionD.setAttribute('style','opacity: 0.7');
        optionD.children[0].setAttribute('style','background: #CCC');
        optionD.children[0].setAttribute('style','cursor:default');
    }
    var answerSpace = document.getElementById("answerSpace");
    //<div class="spinner-grow" style="width: 3rem; height: 3rem;" role="status">
    //  <span class="sr-only">Loading...</span>
    //</div>
    var spinner = document.createElement('div');
    spinner.id = 'spinner';
    spinner.class = 'spinner-grow';
    spinner.style="width: 3rem; height: 3rem;"
    spinner.role="status"
    var loader = document.createElement('span');
    loader.class ="sr-only";
    spinner.appendChild(loader);
    answerSpace.appendChild(spinner);

}

function saveAnswerOnAjax() {
    var selectedOptions = document.getElementById("selectedOptions").value;
    console.log("selectedOptions is "+selectedOptions);
    var username = document.getElementById("user").value;
    xhr = new XMLHttpRequest();
    xhr.open('POST', "/answer/");
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function() {
        if (xhr.status === 200) {
            console.log('Response is ' + xhr.responseText);
        } else if (xhr.status !== 200) {
            console.log('Request failed.  Returned status of ' + xhr.status);
        }
    };
    xhr.send(encodeURI('selectedOptions=' + selectedOptions + "&user=" + username));
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
    var socket = new SockJS('/darkhold-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        //stompClient.send("/app/fetch_scores");
        stompClient.subscribe('/topic/read_scores', function (status) {
            console.log(status);
            gotoScoreBoard();
        });
    });
}

function gotoScoreBoard() {
    document.forms[0].action="/scoreboard";
    document.forms[0].submit();
}