package com.example.library.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IsbnValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "9780306406157",
            "9780134685991",
            "9780596009205",
            "9780262033848"
    })
    @DisplayName("Valid ISBN-13s should pass")
    void validIsbn13s(String isbn) {
        assertTrue(IsbnValidator.isValidIsbn13(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "9780306406158",     // wrong check digit
            "978013468599",      // too short
            "97801346859911",    // too long
            "ABC-0134685991"     // not numeric
    })
    @DisplayName("Invalid ISBN-13s should fail")
    void invalidIsbn13s(String isbn) {
        assertFalse(IsbnValidator.isValidIsbn13(isbn));
    }

    @Test
    void normalizeRemovesDashesAndSpaces() {
        assertEquals("9780306406157", IsbnValidator.normalize("978-0-306-40615-7"));
        assertEquals("9780306406157", IsbnValidator.normalize(" 978 0306 406157 "));
    }
}

