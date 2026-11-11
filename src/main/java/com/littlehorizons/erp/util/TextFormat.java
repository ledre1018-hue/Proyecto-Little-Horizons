package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Small helpers to show values in a consistent way.
 */
public final class TextFormat {

    private static final Locale COLOMBIA = new Locale("es", "CO");
    private static final String EMPTY_VALUE = "-";

    private TextFormat() {
    }

    public static String money(BigDecimal amount) {
        if (amount == null) {
            return EMPTY_VALUE;
        }
        return String.format(COLOMBIA, "$ %,.0f", amount);
    }

    public static String grade(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    public static String average(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    public static String date(LocalDate date) {
        return date == null ? EMPTY_VALUE : date.toString();
    }

    public static String orDash(Object value) {
        if (value == null || value.toString().isBlank()) {
            return EMPTY_VALUE;
        }
        return value.toString();
    }

    public static String yesNo(boolean value) {
        return value ? "Sí" : "No";
    }
}
