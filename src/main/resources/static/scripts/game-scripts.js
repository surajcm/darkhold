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
    //alert(document.getElementById('selectedOptions').value);
    //document.forms[0].action="/answer";
    //document.forms[0].submit();
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