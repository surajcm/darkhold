package com.quiz.darkhold.login.validator;

import com.quiz.darkhold.login.entity.User;
import com.quiz.darkhold.login.service.UserService;
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
        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
            errors.rejectValue(USER_NAME, "Size.userForm.username");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue(USER_NAME, "Duplicate.userForm.username");
        }
    }

    private void validatePassword(final Errors errors, final User user) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }
        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}
