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
    //alert(document.getElementById('selectedOptions').value);
    document.forms[0].action="/timed";
    document.forms[0].submit();
}

function waitAndShowAnswer(elem) {
    var correctAnswer = document.getElementById('correctOptions').value;
    console.log('correctAnswer '+correctAnswer);
    var selectedAnswer = elem.id.charAt(elem.id.length -1);
    console.log('selectedAnswer '+selectedAnswer);
    // split with comma
    var answers = correctAnswer.split(",");
    var correct = false;
    for (i = 0; i < answers.length; i++) {
        if ( selectedAnswer == answers[i] ) {
            correct = true;
        }
    }
    if (correct) {
        alert("Selected answer is correct");
        document.getElementById('selectedOptions').value = "correct";
    } else {
        alert("Selected answer is incorrect");
        document.getElementById('selectedOptions').value = "incorrect";
    }
}