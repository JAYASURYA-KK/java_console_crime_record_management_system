package com.crimemanagement.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PhotoUtils {
    
    private static final String PHOTOS_DIRECTORY = "photos";
    private static final String[] SUPPORTED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
    
    public static void createPhotosDirectory() {
        File photosDir = new File(PHOTOS_DIRECTORY);
        if (!photosDir.exists()) {
            boolean created = photosDir.mkdirs();
            if (created) {
                System.out.println("Created photos directory: " + PHOTOS_DIRECTORY);
            } else {
                System.err.println("Failed to create photos directory: " + PHOTOS_DIRECTORY);
            }
        }
    }
    
    public static List<String> findAllPhotos() {
        List<String> photoFiles = new ArrayList<>();
        File photosDir = new File(PHOTOS_DIRECTORY);
        
        if (!photosDir.exists() || !photosDir.isDirectory()) {
            return photoFiles;
        }
        
        File[] files = photosDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isImageFile(file.getName())) {
                    photoFiles.add(file.getPath());
                }
            }
        }
        
        return photoFiles;
    }
    
    public static boolean isImageFile(String fileName) {
        if (fileName == null) {
            return false;
        }
        
        String lowerFileName = fileName.toLowerCase();
        for (String extension : SUPPORTED_EXTENSIONS) {
            if (lowerFileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static long getFileSize(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.size(path);
        } catch (IOException e) {
            return -1;
        }
    }
    
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
    
    public static void displayPhotoInfo(String photoPath) {
        File photoFile = new File(photoPath);
        
        if (!photoFile.exists()) {
            System.out.println("Photo file not found: " + photoPath);
            return;
        }
        
        System.out.println("\n=== PHOTO INFORMATION ===");
        System.out.println("File path: " + photoPath);
        System.out.println("File name: " + photoFile.getName());
        System.out.println("File size: " + formatFileSize(photoFile.length()));
        System.out.println("File extension: " + getFileExtension(photoFile.getName()));
        System.out.println("Is image file: " + isImageFile(photoFile.getName()));
        System.out.println("Last modified: " + new java.util.Date(photoFile.lastModified()));
        System.out.println("=".repeat(30));
    }
    
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    public static void setupSamplePhotos() {
        createPhotosDirectory();
        
        System.out.println("\n=== PHOTO SETUP INSTRUCTIONS ===");
        System.out.println("To test the ASCII art feature, add image files to the 'photos' directory:");
        System.out.println("Supported formats: " + String.join(", ", SUPPORTED_EXTENSIONS));
        System.out.println();
        System.out.println("Sample file structure:");
        System.out.println("  photos/");
        System.out.println("    ├── john.jpg");
        System.out.println("    ├── jane.png");
        System.out.println("    ├── mike.gif");
        System.out.println("    └── default.jpg");
        System.out.println();
        System.out.println("You can download sample images from the internet or use your own photos.");
        System.out.println("The system will automatically convert them to ASCII art for display.");
        System.out.println("=".repeat(50));
    }
}
