package com.quiz.darkhold.login.controller;

import com.quiz.darkhold.login.model.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    private final Log log = LogFactory.getLog(LoginController.class);

    @PostMapping("/login")
    @GetMapping("/login")
    public String login(Model model) {
        log.info("Going to login page ");
        model.addAttribute("uservo", new UserVO());
        return "login";
    }

    @PostMapping("/logmein")
    public String logMe(@ModelAttribute UserVO userVO) {
        log.info("into the log me method");
        return "options";
    }

    @PostMapping("/signUp")
    public String signUp(@ModelAttribute UserVO userVO) {
        log.info("into the sign up method");
        return "myprofile";
    }
}
