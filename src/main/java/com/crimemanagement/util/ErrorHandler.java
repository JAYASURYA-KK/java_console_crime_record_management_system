package com.crimemanagement.util;

import com.crimemanagement.exception.CrimeManagementException;
import com.crimemanagement.exception.DatabaseException;
import com.crimemanagement.exception.ValidationException;
import com.crimemanagement.exception.AuthenticationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorHandler {
    
    private static final String LOG_PREFIX = "[ERROR]";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void handleException(Exception e) {
        logError(e);
        
        if (e instanceof CrimeManagementException) {
            handleCrimeManagementException((CrimeManagementException) e);
        } else if (e instanceof FileNotFoundException) {
            handleFileNotFoundException((FileNotFoundException) e);
        } else if (e instanceof IOException) {
            handleIOException((IOException) e);
        } else if (e instanceof NumberFormatException) {
            handleNumberFormatException((NumberFormatException) e);
        } else if (e instanceof IllegalArgumentException) {
            handleIllegalArgumentException((IllegalArgumentException) e);
        } else {
            handleGenericException(e);
        }
    }
    
    private static void handleCrimeManagementException(CrimeManagementException e) {
        System.err.println("System Error: " + e.getUserMessage());
        
        if (e instanceof DatabaseException) {
            System.err.println("Please check your database connection and try again.");
            System.err.println("If the problem persists, contact system administrator.");
        } else if (e instanceof ValidationException) {
            System.err.println("Please correct the input and try again.");
        } else if (e instanceof AuthenticationException) {
            System.err.println("Please verify your username and password.");
        }
    }
    
    private static void handleFileNotFoundException(FileNotFoundException e) {
        System.err.println("File Error: The requested file was not found.");
        System.err.println("File path: " + e.getMessage());
        System.err.println("Please check the file path and ensure the file exists.");
    }
    
    private static void handleIOException(IOException e) {
        System.err.println("I/O Error: " + e.getMessage());
        System.err.println("Please check file permissions and disk space.");
    }
    
    private static void handleNumberFormatException(NumberFormatException e) {
        System.err.println("Input Error: Invalid number format.");
        System.err.println("Please enter a valid number.");
    }
    
    private static void handleIllegalArgumentException(IllegalArgumentException e) {
        System.err.println("Input Error: " + e.getMessage());
        System.err.println("Please check your input and try again.");
    }
    
    private static void handleGenericException(Exception e) {
        System.err.println("Unexpected Error: " + e.getMessage());
        System.err.println("Please try again. If the problem persists, contact support.");
    }
    
    private static void logError(Exception e) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        System.err.println(LOG_PREFIX + " " + timestamp + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
        
        // In a real application, you would write this to a log file
        // For now, we'll just print to stderr
    }
    
    public static boolean handleWithRetry(Runnable operation, int maxRetries, String operationName) {
        int attempts = 0;
        
        while (attempts < maxRetries) {
            try {
                operation.run();
                return true; // Success
            } catch (Exception e) {
                attempts++;
                System.err.println("Attempt " + attempts + " failed for " + operationName + ": " + e.getMessage());
                
                if (attempts >= maxRetries) {
                    System.err.println("Maximum retry attempts reached for " + operationName);
                    handleException(e);
                    return false;
                } else {
                    System.err.println("Retrying in 2 seconds...");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }
        
        return false;
    }
    
    public static void displayErrorHelp() {
        System.out.println("\n=== COMMON ERROR SOLUTIONS ===");
        System.out.println();
        System.out.println("Database Connection Errors:");
        System.out.println("  • Ensure MongoDB is running on localhost:27017");
        System.out.println("  • Check if MongoDB service is started");
        System.out.println("  • Verify network connectivity");
        System.out.println();
        System.out.println("File Not Found Errors:");
        System.out.println("  • Check if the file path is correct");
        System.out.println("  • Ensure the file exists in the specified location");
        System.out.println("  • Verify file permissions");
        System.out.println();
        System.out.println("Input Validation Errors:");
        System.out.println("  • Check for empty or null inputs");
        System.out.println("  • Ensure numeric inputs are valid numbers");
        System.out.println("  • Verify date formats are correct");
        System.out.println();
        System.out.println("Authentication Errors:");
        System.out.println("  • Verify username and password are correct");
        System.out.println("  • Check if user account exists");
        System.out.println("  • Ensure proper user role permissions");
        System.out.println("=".repeat(50));
    }
}
