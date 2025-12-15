package com.example.docs;

/**
 * A tiny arithmetic utility used to demonstrate Javadoc.
 * <p>
 * Methods are intentionally simple for readable documentation and tests.
 *
 * @author You
 * @since 1.0
 */
public class Calculator {

    /**
     * Adds two integers.
     *
     * @param a first addend
     * @param b second addend
     * @return the sum a + b
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * Divides two integers (integer division).
     *
     * @param numerator the numerator
     * @param denominator the denominator (must not be zero)
     * @return the quotient {@code numerator / denominator}
     * @throws IllegalArgumentException if {@code denominator == 0}
     */
    public int divide(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("denominator must not be zero");
        }
        return numerator / denominator;
    }
}

