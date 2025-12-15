package com.example.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringsTest {
    @Test
    void blankNullTrue() {
        assertTrue(Strings.isBlank(null));
    }

    @Test
    void blankWhitespaceTrue() {
        assertTrue(Strings.isBlank(" \t\n"));
    }

    @Test
    void reverseWorks() {
        assertEquals("cba", Strings.reverse("abc"));
    }

    @Test
    void reverseNullStaysNull() {
        assertNull(Strings.reverse(null));
    }
}

