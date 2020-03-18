package com.quiz.darkhold.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.game.model.Challenge;
import com.quiz.darkhold.game.model.CurrentStatus;
import com.quiz.darkhold.game.model.ExamStatus;
import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.QuestionOnGame;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "question";
    }

    @PostMapping("/final")
    public String finalScore(final Model model) {
        logger.info("On to the finalScore :");
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
        int currentQuestionNumber = gameService.getCurrentQuestionNo();
        String moderator = gameService.findModerator();
        if (currentQuestionNumber < 0 || principal.getName().equalsIgnoreCase(moderator)) {
            currentQuestionNumber++;
        }
        Challenge challenge = gameService.getCurrentQuestionSet(currentQuestionNumber);
        challenge.setQuestionNumber(challenge.getQuestionNumber() + 1);
        if (principal.getName().equalsIgnoreCase(moderator)) {
            gameService.updateQuestionNo();
        }
        model.addAttribute("challenge", challenge);
        return "game";
    }

    /**
     * Answer the question with correct/incorrect options or time out.
     *
     * @param model           model
     * @param selectedOptions answer
     * @return to the answer show page
     */
    @PostMapping("/answer")
    public String timed(final Model model, @RequestParam("selectedOptions") final String selectedOptions,
                        final Principal principal) {
        logger.info("On to the answer :" + selectedOptions);
        CurrentStatus currentStatus = new CurrentStatus();
        ExamStatus status = getExamStatus(selectedOptions);
        currentStatus.setStatus(status.name());
        Integer scoreOnStatus = findScoreOnStatus(status);
        gameService.saveCurrentScore(principal.getName(), scoreOnStatus);
        model.addAttribute("currentStatus", currentStatus);
        return "answer";
    }

    private Integer findScoreOnStatus(final ExamStatus status) {
        return status == ExamStatus.SUCCESS ? 1 : 0;
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
    @PostMapping("/check_score")
    public String scoreCheck(final Model model) {
        logger.info("On to the check_score :");
        //todo: load the score
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
        String moderator = gameService.findModerator();
        if (name.equalsIgnoreCase(moderator)) {
            currentQuestionNumber++;
        }
        QuestionOnGame questionOnGame = getQuestionOnGame(currentQuestionNumber);
        if (questionOnGame == null) {
            return new StartTrigger("END_GAME");
        }
        questionOnGame.setCurrentQuestionNumber(currentQuestionNumber);
        return new StartTrigger(currentQuestionNumber + 1
                + " : " + questionOnGame.getQuestion());
    }

    private QuestionOnGame getQuestionOnGame(final int currentQuestionNumber) {
        QuestionOnGame questionOnGame = null;
        if (currentQuestionNumber == 0) {
            questionOnGame = gameService.initialFetchAndUpdateNitrate();
        } else {
            List<QuestionSet> questionSets = gameService.getQuestionsOnAPin();
            if (questionSets.size() > currentQuestionNumber) {
                questionOnGame = gameService.fetchAnotherQuestion(currentQuestionNumber);
            }
        }
        return questionOnGame;
    }

    @MessageMapping("/fetch_scores")
    @SendTo("/topic/read_scores")
    public StartTrigger scoresFetch() {
        Map<String, Integer> scores = gameService.getCurrentScore();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(scores);
        } catch (JsonProcessingException exception) {
            logger.error(exception.getMessage());
        }
        return new StartTrigger(jsonString);
    }
}
