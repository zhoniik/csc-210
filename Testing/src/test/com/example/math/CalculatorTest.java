package com.example.math;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    Calculator calc;

    @BeforeEach
    void setUp() { calc = new Calculator(); }

    @Test
    void addsNumbers() {
        assertEquals(7, calc.add(3, 4));
    }

    @Test
    void subtractsNumbers() {
        assertEquals(-1, calc.sub(3, 4));
    }

    @Test
    void dividesNumbers() {
        assertEquals(2, calc.div(8, 4));
    }

    @Test
    void divisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> calc.div(1, 0));
    }
}
