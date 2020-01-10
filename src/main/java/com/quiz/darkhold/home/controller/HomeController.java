package com.quiz.darkhold.home.controller;

import com.quiz.darkhold.home.model.GameInfo;
import com.quiz.darkhold.home.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HomeController {
    public static final String GAME_INFO = "gameinfo";

    @Autowired
    private HomeService homeService;

    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home(Model model) {
        logger.info("Going home page ");
        model.addAttribute(GAME_INFO, new GameInfo());
        return "index";
    }

    @PostMapping("/home")
    public String toHome(Model model) {
        logger.info("Going to toHome page ");
        model.addAttribute(GAME_INFO, new GameInfo());
        return "index";
    }

    @PostMapping("/enterGame/")
    public @ResponseBody
    Boolean enterGame(@ModelAttribute("gamePin") String gamePin) {
        logger.info("Game pin is "+ gamePin);
        return homeService.validateGamePin(gamePin);
    }

    @PostMapping("/joinGame")
    public String joinGame(@ModelAttribute GameInfo gameInfo, Model model) {
        logger.info("joinGame : gameInfo is "+ gameInfo);
        List<String> activeUsers = homeService.participantsInActiveQuiz(gameInfo.getGamePin());
        activeUsers.add(gameInfo.getName());
        gameInfo.setUsers(activeUsers);
        model.addAttribute(GAME_INFO, gameInfo);
        return "gamewait";
    }
}
