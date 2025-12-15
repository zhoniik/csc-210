package com.example.geometry;

/**
 * An immutable axis-aligned rectangle defined by width and height.
 *
 * <p>Demonstrates another implementation of the {@link Shape} API.</p>
 *
 * @author ChatGPT
 * @since 1.0
 */
public final class Rectangle implements Shape {
    private final double width;
    private final double height;

    /**
     * Creates a rectangle.
     *
     * @param width  the width (must be {@code >= 0})
     * @param height the height (must be {@code >= 0})
     * @throws IllegalArgumentException if any dimension is negative
     */
    public Rectangle(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width/height must be >= 0");
        }
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width of this rectangle.
     *
     * @return the width (non-negative)
     */
    public double width() {
        return width;
    }

    /**
     * Returns the height of this rectangle.
     *
     * @return the height (non-negative)
     */
    public double height() {
        return height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public String describe() {
        return "Rectangle[w=" + width + ", h=" + height + "]";
    }
}
