package com.example.geometry;

/**
 * A geometric shape with an area and perimeter.
 *
 * <p>This interface showcases a simple, reusable API that multiple
 * implementations can satisfy.</p>
 *
 * @author ChatGPT
 * @since 1.0
 */
public interface Shape {
    /**
     * Computes the area of this shape.
     *
     * @return the area (non-negative)
     */
    double area();

    /**
     * Computes the perimeter (a.k.a. circumference) of this shape.
     *
     * @return the perimeter (non-negative)
     */
    double perimeter();

    /**
     * Provides a short human-readable description.
     *
     * @return a short description (never {@code null})
     */
    String describe();
}
