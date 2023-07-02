package com.quiz.darkhold.login.controller;

import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.service.SecurityService;
import com.quiz.darkhold.util.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.AbstractMap;
import java.util.Map;

@Controller
public class LoginController {
    private static final String LOGIN = "login";
    private final Logger logger = LogManager.getLogger(LoginController.class);

    private final SecurityService securityService;

    public LoginController(final SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * log me in.
     * Not using this method
     *
     * @param model    model
     * @param userName userName
     * @param password password
     * @param error    if present
     * @param logout   if triggered logout
     * @return to the home options screen
     */
    @PostMapping("/logme")
    public String loginGet(final Model model,
                           @ModelAttribute("username") final String userName,
                           @ModelAttribute("password") final String password,
                           final String error, final String logout) {
        logUserName(userName);
        securityService.autoLogin(userName, password);
        logger.info("AutoLogin done !!!");
        if (error != null || logout != null) {
            var tuple = modelAttributes(error, logout);
            model.addAttribute(tuple.getKey(), tuple.getValue());
        }
        model.addAttribute("userForm", new User());
        return "options/options";
    }

    @GetMapping("/login")
    public String login(final Model model, final String error, final String logout) {
        if (securityService.isAuthenticated()) {
            logger.info("authenticated ...");
            return "redirect:/";
        }
        if (error != null) {
            logger.info("error is there ...{}", error);
            model.addAttribute("danger", "Your username or password is invalid.");
        }
        if (logout != null) {
            logger.info("logout is there ... {}", logout);
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(final HttpServletRequest request) {
        logger.info("Inside logout method of user controller");
        var session = request.getSession(false);
        SecurityContextHolder.clearContext();
        if (session != null) {
            session.invalidate();
        }
        return "login";
    }

    private void logUserName(final String userName) {
        var sanitizedUserName = CommonUtils.sanitizedString(userName);
        logger.info("Login method : user name is {}", sanitizedUserName);
    }

    private Map.Entry<String, String> modelAttributes(final String error, final String logout) {
        Map.Entry<String, String> tuple = null;
        if (error != null) {
            logError(error);
            tuple = new AbstractMap.SimpleEntry<>("error", "Your username and password is invalid.");
        }
        if (logout != null) {
            tuple = new AbstractMap.SimpleEntry<>("message", "You have been logged out successfully.");
        }
        return tuple;
    }

    private void logError(final String error) {
        var sanitizedError = CommonUtils.sanitizedString(error);
        logger.info("Login method, error : {}", sanitizedError);
    }

    /**
     * on to login page via post.
     *
     * @return login
     */
    @PostMapping("/logmein")
    public String loginMe() {
        logger.info("into loginMe");
        return LOGIN;
    }

    /**
     * on to login page via get.
     *
     * @return login
     */
    @GetMapping("/logmein")
    public String loginMe2() {
        logger.info("into GET loginMe");
        return LOGIN;
    }
}
