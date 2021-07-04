package com.quiz.darkhold.util;

public class CommonUtils {

    private CommonUtils() {

    }

    public static String sanitizedString(final String unFormatted) {
        String sanitized = null;
        if (unFormatted != null) {
            sanitized = unFormatted.replaceAll("[\n\r\t]", "_");
        }
        return sanitized;
    }
}
