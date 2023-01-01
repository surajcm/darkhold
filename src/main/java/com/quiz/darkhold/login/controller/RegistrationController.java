package com.quiz.darkhold.login.controller;

import com.quiz.darkhold.home.model.GameInfo;
import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final UserService userService;

    public RegistrationController(final UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User user() {
        return new User();
    }

    @GetMapping
    public String registration(final Model model) {
        model.addAttribute("userForm", new User());
        logger.info("Inside the registration get method");
        return "registration";
    }

    @PostMapping
    public String registration(final Model model,
                               @ModelAttribute("user") final User user,
                               final BindingResult bindingResult) {
        logger.info("Inside the registration post method");
        //userValidator.validate(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            logger.info(String.valueOf(bindingResult.getAllErrors().get(0)));
            return "login";
        }
        userService.save(user);
        var gameInfo = new GameInfo();
        gameInfo.setMessage("Successfully created the account !!!");
        model.addAttribute("gameinfo", gameInfo);
        logger.info("successfully created the user !!!");
        return "index";
    }

}
