package com.example.app;

import com.example.math.Calculator;
import com.example.util.Printer;

public class Main {
    public static void main(String[] args) {
        Printer.printHeader("Simple Multi-Package Demo");

        Calculator calc = new Calculator();
        int sum = calc.add(3, 4);
        int product = calc.multiply(3, 4);

        Printer.printResult("Sum", sum);
        Printer.printResult("Product", product);
    }
}

