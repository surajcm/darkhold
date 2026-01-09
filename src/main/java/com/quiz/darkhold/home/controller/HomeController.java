package com.quiz.darkhold.home.controller;

import com.quiz.darkhold.home.model.GameInfo;
import com.quiz.darkhold.home.service.HomeService;
import com.quiz.darkhold.user.service.SecurityService;
import com.quiz.darkhold.util.CommonUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    private static final String GAME_INFO = "gameinfo";
    private static final String UNREGISTERED_USER = "UNREGISTERED_USER";
    private final Logger logger = LogManager.getLogger(HomeController.class);

    private final HomeService homeService;

    private final SecurityService securityService;

    public HomeController(final HomeService homeService, final SecurityService securityService) {
        this.homeService = homeService;
        this.securityService = securityService;
    }

    /**
     * Initial home redirect.
     *
     * @param model model
     * @return to index
     */
    @GetMapping("/")
    public String home(final Model model) {
        logger.info("Going home page");
        model.addAttribute(GAME_INFO, new GameInfo());
        return "index";
    }

    /**
     * same home redirect from various pages on post.
     *
     * @param model model
     * @return to index
     */
    @PostMapping("/home")
    public String toHome(final Model model) {
        logger.info("Going to toHome page");
        model.addAttribute(GAME_INFO, new GameInfo());
        return "index";
    }

    /**
     * Validate the user entered pin and direct the user to name entering screen.
     *
     * @param gamePin pin
     * @return ajax call to same page
     */
    @PostMapping("/enterGame")
    public @ResponseBody
    Boolean enterGame(@ModelAttribute("gamePin") final String gamePin) {
        var sanitizedPin = CommonUtils.sanitizedString(gamePin);
        logger.info("Game pin is : {}", sanitizedPin);
        return homeService.validateGamePin(gamePin);
    }

    /**
     * If the user entered pin is correct, go to the page where everyone waits for the game to start.
     *
     * @param gameInfo      user info
     * @param bindingResult validation result
     * @param model         model
     * @param session       HTTP session to store game PIN
     * @return wait screen or index with errors
     */
    @PostMapping("/joinGame")
    public String joinGame(@Valid @ModelAttribute final GameInfo gameInfo,
                           final BindingResult bindingResult,
                           final Model model,
                           final HttpSession session) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in joinGame: {}", bindingResult.getAllErrors());
            model.addAttribute(GAME_INFO, gameInfo);
            return "index";
        }
        logger.info("joinGame : gameInfo is {}", gameInfo);
        securityService.autoLogin(gameInfo.getName(), UNREGISTERED_USER);
        populateGameInfo(gameInfo);
        // Store PIN in session for concurrent game support
        session.setAttribute("gamePin", gameInfo.getGamePin());
        logger.info("Stored gamePin in session: {}", gameInfo.getGamePin());
        model.addAttribute(GAME_INFO, gameInfo);
        return "game/gamewait";
    }

    private void populateGameInfo(final GameInfo gameInfo) {
        var activeUsers = homeService.participantsInActiveQuiz(gameInfo.getGamePin());
        activeUsers.add(gameInfo.getName());
        gameInfo.setUsers(activeUsers);
        gameInfo.setModerator(homeService.getModerator(gameInfo.getGamePin()));
    }
}
