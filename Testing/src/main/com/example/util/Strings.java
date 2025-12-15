package com.example.util;

public class Strings {
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    public static String reverse(String s) {
        if (s == null) return null;
        return new StringBuilder(s).reverse().toString();
    }
}

