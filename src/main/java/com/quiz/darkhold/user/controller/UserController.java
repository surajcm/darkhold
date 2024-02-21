package com.quiz.darkhold.user.controller;

import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.exception.UserNotFoundException;
import com.quiz.darkhold.user.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * on to create challenge page.
     *
     * @return create challenge page
     */
    @GetMapping("/userManagement")
    public String manageUsers(final Model model) {
        logger.info("Into the manageUsers method");
        var listUsers = userService.listAll();
        model.addAttribute("listusers", listUsers);
        return "user/usermanagement";
    }

    @GetMapping("/user/create")
    public String createUser(final Model model) {
        logger.info("Into the createUser method");
        var roles = userService.listRoles();
        var user = new User();
        user.setEnabled(true);
        model.addAttribute("userForm", user);
        model.addAttribute("listRoles", roles);
        model.addAttribute("pageTitle", "Create New User");
        return "user/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(final User user, final RedirectAttributes redirectAttributes) {
        logger.info("Into the saveUser method, user is {}", user);
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return "redirect:/userManagement";
    }

    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable(name = "id") final Long id, final Model model,
                           final RedirectAttributes redirectAttributes) {
        logger.info("Into the editUser method, id is {}", id);
        try {
            var user = userService.get(id);
            model.addAttribute("userForm", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            model.addAttribute("listRoles", userService.listRoles());
            return "user/user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addAttribute("message", ex.getMessage());
        }
        return "redirect:/userManagement";
    }
}

