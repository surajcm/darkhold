package com.quiz.darkhold.login.controller;

import com.quiz.darkhold.home.model.GameInfo;
import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.service.SecurityService;
import com.quiz.darkhold.login.service.UserService;
import com.quiz.darkhold.login.validator.UserValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    private static final String LOGIN = "login";
    private final Log log = LogFactory.getLog(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    /**
     * log me in.
     *
     * @param model model
     * @param userForm user info
     * @param error if present
     * @param logout if triggered logout
     * @return to the home options screen
     */
    @PostMapping("/logme")
    public String loginGet(final Model model, @ModelAttribute("userForm") final User userForm,
                           final String error, final String logout) {
        log.info("inside the login method");
        log.info("userForm is : " + userForm.getUsername());
        securityService.autoLogin(userForm.getUsername(), userForm.getPassword());
        log.info("autoLogin done !!!");
        if (error != null) {
            log.info("inside the login method, error : " + error);
            model.addAttribute("error", "Your username and password is invalid.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        model.addAttribute("userForm", new User());
        return "options";
    }

    /**
     * Registration page.
     *
     * @param model model
     * @return registration page
     */
    @GetMapping("/registration")
    public String registration(final Model model) {
        model.addAttribute("userForm", new User());
        log.info("inside the registration get method");
        return "registration";
    }

    /**
     * Register a new user.
     *
     * @param model model
     * @param userForm user info
     * @param bindingResult validation binding
     * @return go to the index page after login
     */
    @PostMapping("/registration")
    public String registration(final Model model, @ModelAttribute("userForm") final User userForm,
                               final BindingResult bindingResult) {
        log.info("inside the registration post method");
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().get(0));
            return LOGIN;
        }
        userService.save(userForm);

        GameInfo gameInfo = new GameInfo();
        gameInfo.setMessage("Successfully created the account !!!");
        model.addAttribute("gameinfo", gameInfo);
        return "index";
    }

    /**
     * on to login page via post.
     * @param model model
     * @return login
     */
    @PostMapping("/logmein")
    public String loginMe(final Model model) {
        log.info("into loginMe");
        return LOGIN;
    }

    /**
     * on to login page via get.
     * @param model model
     * @return login
     */
    @GetMapping("/logmein")
    public String loginMe2(final Model model) {
        log.info("into GET loginMe");
        return LOGIN;
    }
}
