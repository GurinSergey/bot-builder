package com.example.bot_builder.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }

        String regex = "^(?![-_.+])[\\w-_.+]{0,64}(?<![-_.+])\\@(?!-)(?=[a-zA-Z0-9-]{0,64}\\.)([a-zA-Z0-9-]+\\.)+[a-zA-Z0-9-]+(?<!-)$";

        return email.matches(regex);
    }

    public static boolean isEmpty(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

    public static boolean isValidMobileNumber(String mobile) {
        if (isEmpty(mobile)) {
            return false;
        }

        String normalized = mobile.replaceAll("[\\s\\-()]", "");

        Pattern pattern = Pattern.compile("^(\\+380\\d{9}|0\\d{9})$");
        Matcher matcher = pattern.matcher(normalized);

        return matcher.matches();
    }
}
