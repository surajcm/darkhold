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
    private final Log log = LogFactory.getLog(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;


    @PostMapping("/logme")
    public String loginGet(Model model, @ModelAttribute("userForm") User userForm, String error, String logout) {
        log.info("inside the login method");
        log.info("model is : "+model.asMap().keySet().toString());
        log.info("userForm is : "+userForm.getUsername());
        securityService.autoLogin(userForm.getUsername(), userForm.getPassword());
        log.info("autoLogin done !!!");
        if (error != null) {
            log.info("inside the login method, error : "+ error);
            model.addAttribute("error", "Your username and password is invalid.");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        model.addAttribute("userForm", new User());
        return "options";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        log.info("inside the registration get method");
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(Model model, @ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        log.info("inside the registration post method");
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors().get(0));
            return "login";
        }
        userService.save(userForm);

        //return "redirect:/";
        GameInfo gameInfo = new GameInfo();
        gameInfo.setMessage("Successfully created the account !!!");
        model.addAttribute("gameinfo", gameInfo);
        return "index";
    }

    @PostMapping("/logmein")
    public String loginMe(Model model) {
        log.info("into loginMe");
        return "login";
    }

    @GetMapping("/logmein")
    public String loginMe2(Model model) {
        log.info("into GET loginMe");
        return "login";
    }
}
