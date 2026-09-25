package com.loanmanagement.util;

import java.util.Scanner;

public final class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleUtil() {
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
    public static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number, for example 50000 or 12.50");
            }
        }
    }

    public static void heading(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }
}