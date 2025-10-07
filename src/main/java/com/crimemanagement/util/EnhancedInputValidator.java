package com.crimemanagement.util;

import com.crimemanagement.exception.ValidationException;

import java.util.Scanner;

public class EnhancedInputValidator extends InputValidator {
    
    public static String getValidatedString(String prompt, String fieldName, int minLength, int maxLength) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validateLength(input, fieldName, minLength, maxLength);
                return ValidationUtils.sanitizeInput(input);
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please try again.");
            }
        }
    }
    
    public static String getValidatedName(String prompt, String fieldName) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validateName(input, fieldName);
                return ValidationUtils.sanitizeInput(input);
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please try again.");
            }
        }
    }
    
    public static String getValidatedUsername(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validateUsername(input);
                return ValidationUtils.sanitizeInput(input);
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please try again.");
            }
        }
    }
    
    public static String getValidatedPassword(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validatePassword(input);
                return input; // Don't sanitize passwords
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please try again.");
            }
        }
    }
    
    public static String getValidatedRole(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validateRole(input);
                return input.trim().toLowerCase();
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Valid roles: admin, special, normal");
                System.out.println("Please try again.");
            }
        }
    }
    
    public static String getValidatedCrimeType(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validateCrimeType(input);
                return ValidationUtils.sanitizeInput(input);
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please try again.");
            }
        }
    }
    
    public static String getValidatedDetails(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validateDetails(input);
                return ValidationUtils.sanitizeInput(input);
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please try again.");
            }
        }
    }
    
    public static String getValidatedPhotoPath(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt, true);
                if (!input.isEmpty()) {
                    ValidationUtils.validatePhotoPath(input);
                }
                return input.isEmpty() ? "photos/default.jpg" : ValidationUtils.sanitizeInput(input);
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please try again or press Enter to use default.");
            }
        }
    }
    
    public static String getValidatedId(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt, false);
                ValidationUtils.validateId(input);
                return input.trim();
            } catch (ValidationException e) {
                System.err.println("Validation Error: " + e.getUserMessage());
                System.out.println("Please enter a valid 24-character ID.");
            }
        }
    }
    
    public static int getSafeInt(String prompt, int min, int max) {
        Scanner scanner = getScanner();
        
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                
                if (!ValidationUtils.isValidInteger(input, min, max)) {
                    System.err.println("Please enter a number between " + min + " and " + max);
                    continue;
                }
                
                return Integer.parseInt(input);
                
            } catch (Exception e) {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    
    public static boolean getSafeConfirmation(String prompt) {
        while (true) {
            try {
                String input = getValidString(prompt + " (y/n): ", false).toLowerCase();
                
                if (input.equals("y") || input.equals("yes")) {
                    return true;
                } else if (input.equals("n") || input.equals("no")) {
                    return false;
                } else {
                    System.err.println("Please enter 'y' for yes or 'n' for no.");
                }
            } catch (Exception e) {
                System.err.println("Invalid input. Please try again.");
            }
        }
    }
}
