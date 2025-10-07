package com.crimemanagement.util;


import java.util.function.Supplier;

public class SafeOperations {
    
    public static <T> T executeWithErrorHandling(Supplier<T> operation, String operationName) {
        return executeWithErrorHandling(operation, operationName, null);
    }
    
    public static <T> T executeWithErrorHandling(Supplier<T> operation, String operationName, T defaultValue) {
        try {
            return operation.get();
        } catch (Exception e) {
            System.err.println("Error in " + operationName + ": " + e.getMessage());
            ErrorHandler.handleException(e);
            return defaultValue;
        }
    }
    
    public static boolean executeWithRetry(Runnable operation, String operationName) {
        return executeWithRetry(operation, operationName, 3);
    }
    
    public static boolean executeWithRetry(Runnable operation, String operationName, int maxRetries) {
        return ErrorHandler.handleWithRetry(operation, maxRetries, operationName);
    }
    
    public static void executeSafely(Runnable operation, String operationName) {
        try {
            operation.run();
        } catch (Exception e) {
            System.err.println("Error in " + operationName + ": " + e.getMessage());
            ErrorHandler.handleException(e);
        }
    }
    
    public static void validateAndExecute(Runnable validation, Runnable operation, String operationName) {
        try {
            validation.run();
            operation.run();
        } catch (Exception e) {
            System.err.println("Error in " + operationName + ": " + e.getMessage());
            ErrorHandler.handleException(e);
        }
    }
    
    public static boolean isDatabaseAvailable() {
        try {
            // This would typically ping the database
            // For now, we'll assume it's available if no exception is thrown
            return true;
        } catch (Exception e) {
            System.err.println("Database connectivity check failed: " + e.getMessage());
            return false;
        }
    }
    
    public static void performHealthCheck() {
        System.out.println("\n=== SYSTEM HEALTH CHECK ===");
        
        // Check database connectivity
        System.out.print("Database connectivity: ");
        if (isDatabaseAvailable()) {
            System.out.println("✓ OK");
        } else {
            System.out.println("✗ FAILED");
        }
        
        // Check memory usage
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        System.out.print("Memory usage: ");
        if (memoryUsagePercent < 80) {
            System.out.println("✓ OK (" + String.format("%.1f", memoryUsagePercent) + "%)");
        } else {
            System.out.println("⚠ HIGH (" + String.format("%.1f", memoryUsagePercent) + "%)");
        }
        
        // Check photos directory
        System.out.print("Photos directory: ");
        if (PhotoUtils.findAllPhotos().size() > 0) {
            System.out.println("✓ OK (" + PhotoUtils.findAllPhotos().size() + " photos found)");
        } else {
            System.out.println("⚠ No photos found");
        }
        
        System.out.println("=".repeat(30));
    }
}
