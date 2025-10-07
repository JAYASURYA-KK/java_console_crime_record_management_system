package com.crimemanagement.util;

import java.io.IOException;

public class SystemUtils {
    
    public static void clearScreen() {
        try {
            // For Windows
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } 
            // For Unix/Linux/Mac
            else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            // If clearing fails, just print some newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    public static void printSeparator() {
        System.out.println("=".repeat(80));
    }
    
    public static void printHeader(String title) {
        printSeparator();
        System.out.println(centerText(title, 80));
        printSeparator();
    }
    
    public static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        
        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        
        return sb.toString();
    }
    
    public static void displayWelcomeMessage() {
        clearScreen();
        printHeader("CRIME RECORD MANAGEMENT SYSTEM");
        System.out.println("Version 1.0");
        System.out.println("Developed with Java + MongoDB");
        System.out.println();
        System.out.println("Features:");
        System.out.println("• Role-based access control (Admin, Special User, Normal User)");
        System.out.println("• Complete CRUD operations for crime records");
        System.out.println("• Advanced search functionality");
        System.out.println("• ASCII art photo display");
        System.out.println("• Data structure demonstrations");
        System.out.println("• MongoDB integration");
        printSeparator();
    }
    
    public static void displaySystemInfo() {
        System.out.println("\n=== SYSTEM INFORMATION ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Operating System: " + System.getProperty("os.name"));
        System.out.println("User Directory: " + System.getProperty("user.dir"));
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max Memory: " + formatBytes(Runtime.getRuntime().maxMemory()));
        System.out.println("Free Memory: " + formatBytes(Runtime.getRuntime().freeMemory()));
        System.out.println("Total Memory: " + formatBytes(Runtime.getRuntime().totalMemory()));
        printSeparator();
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    public static void displayHelpMenu() {
        System.out.println("\n=== HELP MENU ===");
        System.out.println("User Roles and Permissions:");
        System.out.println();
        System.out.println("ADMIN:");
        System.out.println("  • Add/Edit/Delete users");
        System.out.println("  • Add/Edit/Delete crime records");
        System.out.println("  • View/Search crime records");
        System.out.println("  • Access all system features");
        System.out.println();
        System.out.println("SPECIAL USER:");
        System.out.println("  • Add/Edit/Delete crime records");
        System.out.println("  • View/Search crime records");
        System.out.println("  • View statistics and photos");
        System.out.println();
        System.out.println("NORMAL USER:");
        System.out.println("  • View/Search crime records only");
        System.out.println("  • View statistics and photos");
        System.out.println();
        System.out.println("Default Login Credentials:");
        System.out.println("  Username: admin");
        System.out.println("  Password: admin123");
        printSeparator();
    }
}
