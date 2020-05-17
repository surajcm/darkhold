package com.quiz.darkhold.game.controller;

import com.quiz.darkhold.game.model.Challenge;
import com.quiz.darkhold.game.model.CurrentScore;
import com.quiz.darkhold.game.model.CurrentStatus;
import com.quiz.darkhold.game.model.ExamStatus;
import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.QuestionOnGame;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.game.model.StartTrigger;
import com.quiz.darkhold.game.model.UserResponse;
import com.quiz.darkhold.game.service.GameService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Controller
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @PostMapping("/interstitial")
    public String startInterstitial(final Model model, @RequestParam("quiz_pin") final String quizPin) {
        logger.info("On to interstitial :" + quizPin);
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
        QuestionPointer questionPointer = gameService.getCurrentQuestionPointer();
        if (questionPointer.getCurrentQuestionNumber() == questionPointer.getTotalQuestionCount()) {
            return finalScore(model);
        }
        logger.info("going to question page");
        return "question";
    }

    @PostMapping("/final")
    public String finalScore(final Model model) {
        logger.info("On to the finalScore :");
        CurrentScore score = new CurrentScore();
        Map<String, Integer> scores = gameService.getCurrentScore();
        score.setScore(scores);
        model.addAttribute("score", score);
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
        QuestionPointer questionPointer = gameService.getCurrentQuestionPointer();
        Challenge challenge = new Challenge();
        challenge.setQuestionNumber(questionPointer.getCurrentQuestionNumber());
        challenge.setQuestionSet(questionPointer.getCurrentQuestion());
        challenge.setQuestionNumber(challenge.getQuestionNumber() + 1);

        String moderator = gameService.findModerator();
        if (principal.getName().equalsIgnoreCase(moderator)) {
            gameService.incrementQuestionNo();
        }
        model.addAttribute("challenge", challenge);
        return "game";
    }

    @PostMapping("/answer/")
    public @ResponseBody
    Boolean enterGame(@ModelAttribute("selectedOptions") final String selectedOptions,
                      @ModelAttribute("user") final String user) {
        logger.info("selectedOptions is " + selectedOptions);
        logger.info("user is " + user);
        CurrentStatus currentStatus = new CurrentStatus();
        ExamStatus status = getExamStatus(selectedOptions);
        currentStatus.setStatus(status.name());
        Integer scoreOnStatus = findScoreOnStatus(status);
        gameService.saveCurrentScore(user, scoreOnStatus);
        return true;
    }

    private Integer findScoreOnStatus(final ExamStatus status) {
        return status == ExamStatus.SUCCESS ? 1000 : 0;
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
     * show the till now score and display who are the top 3 players.
     *
     * @param model model
     * @return to scoreboard
     */
    @PostMapping("/scoreboard")
    public String scoreCheck(final Model model) {
        logger.info("On to the scoreboard :");
        CurrentScore score = new CurrentScore();
        Map<String, Integer> scores = gameService.getCurrentScore();
        score.setScore(scores);
        model.addAttribute("score", score);
        return "scoreboard";
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
        logger.info("On to getGame :" + game);
        List<String> users = gameService.getAllParticipants(game.getPin());
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
        logger.info("On to startGame :" + pin);
        logger.info("On to startGame : user : " + principal.getName());
        // who started the game is already in nitrate
        return new StartTrigger(pin);
    }

    @MessageMapping("/question_fetch")
    @SendTo("/topic/question_read")
    public StartTrigger questionFetch(final String name) {
        logger.info(String.format("On to questionFetch : %s", name));
        int currentQuestionNumber = gameService.getCurrentQuestionNo();
        QuestionOnGame questionOnGame = getQuestionOnGame(currentQuestionNumber);
        if (questionOnGame == null) {
            return new StartTrigger("END_GAME");
        }
        questionOnGame.setCurrentQuestionNumber(currentQuestionNumber);
        return new StartTrigger(currentQuestionNumber + 1 + " : " + questionOnGame.getQuestion());
    }

    private QuestionOnGame getQuestionOnGame(final int currentQuestionNumber) {
        logger.info("On getQuestionOnGame : currentQuestionNumber : " + currentQuestionNumber);
        return gameService.fetchQuestion();
    }


    @MessageMapping("/fetch_scores")
    @SendTo("/topic/read_scores")
    public Boolean scoresFetch() {
        logger.info("On to scoresFetch");
        return Boolean.TRUE;
    }
}
