package com.projectmanagement.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date Utility for parsing and formatting SQL Dates.
 */
public final class DateUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateUtils() {
    }

    public static Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(dateStr.trim(), ISO_FORMATTER);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) return "";
        return date.toString();
    }
}
