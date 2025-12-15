package com.example.docdemo;

/**
 * A simple calculator for demonstrating Javadoc.
 * <p>
 * This class provides basic arithmetic operations.
 * </p>
 *
 * @author Matthew
 * @version 1.0
 */
public class Calculator {

    /**
     * Adds two numbers.
     *
     * @param a First number
     * @param b Second number
     * @return The sum of {@code a} and {@code b}
     */
    public double add(double a, double b) {
        return a + b;
    }

    /**
     * Divides one number by another.
     *
     * @param a Numerator
     * @param b Denominator
     * @return Result of division {@code a / b}
     * @throws ArithmeticException If {@code b} is zero
     */
    public double divide(double a, double b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }
}

