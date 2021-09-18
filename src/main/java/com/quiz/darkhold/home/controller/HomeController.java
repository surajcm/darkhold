package com.quiz.darkhold.home.controller;

import com.quiz.darkhold.home.model.GameInfo;
import com.quiz.darkhold.home.service.HomeService;
import com.quiz.darkhold.login.service.SecurityService;
import com.quiz.darkhold.util.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    @PostMapping("/enterGame/")
    public @ResponseBody
    Boolean enterGame(@ModelAttribute("gamePin") final String gamePin) {
        var sanitizedPin = CommonUtils.sanitizedString(gamePin);
        logger.info("Game pin is : {}", sanitizedPin);
        return homeService.validateGamePin(gamePin);
    }

    /**
     * If the user entered pin is correct, go to the page where everyone waits for the game to start.
     *
     * @param gameInfo user info
     * @param model    model
     * @return wait screen
     */
    @PostMapping("/joinGame")
    public String joinGame(@ModelAttribute final GameInfo gameInfo, final Model model) {
        logger.info("joinGame : gameInfo is {}", gameInfo);
        securityService.autoLogin(gameInfo.getName(), UNREGISTERED_USER);
        logger.info("autoLogin done !!!");
        var activeUsers = homeService.participantsInActiveQuiz(gameInfo.getGamePin());
        activeUsers.add(gameInfo.getName());
        gameInfo.setUsers(activeUsers);
        model.addAttribute(GAME_INFO, gameInfo);
        return "gamewait";
    }
}
