package com.example.docs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    @Test
    void add_works() {
        Calculator c = new Calculator();
        assertEquals(7, c.add(3, 4));
    }

    @Test
    void divide_works() {
        Calculator c = new Calculator();
        assertEquals(2, c.divide(10, 5));
    }

    @Test
    void divide_by_zero_throws() {
        Calculator c = new Calculator();
        assertThrows(IllegalArgumentException.class, () -> c.divide(1, 0));
    }
}

