package com.example.client;

import com.example.docs.Calculator; // from doc-demo dependency

public class App {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        int a = 12, b = 3;
        System.out.println("a + b = " + calc.add(a, b));
        System.out.println("a / b = " + calc.divide(a, b));
    }
}

