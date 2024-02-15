package com.quiz.darkhold.user.validator;

import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    private static final String USER_NAME = "username";
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(final Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        User user = (User) obj;
        validateUserName(errors, user);
        validatePassword(errors, user);
    }

    private void validateUserName(final Errors errors, final User user) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, USER_NAME, "NotEmpty");
        if (user.getEmail().length() < 6 || user.getEmail().length() > 32) {
            errors.rejectValue(USER_NAME, "Size.userForm.username");
        }
        if (userService.findByUsername(user.getEmail()) != null) {
            errors.rejectValue(USER_NAME, "Duplicate.userForm.username");
        }
    }

    private void validatePassword(final Errors errors, final User user) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }
    }
}
