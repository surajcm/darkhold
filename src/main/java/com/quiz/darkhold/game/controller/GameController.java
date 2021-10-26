package com.quiz.darkhold.game.controller;

import com.quiz.darkhold.game.model.Challenge;
import com.quiz.darkhold.game.model.CurrentScore;
import com.quiz.darkhold.game.model.ExamStatus;
import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.StartTrigger;
import com.quiz.darkhold.game.model.UserResponse;
import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@SuppressWarnings("unused")
@Controller
public class GameController {
    private final Logger logger = LogManager.getLogger(GameController.class);

    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/interstitial")
    public String startInterstitial(final Model model, @RequestParam("quiz_pin") final String quizPin) {
        var sanitizedPin = CommonUtils.sanitizedString(quizPin);
        logger.info("On to interstitial : {}", sanitizedPin);
        return "interstitial";
    }

    /**
     * On to the page where the only question is getting displayed for few seconds.
     *
     * @param model     model
     * @param principal auth
     * @return question page
     */
    @PostMapping("/question")
    public String question(final Model model, final Principal principal) {
        logger.info("On to question :");
        var questionPointer = gameService.getCurrentQuestionPointer();
        if (questionPointer.getCurrentQuestionNumber() == questionPointer.getTotalQuestionCount()) {
            return finalScore(model);
        }
        logger.info("Going to question page");
        return "question";
    }

    @PostMapping("/final")
    public String finalScore(final Model model) {
        logger.info("On to the finalScore :");
        var score = new CurrentScore();
        var scores = gameService.getCurrentScore();
        score.setScore(scores);
        model.addAttribute("score", score);
        gameService.cleanUpCurrentGame();
        return "finalscore";
    }

    /**
     * On to the game.
     *
     * @param model     model
     * @param principal auth
     * @return game page
     */
    @PostMapping("/game")
    public String startGame(final Model model, final Principal principal) {
        logger.info("On to game :");
        var questionPointer = gameService.getCurrentQuestionPointer();
        var challenge = new Challenge();
        challenge.setQuestionNumber(questionPointer.getCurrentQuestionNumber());
        challenge.setQuestionSet(questionPointer.getCurrentQuestion());
        challenge.setQuestionNumber(challenge.getQuestionNumber() + 1);
        model.addAttribute("challenge", challenge);
        model.addAttribute("game_timer", "25");
        return "game";
    }

    @PostMapping("/answer/")
    public @ResponseBody
    Boolean enterGame(@ModelAttribute("selectedOptions") final String selectedOptions,
                      @ModelAttribute("user") final String user,
                      @ModelAttribute("timeTook") final String timeTook) {
        logParams(selectedOptions, user, timeTook);
        var status = getExamStatus(selectedOptions);
        var scoreOnStatus = findScoreOnStatus(status, timeTook);
        logger.info("score is {}", scoreOnStatus);
        var moderator = gameService.findModerator();
        if (user.equalsIgnoreCase(moderator)) {
            gameService.incrementQuestionNo();
        } else {
            // save score only if it is not a moderator, moderator scores are not saved
            gameService.saveCurrentScore(user, scoreOnStatus);
        }
        return true;
    }

    private void logParams(final String selectedOptions, final String user, final String timeTook) {
        var sanitizedOptions = CommonUtils.sanitizedString(selectedOptions);
        var sanitizedUser = CommonUtils.sanitizedString(user);
        var sanitizedTime = CommonUtils.sanitizedString(timeTook);
        logger.info("selectedOptions are {}, user is {}, and timeTook is {}",
                sanitizedOptions, sanitizedUser, sanitizedTime);
    }

    private int findScoreOnStatus(final ExamStatus status, final String timeTook) {
        int timeForAnswer = 0;
        if (status == ExamStatus.SUCCESS) {
            try {
                var inTwentySeconds = Integer.parseInt(timeTook);
                if (inTwentySeconds > 0L) {
                    timeForAnswer = (20_000 - inTwentySeconds) / 20;
                }
            } catch (NumberFormatException exception) {
                logger.info(exception.getMessage());
            }
        }
        return timeForAnswer;
    }

    private ExamStatus getExamStatus(@RequestParam("selectedOptions") final String selectedOptions) {
        ExamStatus status;
        if (StringUtils.isNotEmpty(selectedOptions)) {
            if (selectedOptions.equalsIgnoreCase("correct")) {
                status = ExamStatus.SUCCESS;
            } else if (selectedOptions.equalsIgnoreCase("incorrect")) {
                status = ExamStatus.FAILURE;
            } else {
                status = ExamStatus.TIME_OUT;
            }
        } else {
            status = ExamStatus.TIME_OUT;
        }
        return status;
    }


    /**
     * Always active, will add the new user and give it back in response.
     *
     * @param game game
     * @return ajax
     */
    @MessageMapping("/user")
    @SendTo("/topic/user")
    public UserResponse getGame(final Game game) {
        logger.info("On to getGame : {}", game);
        var users = gameService.getAllParticipants(game.getPin());
        return new UserResponse(users);
    }

    /**
     * Trigger for starting the game.
     *
     * @param pin       of the game
     * @param principal of who started it
     * @return to the game page
     */
    @MessageMapping("/start")
    @SendTo("/topic/start")
    public StartTrigger startTrigger(final String pin, final Principal principal) {
        // this is triggered by the game moderator
        logger.info("On to startGame : {}", pin);
        logger.info("On to startGame : user : {}", principal.getName());
        // who started the game is already in nitrate
        return new StartTrigger(pin);
    }

    @MessageMapping("/question_fetch")
    @SendTo("/topic/question_read")
    public StartTrigger questionFetch(final String name) {
        logger.info("On to questionFetch : {}}", name);
        var questionPointer = gameService.getCurrentQuestionPointer();
        if (questionPointer.getCurrentQuestionNumber() == questionPointer.getTotalQuestionCount()) {
            return new StartTrigger("END_GAME");
        }
        logger.info("On questionPointer.getCurrentQuestionNumber() : {}",
                questionPointer.getCurrentQuestionNumber());
        return new StartTrigger(questionPointer.getCurrentQuestionNumber() + 1 + " : "
                + questionPointer.getCurrentQuestion().getQuestion());
    }

    @MessageMapping("/fetch_scores")
    @SendTo("/topic/read_scores")
    public Boolean scoresFetch() {
        logger.info("On to scoresFetch");
        return Boolean.TRUE;
    }
}