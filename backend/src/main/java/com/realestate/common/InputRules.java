package com.realestate.common;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public final class InputRules {
    private InputRules() { }
    public static ModuleException invalid(String message) {
        return new ModuleException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }
    public static void allow(boolean allowed) {
        if (!allowed) throw new ModuleException(HttpStatus.FORBIDDEN, "FORBIDDEN", "You do not have permission for this action.");
    }
    public static void conflict(boolean condition, String message) {
        if (condition) throw new ModuleException(HttpStatus.CONFLICT, "STATE_CONFLICT", message);
    }
    public static String text(String value, String field, int max) {
        if (value == null || value.trim().isEmpty()) throw invalid(field + " is required.");
        String result = value.trim();
        if (result.length() > max) throw invalid(field + " is too long.");
        return result;
    }
    public static String optional(String value, int max) {
        if (value == null) return null;
        String result = value.trim();
        if (result.length() > max) throw invalid("Optional text is too long.");
        return result;
    }
    public static String id(String value) {
        String result = text(value, "Record ID", 36);
        try {
            if (!UUID.fromString(result).toString().equalsIgnoreCase(result)) throw invalid("A UUID record ID is required.");
        } catch (IllegalArgumentException e) { throw invalid("A UUID record ID is required."); }
        return result;
    }
    public static String choice(String value, String... choices) {
        if (value != null) {
            for (String choice : choices) { if (choice.equals(value)) return value; }
        }
        throw invalid("Unsupported or missing choice.");
    }
    public static String email(String value) {
        String result = text(value, "Email", 254);
        if (!result.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) throw invalid("Enter a valid email address.");
        return result;
    }
    public static BigDecimal money(String value) {
        String raw = text(value, "Amount", 40);
        if (!raw.matches("[0-9]+(\\.[0-9]{1,2})?")) throw invalid("Use a positive decimal amount with at most two decimal places.");
        BigDecimal amount = new BigDecimal(raw);
        if (amount.signum() <= 0 || amount.compareTo(new BigDecimal("10000000000000")) >= 0) throw invalid("Amount is outside the allowed range.");
        return amount.setScale(2);
    }
}
