package com.quiz.darkhold.game.controller;

import com.quiz.darkhold.challenge.entity.QuestionSet;
import com.quiz.darkhold.challenge.entity.QuestionType;
import com.quiz.darkhold.game.model.Challenge;
import com.quiz.darkhold.game.model.CurrentScore;
import com.quiz.darkhold.game.model.ExamStatus;
import com.quiz.darkhold.game.model.Game;
import com.quiz.darkhold.game.model.QuestionPointer;
import com.quiz.darkhold.game.model.StartTrigger;
import com.quiz.darkhold.game.model.UserResponse;
import com.quiz.darkhold.analytics.service.ResultService;
import com.quiz.darkhold.game.service.AnswerValidationService;
import com.quiz.darkhold.game.service.GameService;
import com.quiz.darkhold.init.GameConfig;
import com.quiz.darkhold.team.model.TeamInfo;
import com.quiz.darkhold.team.service.TeamService;
import com.quiz.darkhold.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final GameConfig gameConfig;
    private final AnswerValidationService answerValidationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ResultService resultService;
    private final TeamService teamService;

    public GameController(final GameService gameService,
                          final GameConfig gameConfig,
                          final AnswerValidationService answerValidationService,
                          final SimpMessagingTemplate messagingTemplate,
                          final ResultService resultService,
                          final TeamService teamService) {
        this.gameService = gameService;
        this.gameConfig = gameConfig;
        this.answerValidationService = answerValidationService;
        this.messagingTemplate = messagingTemplate;
        this.resultService = resultService;
        this.teamService = teamService;
    }

    @PostMapping("/interstitial")
    public String startInterstitial(final Model model, @RequestParam("quiz_pin") final String quizPin) {
        var sanitizedPin = CommonUtils.sanitizedString(quizPin);
        logger.info("On to interstitial : {}", sanitizedPin);
        model.addAttribute("quizPin", quizPin);
        return "interstitial";
    }

    /**
     * On to the page where the only question is getting displayed for few seconds.
     *
     * @param model     model
     * @param quizPin   the quiz PIN
     * @param principal auth
     * @return question page
     */
    @PostMapping("/question")
    public String question(final Model model,
                           @RequestParam(value = "quizPin", required = false) final String quizPin,
                           final Principal principal) {
        logger.info("On to question : {}", quizPin);
        var questionPointer = quizPin != null
                ? gameService.getCurrentQuestionPointer(quizPin)
                : gameService.getCurrentQuestionPointer();
        logger.info("Question pointer: current={}, total={}",
                questionPointer.getCurrentQuestionNumber(),
                questionPointer.getTotalQuestionCount());

        // Check if question pointer is valid (total count should be > 0)
        if (questionPointer.getTotalQuestionCount() == 0) {
            logger.error("Invalid question pointer - total count is 0. PIN: {}", quizPin);
            return finalScore(model, quizPin);
        }

        if (questionPointer.getCurrentQuestionNumber() == questionPointer.getTotalQuestionCount()) {
            logger.warn("Game ending: currentQuestion == totalQuestions ({} == {})",
                    questionPointer.getCurrentQuestionNumber(),
                    questionPointer.getTotalQuestionCount());
            return finalScore(model, quizPin);
        }
        model.addAttribute("quizPin", quizPin);
        logger.info("Going to question page");
        return "question";
    }

    @PostMapping("/final")
    public String finalScore(final Model model,
                             @RequestParam(value = "quizPin", required = false) final String quizPin) {
        logger.info("On to the finalScore for PIN: {}", quizPin);
        var score = new CurrentScore();
        var scores = gameService.getCurrentScore();
        score.setScore(scores);
        model.addAttribute("score", score);

        // Check if team mode and add team scores
        if (quizPin != null && gameService.isTeamMode(quizPin)) {
            var teamScores = gameService.getTeamScores(quizPin);
            model.addAttribute("teamScores", teamScores);
            model.addAttribute("isTeamMode", true);
            var teams = teamService.getTeams(quizPin);
            model.addAttribute("teams", teams);
        } else {
            model.addAttribute("isTeamMode", false);
        }

        // Save game results for analytics
        saveGameResultIfAvailable(quizPin);

        gameService.cleanUpCurrentGame();
        return "finalscore";
    }

    private void saveGameResultIfAvailable(final String quizPin) {
        if (quizPin != null && !quizPin.isEmpty()) {
            try {
                String challengeName = "Quiz Session";
                resultService.saveGameResult(quizPin, challengeName);
                logger.info("Game results saved for PIN: {}", quizPin);
                // OK to catch RuntimeException
            } catch (RuntimeException ex) {
                logger.error("Failed to save game results for PIN: {}", quizPin, ex);
            }
        }
    }

    /**
     * On to the game.
     *
     * @param model     model
     * @param quizPin   the quiz PIN
     * @param principal auth
     * @return game page
     */
    @PostMapping("/game")
    public String startGame(final Model model,
                            @RequestParam(value = "quizPin", required = false) final String quizPin,
                            final Principal principal) {
        logger.info("On to game : {}", quizPin);
        var questionPointer = quizPin != null
                ? gameService.getCurrentQuestionPointer(quizPin)
                : gameService.getCurrentQuestionPointer();
        var currentQuestion = questionPointer.getCurrentQuestion();
        var challenge = buildChallengeModel(questionPointer, currentQuestion);
        var gameMode = quizPin != null
                ? gameService.getGameMode(quizPin)
                : gameService.getGameMode();

        model.addAttribute("challenge", challenge);
        model.addAttribute("quizPin", quizPin);
        model.addAttribute("gameMode", gameMode.name());
        addQuestionAttributes(model, currentQuestion);
        return "game";
    }

    private Challenge buildChallengeModel(final QuestionPointer qp, final QuestionSet qs) {
        var challenge = new Challenge();
        challenge.setQuestionNumber(qp.getCurrentQuestionNumber());
        challenge.setTotalQuestions(qp.getTotalQuestionCount());
        challenge.setQuestionSet(qs);
        challenge.setQuestionNumber(challenge.getQuestionNumber() + 1);
        return challenge;
    }

    private void addQuestionAttributes(final Model model, final QuestionSet qs) {
        int timeLimit = qs.getTimeLimit() != null ? qs.getTimeLimit() : gameConfig.getTimerSeconds();
        model.addAttribute("game_timer", String.valueOf(timeLimit));
        var qType = qs.getQuestionType() != null ? qs.getQuestionType() : QuestionType.MULTIPLE_CHOICE;
        model.addAttribute("question_type", qType.name());
        int points = qs.getPoints() != null ? qs.getPoints() : 1000;
        model.addAttribute("question_points", String.valueOf(points));
    }

    @PostMapping("/answer/")
    public @ResponseBody
    Boolean enterGame(@ModelAttribute("selectedOptions") final String selectedOptions,
                      @ModelAttribute("user") final String user,
                      @ModelAttribute("timeTook") final String timeTook) {
        logParams(selectedOptions, user, timeTook);
        var status = getExamStatus(selectedOptions);
        var moderator = gameService.findModerator();
        boolean isPracticeMode = gameService.isPracticeMode();

        logger.info("is user moderator : {}", user.equalsIgnoreCase(moderator));
        logger.info("is practice mode : {}", isPracticeMode);

        // In practice mode, moderator CAN score. In multiplayer, moderator cannot.
        if (user.equalsIgnoreCase(moderator) && !isPracticeMode) {
            // Multiplayer: moderator doesn't earn points, just controls game flow
            gameService.savePreviousScores();
            gameService.incrementQuestionNo();
        } else {
            // Regular player OR practice mode player (who is also moderator)
            processPlayerAnswer(user, status, timeTook);
            // In practice mode, also increment question after scoring
            if (isPracticeMode && user.equalsIgnoreCase(moderator)) {
                gameService.savePreviousScores();
                gameService.incrementQuestionNo();
            }
        }
        return true;
    }

    private void processPlayerAnswer(final String user, final ExamStatus status, final String timeTook) {
        boolean isCorrect = status == ExamStatus.SUCCESS;
        int streak = gameService.updateStreak(user, isCorrect);
        var questionPointer = gameService.getCurrentQuestionPointer();
        var currentQuestion = questionPointer.getCurrentQuestion();
        int basePoints = currentQuestion.getPoints() != null ? currentQuestion.getPoints() : 1000;
        var scoreOnStatus = calculateScoreWithStreak(status, timeTook, basePoints, streak);
        logger.info("score is {} (streak: {}, base: {})", scoreOnStatus, streak, basePoints);
        gameService.saveCurrentScore(user, scoreOnStatus);
    }

    /**
     * Validate a TYPE_ANSWER response server-side using fuzzy matching.
     *
     * @param userAnswer the user's typed answer
     * @param user       the username
     * @param timeTook   time taken in ms
     * @return "correct" or "incorrect"
     */
    @PostMapping("/validate_answer/")
    public @ResponseBody
    String validateTypeAnswer(@ModelAttribute("userAnswer") final String userAnswer,
                              @ModelAttribute("user") final String user,
                              @ModelAttribute("timeTook") final String timeTook) {
        logger.info("Validating TYPE_ANSWER: '{}' for user {}", userAnswer, user);
        var questionPointer = gameService.getCurrentQuestionPointer();
        var currentQuestion = questionPointer.getCurrentQuestion();

        boolean isCorrect = answerValidationService.validateAnswer(currentQuestion, userAnswer);
        var status = isCorrect ? ExamStatus.SUCCESS : ExamStatus.FAILURE;

        var moderator = gameService.findModerator();
        boolean isPracticeMode = gameService.isPracticeMode();

        // In practice mode, moderator CAN score. In multiplayer, moderator cannot.
        if (!user.equalsIgnoreCase(moderator) || isPracticeMode) {
            processPlayerAnswer(user, status, timeTook);
            // In practice mode, also increment question after scoring
            if (isPracticeMode && user.equalsIgnoreCase(moderator)) {
                gameService.savePreviousScores();
                gameService.incrementQuestionNo();
            }
        }

        return isCorrect ? "correct" : "incorrect";
    }

    private void logParams(final String selectedOptions, final String user, final String timeTook) {
        var sanitizedOptions = CommonUtils.sanitizedString(selectedOptions);
        var sanitizedUser = CommonUtils.sanitizedString(user);
        var sanitizedTime = CommonUtils.sanitizedString(timeTook);
        logger.info("selectedOptions are {}, user is {}, and timeTook is {}",
                sanitizedOptions, sanitizedUser, sanitizedTime);
    }

    private int calculateScoreWithStreak(final ExamStatus status, final String timeTook,
                                          final int basePoints, final int streak) {
        if (status != ExamStatus.SUCCESS) {
            return 0;
        }
        return calculateTimeFactor(timeTook, basePoints, streak);
    }

    private int calculateTimeFactor(final String timeTook, final int basePoints, final int streak) {
        try {
            var timeTakenMs = Integer.parseInt(timeTook);
            if (timeTakenMs <= 0) {
                return 0;
            }
            var timerMs = gameConfig.getTimerSeconds() * 1000;
            int timeFactor = Math.max(0, (timerMs - timeTakenMs) * 1000 / timerMs);
            return gameService.calculateScoreWithStreak(basePoints, timeFactor, streak);
        } catch (NumberFormatException ex) {
            logger.info("Invalid time format: {}", ex.getMessage());
            return 0;
        }
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
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param game game
     */
    @MessageMapping("/user")
    public void getGame(final Game game) {
        logger.info("On to getGame : {}", game);
        var users = gameService.getAllParticipants(game.getPin());
        var response = new UserResponse(users);
        messagingTemplate.convertAndSend("/topic/" + game.getPin() + "/user", response);
    }

    /**
     * Trigger for starting the game.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin       of the game
     * @param principal of who started it
     */
    @MessageMapping("/start")
    public void startTrigger(final String pin, final Principal principal) {
        // this is triggered by the game moderator
        logger.info("On to startGame : {}", pin);
        logger.info("On to startGame : user : {}", principal.getName());
        // who started the game is already in nitrate
        var trigger = new StartTrigger(pin);
        messagingTemplate.convertAndSend("/topic/" + pin + "/start", trigger);
    }

    /**
     * Fetch current question for display.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param message format: "pin:username" or just "pin"
     */
    @MessageMapping("/question_fetch")
    public void questionFetch(final String message) {
        logger.info("On to questionFetch : {}", message);
        // Extract PIN from message (format: "pin" or "pin:username")
        String pin = message.contains(":") ? message.substring(0, message.indexOf(":")) : message;
        var questionPointer = gameService.getCurrentQuestionPointer(pin);
        StartTrigger trigger;
        if (questionPointer.getCurrentQuestionNumber() == questionPointer.getTotalQuestionCount()) {
            trigger = new StartTrigger("END_GAME");
        } else {
            logger.info("On questionPointer.getCurrentQuestionNumber() : {}",
                    questionPointer.getCurrentQuestionNumber());
            trigger = new StartTrigger(questionPointer.getCurrentQuestionNumber() + 1 + " : "
                    + questionPointer.getCurrentQuestion().getQuestion());
        }
        messagingTemplate.convertAndSend("/topic/" + pin + "/question_read", trigger);
    }

    /**
     * Signal that scores are ready to be fetched.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game PIN
     */
    @MessageMapping("/fetch_scores")
    public void scoresFetch(final String pin) {
        logger.info("On to scoresFetch for game: {}", pin);
        messagingTemplate.convertAndSend("/topic/" + pin + "/read_scores", true);
    }

    // ==================== Game Control WebSocket Endpoints ====================

    /**
     * Pause the current game.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game pin
     */
    @MessageMapping("/pause_game")
    public void pauseGame(final String pin) {
        logger.info("Pausing game: {}", pin);
        Boolean result = gameService.pauseGame();
        messagingTemplate.convertAndSend("/topic/" + pin + "/game_paused", result);
    }

    /**
     * Resume a paused game.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game pin
     */
    @MessageMapping("/resume_game")
    public void resumeGame(final String pin) {
        logger.info("Resuming game: {}", pin);
        Long elapsed = gameService.resumeGame();
        messagingTemplate.convertAndSend("/topic/" + pin + "/game_resumed", elapsed);
    }

    /**
     * Skip the current question.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game pin
     */
    @MessageMapping("/skip_question")
    public void skipQuestion(final String pin) {
        logger.info("Skipping question for game: {}", pin);
        gameService.savePreviousScores();
        gameService.skipQuestion();
        messagingTemplate.convertAndSend("/topic/" + pin + "/question_skipped", true);
    }

    /**
     * End the game early.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game pin
     */
    @MessageMapping("/end_game_early")
    public void endGameEarly(final String pin) {
        logger.info("Ending game early: {}", pin);
        gameService.setGameStatus(
                com.quiz.darkhold.game.model.GameStatus.ENDED);
        messagingTemplate.convertAndSend("/topic/" + pin + "/game_ended", true);
    }

    /**
     * Kick a player from the game.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param message format: "pin:username"
     */
    @MessageMapping("/kick_player")
    public void kickPlayer(final String message) {
        // Expected format: "pin:username"
        String[] parts = message.split(":", 2);
        if (parts.length != 2) {
            logger.warn("Invalid kick message format: {}", message);
            return;
        }
        String pin = parts[0];
        String username = parts[1];
        logger.info("Kicking player {} from game {}", username, pin);
        boolean kicked = gameService.kickPlayer(username);
        String result = kicked ? username : null;
        messagingTemplate.convertAndSend("/topic/" + pin + "/player_kicked", result);
    }

    /**
     * Get current participant count.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game pin
     */
    @MessageMapping("/participant_count")
    public void getParticipantCount(final String pin) {
        Integer count = gameService.getParticipantCount();
        messagingTemplate.convertAndSend("/topic/" + pin + "/participant_count", count);
    }

    // ==================== Team Mode WebSocket Endpoints ====================

    /**
     * Assign a player to a team.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param message format: "pin:username:teamName"
     */
    @MessageMapping("/team/assign")
    public void assignToTeam(final String message) {
        String[] parts = message.split(":", 3);
        if (parts.length != 3) {
            logger.warn("Invalid team assign message format: {}", message);
            return;
        }
        String pin = parts[0];
        String username = parts[1];
        String teamName = parts[2];

        logger.info("Assigning player {} to team {} in game {}", username, teamName, pin);
        teamService.assignPlayerToTeam(pin, username, teamName);

        java.util.List<TeamInfo> teams = teamService.getTeams(pin);
        messagingTemplate.convertAndSend("/topic/" + pin + "/team_update", teams);
    }

    /**
     * Get current team configuration.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game pin
     */
    @MessageMapping("/team/list")
    public void getTeams(final String pin) {
        logger.info("Fetching teams for game: {}", pin);
        java.util.List<TeamInfo> teams = teamService.getTeams(pin);
        messagingTemplate.convertAndSend("/topic/" + pin + "/team_list", teams);
    }

    /**
     * Fetch team scores.
     * Sends to PIN-scoped topic for concurrent game support.
     *
     * @param pin game pin
     */
    @MessageMapping("/team/scores")
    public void getTeamScores(final String pin) {
        logger.info("Fetching team scores for game: {}", pin);
        java.util.Map<String, Integer> teamScores = teamService.calculateTeamScores(pin);
        messagingTemplate.convertAndSend("/topic/" + pin + "/team_scores", teamScores);
    }
}