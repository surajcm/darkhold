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
    //document.forms[0].action="/answer";
    //document.forms[0].submit();
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
    optionA.setAttribute('style','display:none');
    var optionB = document.getElementById("optionB");
    optionB.setAttribute('style','display:none');
    var optionC = document.getElementById("optionC");
    if (optionC != null) {
        optionC.setAttribute('style','display:none');
    }
    var optionD = document.getElementById("optionD");
    if(optionD != null) {
        optionD.setAttribute('style','display:none');
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