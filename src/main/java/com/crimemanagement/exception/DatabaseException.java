package com.crimemanagement.exception;

public class DatabaseException extends CrimeManagementException {
    
    public DatabaseException(String message) {
        super("DB_ERROR", message, "Database operation failed. Please try again.");
    }
    
    public DatabaseException(String message, Throwable cause) {
        super("DB_ERROR", message, "Database operation failed. Please try again.", cause);
    }
    
    public DatabaseException(String userMessage, String message, Throwable cause) {
        super("DB_ERROR", message, userMessage, cause);
    }
}
