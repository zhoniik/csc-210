package com.example;

import com.example.util.MathUtils;
import com.example.util.StringUtils;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Utility Demo ===");

        int n = 5;
        System.out.println("Square of " + n + " = " + MathUtils.square(n));

        double avg = MathUtils.average(4, 8, 15, 16, 23, 42);
        System.out.println("Average = " + avg);

        String word = "racecar";
        System.out.println("Is '" + word + "' a palindrome? " + StringUtils.isPalindrome(word));

        String name = "matthew";
        System.out.println("Capitalized: " + StringUtils.capitalize(name));
    }
}

