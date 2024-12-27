package com.example.demo.Other;

import java.io.PrintStream;
import org.springframework.stereotype.Component;

@Component
public class PrintStreamColor extends PrintStream {

    // ANSI color codes
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";

    // Constructor that wraps another PrintStream (e.g., System.out)
    public PrintStreamColor(PrintStream original) {
        super(original);
    }

    // Override println to add color
    public void printlnRed(String message) {
        super.println(RED + message + RESET);
    }

    public void printlnGreen(String message) {
        System.out.println("DEBUG: printlnGreen called");
        super.println(GREEN + message + RESET);
    }

    public void printlnBlue(String message) {
        super.println(BLUE + message + RESET);
    }

}
