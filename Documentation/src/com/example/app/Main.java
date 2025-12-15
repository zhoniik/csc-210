package com.example.app;

import com.example.geometry.Circle;
import com.example.geometry.Rectangle;
import com.example.geometry.Shape;
import com.example.util.Strings;

import java.util.List;

/**
 * Demo application that exercises the geometry model.
 *
 * <p>Run this class from the command line to see formatted output.</p>
 *
 * @author ChatGPT
 * @version 1.0
 * @since 1.0
 */
public final class Main {
    /**
     * Program entry point.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        List<Shape> shapes = List.of(
                new Circle(3),
                new Rectangle(4, 2),
                new Circle(0.5),
                new Rectangle(10, 0.75)
        );

        System.out.println("Shapes Demo");
        System.out.println("-----------");
        for (Shape s : shapes) {
            String name = Strings.leftPad(s.describe(), ' ', 22);
            System.out.printf("%s  |  area=%8.3f  perimeter=%8.3f%n",
                    name, s.area(), s.perimeter());
        }
    }
}
