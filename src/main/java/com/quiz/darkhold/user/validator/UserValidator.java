package com.quiz.darkhold.user.validator;

import com.quiz.darkhold.user.entity.User;
import com.quiz.darkhold.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Locale;

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

        var password = user.getPassword();

        if (!isValidPasswordLength(password)) {
            errors.rejectValue("password", "Size.userForm.password");
            return;
        }

        validatePasswordStrength(errors, password);
    }

    private boolean isValidPasswordLength(final String password) {
        return password.length() >= 8 && password.length() <= 32;
    }

    private void validatePasswordStrength(final Errors errors, final String password) {
        validatePasswordCharacterRequirements(errors, password);
        if (isCommonPassword(password)) {
            errors.rejectValue("password", "Common.userForm.password");
        }
    }

    private void validatePasswordCharacterRequirements(final Errors errors, final String password) {
        if (!password.matches(".*[A-Z].*")) {
            errors.rejectValue("password", "Strength.userForm.password.uppercase");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.rejectValue("password", "Strength.userForm.password.lowercase");
        }
        if (!password.matches(".*\\d.*")) {
            errors.rejectValue("password", "Strength.userForm.password.digit");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            errors.rejectValue("password", "Strength.userForm.password.special");
        }
    }

    /**
     * Check if password is in a list of commonly used passwords.
     *
     * @param password the password to check
     * @return true if the password is common
     */
    private boolean isCommonPassword(final String password) {
        var commonPasswords = new String[]{
            "password", "Password1", "12345678", "qwerty", "abc123",
            "password123", "admin123", "letmein", "welcome", "monkey",
            "1234567890", "password1", "qwerty123", "welcome1", "admin"
        };

        var lowerPassword = password.toLowerCase(Locale.ROOT);
        for (var common : commonPasswords) {
            if (lowerPassword.equals(common.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
