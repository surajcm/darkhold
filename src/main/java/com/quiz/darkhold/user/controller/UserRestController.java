package com.quiz.darkhold.user.controller;

import com.quiz.darkhold.user.service.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    private final UserService userService;

    public UserRestController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@Param("id") final Long id, @Param("email") final String email) {
        return userService.isEmailUnique(id, email) ? "OK" : "DUPLICATED";
    }
}
