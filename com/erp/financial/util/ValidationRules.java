package com.erp.financial.util;

import com.erp.financial.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class ValidationRules {
    private ValidationRules() {
    }

    public static void requireText(Map<String, Object> values, String key, String label) {
        Object value = values.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            throw new ValidationException(label + " is required.");
        }
    }

    public static void requireDate(Map<String, Object> values, String key, String label) {
        Object value = values.get(key);
        if (!(value instanceof LocalDate)) {
            throw new ValidationException(label + " must be a valid date in YYYY-MM-DD format.");
        }
    }

    public static void requirePositiveDecimal(Map<String, Object> values, String key, String label) {
        BigDecimal value = decimal(values, key);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(label + " must be greater than zero.");
        }
    }

    public static void requireNonNegativeDecimal(Map<String, Object> values, String key, String label) {
        BigDecimal value = decimal(values, key);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(label + " cannot be negative.");
        }
    }

    public static void requireChoice(Map<String, Object> values, String key, String label, List<String> allowed) {
        Object value = values.get(key);
        if (value == null || !allowed.contains(String.valueOf(value))) {
            throw new ValidationException(label + " must be one of: " + allowed);
        }
    }

    public static BigDecimal decimal(Map<String, Object> values, String key) {
        Object value = values.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException exception) {
            throw new ValidationException(key + " must be a valid decimal number.");
        }
    }

    public static LocalDate date(Map<String, Object> values, String key) {
        Object value = values.get(key);
        if (value instanceof LocalDate date) {
            return date;
        }
        throw new ValidationException(key + " must be a valid date.");
    }
}
