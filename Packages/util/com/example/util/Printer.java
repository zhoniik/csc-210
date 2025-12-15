package com.example.util;

public class Printer {
    public static void printHeader(String title) {
        System.out.println("==== " + title + " ====");
    }

    public static void printResult(String label, int value) {
        System.out.println(label + ": " + value);
    }
}

