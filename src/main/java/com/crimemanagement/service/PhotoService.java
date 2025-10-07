package com.crimemanagement.service;

import com.crimemanagement.util.InputValidator;

import java.io.File;

public class PhotoService {
    
    public void displayCriminalPhoto(String photoPath) {
        if (photoPath == null || photoPath.trim().isEmpty()) {
            System.out.println("No photo path provided.");
            return;
        }
        
        System.out.println("\n=== CRIMINAL PHOTO INFO ===");
        System.out.println("Photo path: " + photoPath);
        
        File photoFile = new File(photoPath);
        if (!photoFile.exists()) {
            System.out.println("Photo file not found at: " + photoPath);
            return;
        }
        
        if (!isImageFile(photoPath)) {
            System.out.println("Invalid image file format.");
            System.out.println("Supported formats: JPG, JPEG, PNG, GIF, BMP");
            return;
        }
        
        System.out.println("Photo file exists and is valid.");
        System.out.println("File size: " + photoFile.length() + " bytes");
        System.out.println("Note: Photo viewing is available in the web interface.");
    }
    
    public boolean validatePhotoPath(String photoPath) {
        if (photoPath == null || photoPath.trim().isEmpty()) {
            return false;
        }
        
        File photoFile = new File(photoPath);
        return photoFile.exists() && isImageFile(photoPath);
    }
    
    public String getPhotoPathFromUser() {
        while (true) {
            String photoPath = InputValidator.getValidString("Enter photo path (or 'skip' to use default): ", false);
            
            if ("skip".equalsIgnoreCase(photoPath)) {
                return "photos/default.jpg";
            }
            
            if (validatePhotoPath(photoPath)) {
                return photoPath;
            } else {
                System.out.println("Invalid photo path or file not found.");
                if (InputValidator.getYesNoConfirmation("Use default photo instead?")) {
                    return "photos/default.jpg";
                }
                // Continue loop to ask again
            }
        }
    }
    
    private boolean isImageFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".jpg") || 
               lowerPath.endsWith(".jpeg") || 
               lowerPath.endsWith(".png") || 
               lowerPath.endsWith(".gif") || 
               lowerPath.endsWith(".bmp");
    }
}
