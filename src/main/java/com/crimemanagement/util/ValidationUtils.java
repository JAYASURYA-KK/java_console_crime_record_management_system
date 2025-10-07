package com.crimemanagement.util;

import com.crimemanagement.exception.ValidationException;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-z\\s]{2,50}$"
    );
    
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "cannot be empty");
        }
    }
    
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) throws ValidationException {
        validateNotEmpty(value, fieldName);
        
        int length = value.trim().length();
        if (length < minLength || length > maxLength) {
            throw new ValidationException(fieldName, 
                String.format("must be between %d and %d characters", minLength, maxLength));
        }
    }
    
    public static void validateName(String name, String fieldName) throws ValidationException {
        validateNotEmpty(name, fieldName);
        
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            throw new ValidationException(fieldName, "must contain only letters and spaces (2-50 characters)");
        }
    }
    
    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "email");
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("email", "must be a valid email address");
        }
    }
    
    public static void validatePhone(String phone) throws ValidationException {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new ValidationException("phone", "must be a valid phone number (10-15 digits)");
            }
        }
    }
    
    public static void validateRole(String role) throws ValidationException {
        validateNotEmpty(role, "role");
        
        String normalizedRole = role.trim().toLowerCase();
        if (!normalizedRole.equals("admin") && !normalizedRole.equals("special") && !normalizedRole.equals("normal")) {
            throw new ValidationException("role", "must be 'admin', 'special', or 'normal'");
        }
    }
    
    public static void validateCrimeType(String crimeType) throws ValidationException {
        validateNotEmpty(crimeType, "crime type");
        validateLength(crimeType, "crime type", 2, 50);
    }
    
    public static void validateCity(String city) throws ValidationException {
        validateName(city, "city");
    }
    
    public static void validateCriminalName(String name) throws ValidationException {
        validateName(name, "criminal name");
    }
    
    public static void validateDetails(String details) throws ValidationException {
        validateNotEmpty(details, "details");
        validateLength(details, "details", 10, 500);
    }
    
    public static void validatePhotoPath(String photoPath) throws ValidationException {
        if (photoPath != null && !photoPath.trim().isEmpty()) {
            String path = photoPath.trim();
            
            // Check file extension
            String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
            boolean validExtension = false;
            
            for (String ext : validExtensions) {
                if (path.toLowerCase().endsWith(ext)) {
                    validExtension = true;
                    break;
                }
            }
            
            if (!validExtension) {
                throw new ValidationException("photo path", "must have a valid image extension (.jpg, .jpeg, .png, .gif, .bmp)");
            }
        }
    }
    
    public static void validateUsername(String username) throws ValidationException {
        validateNotEmpty(username, "username");
        validateLength(username, "username", 3, 20);
        
        // Username should contain only alphanumeric characters and underscores
        if (!Pattern.matches("^[a-zA-Z0-9_]+$", username.trim())) {
            throw new ValidationException("username", "must contain only letters, numbers, and underscores");
        }
    }
    
    public static void validatePassword(String password) throws ValidationException {
        validateNotEmpty(password, "password");
        validateLength(password, "password", 6, 50);
        
        // Password should contain at least one letter and one number
        if (!Pattern.matches(".*[a-zA-Z].*", password) || !Pattern.matches(".*[0-9].*", password)) {
            throw new ValidationException("password", "must contain at least one letter and one number");
        }
    }
    
    public static void validateId(String id) throws ValidationException {
        validateNotEmpty(id, "ID");
        
        // MongoDB ObjectId validation (24 hex characters)
        if (!Pattern.matches("^[a-fA-F0-9]{24}$", id.trim())) {
            throw new ValidationException("ID", "must be a valid 24-character hexadecimal string");
        }
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove potentially dangerous characters
        return input.trim()
                   .replaceAll("[<>\"'&]", "")  // Remove HTML/XML characters
                   .replaceAll("\\s+", " ");    // Normalize whitespace
    }
    
    public static boolean isValidInteger(String input, int min, int max) {
        try {
            int value = Integer.parseInt(input.trim());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static void displayValidationRules() {
        System.out.println("\n=== INPUT VALIDATION RULES ===");
        System.out.println();
        System.out.println("Username:");
        System.out.println("  • 3-20 characters");
        System.out.println("  • Letters, numbers, and underscores only");
        System.out.println();
        System.out.println("Password:");
        System.out.println("  • 6-50 characters");
        System.out.println("  • Must contain at least one letter and one number");
        System.out.println();
        System.out.println("Names (Criminal, City):");
        System.out.println("  • 2-50 characters");
        System.out.println("  • Letters and spaces only");
        System.out.println();
        System.out.println("Crime Details:");
        System.out.println("  • 10-500 characters");
        System.out.println("  • Cannot be empty");
        System.out.println();
        System.out.println("Photo Path:");
        System.out.println("  • Must have valid image extension (.jpg, .jpeg, .png, .gif, .bmp)");
        System.out.println("  • Optional field");
        System.out.println();
        System.out.println("Role:");
        System.out.println("  • Must be 'admin', 'special', or 'normal'");
        System.out.println("=".repeat(50));
    }
}
