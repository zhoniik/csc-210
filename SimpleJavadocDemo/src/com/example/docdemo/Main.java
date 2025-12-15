package com.example.docdemo;

/**
 * Entry point of the documentation demo application.
 * <p>
 * This class demonstrates how Javadoc comments can be
 * used to generate HTML documentation for multiple classes.
 * </p>
 *
 * @author Matthew
 * @version 1.0
 * @see Calculator
 * @see GeometryUtils
 */
public class Main {
    /**
     * Main method to run the demo.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        double result = calc.add(5, 3);
        System.out.println("5 + 3 = " + result);

        double area = GeometryUtils.circleArea(2.5);
        System.out.println("Area of circle with radius 2.5 = " + area);
    }
}

