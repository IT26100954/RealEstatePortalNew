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
        int at = result.indexOf('@');
        if (at <= 0 || at != result.lastIndexOf('@')) throw invalid("Enter a valid email address.");
        String local = result.substring(0, at);
        String domain = result.substring(at + 1);
        if (local.length() > 64 || !local.matches("[A-Za-z0-9!#$%&'*+/=?^_`{|}~.-]+") ||
            local.startsWith(".") || local.endsWith(".") || local.contains("..")) throw invalid("Enter a valid email address.");
        String[] labels = domain.split("\\.", -1);
        if (labels.length < 2) throw invalid("Enter a valid email address.");
        for (String label : labels) {
            if (!label.matches("[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?")) throw invalid("Enter a valid email address.");
        }
        if (!labels[labels.length - 1].matches("[A-Za-z]{2,63}")) throw invalid("Enter a valid email address.");
        return result;
    }
    public static String phone(String value) {
        String result = text(value, "Phone", 20);
        if (!result.matches("\\+?[0-9]{7,15}")) throw invalid("Enter 7 to 15 phone digits, optionally starting with +.");
        return result;
    }
    public static String password(String value) {
        // Never trim a password: spaces in a non-blank password are significant.
        if (value == null || value.length() < 8 || value.length() > 128 ||
            value.matches("[\\s\\p{Z}\\u0085\\uFEFF]*")) throw invalid("Password must contain 8 to 128 characters and cannot be only whitespace.");
        return value;
    }
    public static BigDecimal money(String value) {
        String raw = text(value, "Amount", 40);
        if (!raw.matches("[0-9]+(\\.[0-9]{1,2})?")) throw invalid("Use a positive decimal amount with at most two decimal places.");
        BigDecimal amount = new BigDecimal(raw);
        if (amount.signum() <= 0 || amount.compareTo(new BigDecimal("10000000000000")) >= 0) throw invalid("Amount is outside the allowed range.");
        return amount.setScale(2);
    }
}
