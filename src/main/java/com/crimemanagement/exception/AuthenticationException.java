package com.crimemanagement.exception;

public class AuthenticationException extends CrimeManagementException {
    
    public AuthenticationException(String message) {
        super("AUTH_ERROR", message, "Authentication failed. Please check your credentials.");
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super("AUTH_ERROR", message, "Authentication failed. Please check your credentials.", cause);
    }
}
