package com.crimemanagement.exception;

public class CrimeManagementException extends Exception {
    private String errorCode;
    private String userMessage;
    
    public CrimeManagementException(String message) {
        super(message);
        this.userMessage = message;
    }
    
    public CrimeManagementException(String message, Throwable cause) {
        super(message, cause);
        this.userMessage = message;
    }
    
    public CrimeManagementException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public CrimeManagementException(String errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage != null ? userMessage : getMessage();
    }
}
