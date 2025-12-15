package com.example.library.util;

public final class IsbnValidator {
    private IsbnValidator() {}

    public static String normalize(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("[^0-9Xx]", "");
    }

    /** Accepts ISBN-13 only (digits only after normalize). */
    public static boolean isValidIsbn13(String raw) {
        String s = normalize(raw);
        if (s == null || s.length() != 13 || !s.matches("\\d{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = s.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : 3 * d;
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (s.charAt(12) - '0');
    }
}

