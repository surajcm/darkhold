package com.quiz.darkhold.login.controller;

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
    private final Log log = LogFactory.getLog(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;


    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        log.info("inside the login method");
        if (error != null) {
            log.info("inside the login method, error : "+ error);
            model.addAttribute("error", "Your username and password is invalid.");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        model.addAttribute("userForm", new User());
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        log.info("inside the registration get method");
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        log.info("inside the registration post method");
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().get(0));
            return "login";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/";
    }

    @PostMapping("/loginMe")
    public String loginMe(Model model) {
        log.info("into loginMe");
        return "login";
    }
}
