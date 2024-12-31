package com.example.demo.Other;

import org.springframework.stereotype.Component;

@Component
public class PrintStreamColor {

    // ANSI color codes
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";

    // Constructor that wraps another PrintStream (e.g., System.out)
    public PrintStreamColor() {
        // Wrapping System.out to add color
    }

    // Override println to add color
    public void printlnRed(String message) {
        System.out.println(RED + message + RESET + "\r\n");
    }

    public void printlnGreen(String message) {
        System.out.println(GREEN + message + RESET + "\r\n");
    }
}
