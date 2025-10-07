package com.crimemanagement.util;

import java.util.Scanner;

public class InputValidator {
    private static Scanner scanner = new Scanner(System.in);
    
    public static String getValidString(String prompt, boolean allowEmpty) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            
            if (!allowEmpty && input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (!allowEmpty && input.isEmpty());
        
        return input;
    }
    
    public static int getValidInt(String prompt, int min, int max) {
        int input;
        do {
            try {
                System.out.print(prompt);
                input = Integer.parseInt(scanner.nextLine().trim());
                
                if (input < min || input > max) {
                    System.out.println("Please enter a number between " + min + " and " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                input = min - 1; // Force loop to continue
            }
        } while (input < min || input > max);
        
        return input;
    }
    
    public static String getValidRole() {
        String role;
        do {
            System.out.print("Enter role (admin/special/normal): ");
            role = scanner.nextLine().trim().toLowerCase();
            
            if (!role.equals("admin") && !role.equals("special") && !role.equals("normal")) {
                System.out.println("Invalid role! Please enter: admin, special, or normal");
            }
        } while (!role.equals("admin") && !role.equals("special") && !role.equals("normal"));
        
        return role;
    }
    
    public static boolean getYesNoConfirmation(String prompt) {
        String input;
        do {
            System.out.print(prompt + " (y/n): ");
            input = scanner.nextLine().trim().toLowerCase();
            
            if (!input.equals("y") && !input.equals("n") && 
                !input.equals("yes") && !input.equals("no")) {
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
        } while (!input.equals("y") && !input.equals("n") && 
                 !input.equals("yes") && !input.equals("no"));
        
        return input.equals("y") || input.equals("yes");
    }
    
    public static Scanner getScanner() {
        return scanner;
    }
}
