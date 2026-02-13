package com.quiz.darkhold.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for admin dashboard and system management.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    /**
     * Show admin dashboard with system statistics.
     *
     * @param model Spring MVC model
     * @return view name
     */
    @GetMapping({"", "/"})
    public String showAdminDashboard(final Model model) {
        // Statistics will be added later via service layer
        // For now, showing the dashboard structure
        model.addAttribute("totalUsers", 0);
        model.addAttribute("activeGames", 0);
        model.addAttribute("totalChallenges", 0);
        model.addAttribute("gamesPlayed", 0);

        return "admin/dashboard";
    }
}
