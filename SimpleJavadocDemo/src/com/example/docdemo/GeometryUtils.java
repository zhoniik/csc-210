package com.example.docdemo;

/**
 * A utility class for common geometry calculations.
 * <p>
 * Demonstrates the use of static methods and constants
 * in Javadoc documentation.
 * </p>
 *
 * @author Matthew
 * @version 1.0
 * @see Calculator
 */
public final class GeometryUtils {

    /**
     * The constant value of π used for geometric calculations.
     */
    public static final double PI = 3.141592653589793;

    // Private constructor to prevent instantiation
    private GeometryUtils() {}

    /**
     * Computes the area of a circle using the formula
     * <em>π × radius²</em>.
     *
     * @param radius Radius of the circle (must be non-negative)
     * @return The area of the circle
     * @throws IllegalArgumentException If the radius is negative
     */
    public static double circleArea(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        return PI * radius * radius;
    }
}

