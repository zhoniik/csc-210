package com.myprog.main;

import com.example.math.*;
import com.example.util.*;

public class Main {
  public static void main(String [] args){
    System.out.println("Hello world!");

    Calculator c = new Calculator();
    System.out.println(c.add(10, 32));
    System.out.println(Strings.isBlank(""));

    System.out.println("Goodbye world!");
  }
}
