package com.crimemanagement;

import com.crimemanagement.config.DatabaseConfig;
import com.crimemanagement.ui.MenuManager;

public class CrimeManagementApplication {
    
    public static void main(String[] args) {
        try {
            // Initialize database connection
            System.out.println("Initializing Crime Record Management System...");
            DatabaseConfig.connect();
            
            // Start the application
            MenuManager menuManager = new MenuManager();
            menuManager.start();
            
        } catch (Exception e) {
            System.err.println("Fatal error starting application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cleanup database connection
            DatabaseConfig.disconnect();
        }
    }
}
