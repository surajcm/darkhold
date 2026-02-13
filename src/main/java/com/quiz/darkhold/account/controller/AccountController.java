package com.quiz.darkhold.account.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for user account settings and preferences.
 */
@Controller
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    /**
     * Show account settings page.
     *
     * @return view name
     */
    @GetMapping({"", "/"})
    public String showAccountSettings() {
        return "account/settings";
    }
}
