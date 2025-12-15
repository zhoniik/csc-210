package com.example.geometry;

/**
 * An immutable circle defined by a radius in linear units.
 *
 * <p>Instances validate the radius at construction time to maintain
 * class invariants.</p>
 *
 * @author ChatGPT
 * @since 1.0
 */
public final class Circle implements Shape {
    private final double radius;

    /**
     * Creates a circle with the given radius.
     *
     * @param radius the radius (must be {@code >= 0})
     * @throws IllegalArgumentException if {@code radius < 0}
     */
    public Circle(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be >= 0");
        }
        this.radius = radius;
    }

    /**
     * Returns the radius of this circle.
     *
     * @return the radius (non-negative)
     */
    public double radius() {
        return radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public String describe() {
        return "Circle[r=" + radius + "]";
    }
}
