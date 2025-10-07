package com.crimemanagement.exception;

public class ValidationException extends CrimeManagementException {
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, message);
    }
    
    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", 
              "Validation failed for field '" + field + "': " + message,
              "Invalid " + field + ": " + message);
    }
}
