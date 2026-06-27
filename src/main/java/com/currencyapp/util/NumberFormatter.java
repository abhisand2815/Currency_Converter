package com.currencyapp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NumberFormatter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatRate(double rate, int precision) {
        int decimals = Math.max(2, Math.min(6, precision));
        return String.format(Locale.US, "%." + decimals + "f", rate);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
