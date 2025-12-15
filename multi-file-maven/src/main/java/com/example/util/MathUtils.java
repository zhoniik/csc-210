package com.example.util;

public class MathUtils {
    public static int square(int n) {
        return n * n;
    }

    public static double average(double... values) {
        double sum = 0;
        for (double v : values) sum += v;
        return values.length == 0 ? 0 : sum / values.length;
    }
}

