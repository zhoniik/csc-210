package com.example.util;

import java.util.Objects;

/**
 * String-related helper methods.
 *
 * @author ChatGPT
 * @since 1.0
 */
public final class Strings {
    private Strings() { /* no instances */ }

    /**
     * Pads the given string on the left with the specified character until
     * it reaches the target length.
     *
     * @param s the original string (not {@code null})
     * @param ch the pad character
     * @param targetLen the desired length (must be {@code >= 0})
     * @return a new string with left padding applied; if {@code s} is
     *         already at least {@code targetLen} long, it is returned unchanged
     * @throws NullPointerException if {@code s} is {@code null}
     * @throws IllegalArgumentException if {@code targetLen < 0}
     */
    public static String leftPad(String s, char ch, int targetLen) {
        Objects.requireNonNull(s, "s");
        if (targetLen < 0) throw new IllegalArgumentException("targetLen < 0");
        int diff = targetLen - s.length();
        if (diff <= 0) return s;
        StringBuilder sb = new StringBuilder(targetLen);
        for (int i = 0; i < diff; i++) sb.append(ch);
        sb.append(s);
        return sb.toString();
    }
}
