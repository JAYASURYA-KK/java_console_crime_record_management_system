package com.crimemanagement.ui;

import com.crimemanagement.model.Crime;
import com.crimemanagement.service.*;
import com.crimemanagement.util.*;

import java.util.List;
import java.util.Scanner;

public class MenuManager {
    private AuthService authService;
    private UserService userService;
    private CrimeService crimeService;
    private Scanner scanner;
    
    public MenuManager() {
        this.authService = new AuthService();
        this.userService = new UserService();
        this.crimeService = new CrimeService(SharedServiceHolder.getWebSocketService());
        SharedServiceHolder.setCrimeService(this.crimeService);
        this.scanner = new Scanner(System.in);
    }
    
    public void start() {
        try {
            System.out.println("=".repeat(80));
            System.out.println("                     CRIME RECORD MANAGEMENT SYSTEM v2.0");
            System.out.println("=".repeat(80));
            System.out.println(" ██████╗██████╗ ██╗███╗   ███╗███████╗    ██████╗ ███████╗ ██████╗ ██████╗ ██████╗ ██████╗ ███████╗");
            System.out.println("██╔════╝██╔══██╗██║████╗ ████║██╔════╝    ██╔══██╗██╔════╝██╔════╝██╔═══██╗██╔══██╗██╔══██╗██╔════╝");
            System.out.println("██║     ██████╔╝██║██╔████╔██║█████╗      ██████╔╝█████╗  ██║     ██║   ██║██████╔╝██║  ██║███████╗");
            System.out.println("██║     ██╔══██╗██║██║╚██╔╝██║██╔══╝      ██╔══██╗██╔══╝  ██║     ██║   ██║██╔══██╗██║  ██║╚════██║");
            System.out.println("╚██████╗██║  ██║██║██║ ╚═╝ ██║███████╗    ██║  ██║███████╗╚██████╗╚██████╔╝██║  ██║██████╔╝███████║");
            System.out.println(" ╚═════╝╚═╝  ╚═╝╚═╝╚═╝     ╚═╝╚══════╝    ╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝ ╚══════╝");
            System.out.println("=".repeat(80));
            
            System.out.println("Welcome to the Crime Record Management System!");
            System.out.println("Please login to continue...\n");
            
            // Main application loop
            while (true) {
                if (!authService.isLoggedIn()) {
                    if (!showLoginMenu()) {
                        break; // User chose to exit
                    }
                } else {
                    showMainMenu();
                }
            }
            
            WebLauncherService.stopWebInterface();
            System.out.println("Thank you for using Crime Record Management System!");
            System.out.println("Goodbye!");
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    
    private boolean showLoginMenu() {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        
        int choice = InputValidator.getValidInt("Select option (1-2): ", 1, 2);
        
        switch (choice) {
            case 1:
                return performLogin();
            case 2:
                return false; // Exit application
            default:
                return true; // Continue loop
        }
    }
    
    private boolean performLogin() {
        System.out.println("\n=== USER LOGIN ===");
        String username = InputValidator.getValidString("Email: ", false);
        String password = InputValidator.getValidString("Password: ", false);
        
        if (authService.login(username, password)) {
            System.out.println("Login successful! Role: " + authService.getCurrentUserRole());
            return true;
        } else {
            System.out.println("Login failed. Please try again.");
            return true; // Continue to show login menu again
        }
    }
    
    private void showMainMenu() {
        String role = authService.getCurrentUserRole();
        
        switch (role.toLowerCase()) {
            case "admin":
                showAdminMenu();
                break;
            case "special":
                showSpecialUserMenu();
                break;
            case "normal":
                showNormalUserMenu();
                break;
            default:
                System.out.println("Unknown user role. Logging out...");
                authService.logout();
        }
    }
    
    private void showAdminMenu() {
        System.out.println("\n=== ADMIN MENU ===");
        System.out.println("Current user: " + authService.getCurrentUser().getEmail() + " (Admin)");
        System.out.println();
        System.out.println("1. Add User");
        System.out.println("2. Manage Users");
        System.out.println("3. Add Crime Record");
        System.out.println("4. Edit Crime Record");
        System.out.println("5. Delete Crime Record");
        System.out.println("6. Crime Record View (Web Interface)");
        System.out.println("7. Logout");
        
        int choice = InputValidator.getValidInt("Select option (1-7): ", 1, 7);
        handleAdminChoice(choice);
    }
    
    private void showSpecialUserMenu() {
        System.out.println("\n=== SPECIAL USER MENU ===");
        System.out.println("Current user: " + authService.getCurrentUser().getEmail() + " (Special User)");
        System.out.println();
        System.out.println("1. Add Crime Record");
        System.out.println("2. Edit Crime Record");
        System.out.println("3. Delete Crime Record");
        System.out.println("4. Crime Record View (Web Interface)");
        System.out.println("5. Logout");
        
        int choice = InputValidator.getValidInt("Select option (1-5): ", 1, 5);
        handleSpecialUserChoice(choice);
    }
    
    private void showNormalUserMenu() {
        System.out.println("\n=== NORMAL USER MENU ===");
        System.out.println("Current user: " + authService.getCurrentUser().getEmail() + " (Normal User)");
        System.out.println();
        System.out.println("1. Crime Record View (Web Interface)");
        System.out.println("2. Logout");
        
        int choice = InputValidator.getValidInt("Select option (1-2): ", 1, 2);
        handleNormalUserChoice(choice);
    }
    
    private void handleAdminChoice(int choice) {
        switch (choice) {
            case 1:
                addUser();
                break;
            case 2:
                manageUsers();
                break;
            case 3:
                addCrimeRecord();
                break;
            case 4:
                editCrimeRecord();
                break;
            case 5:
                deleteCrimeRecord();
                break;
            case 6:
                launchWebInterface();
                break;
            case 7:
                authService.logout();
                break;
        }
        
        if (choice != 7) {
            pauseForUser();
        }
    }
    
    private void handleSpecialUserChoice(int choice) {
        switch (choice) {
            case 1:
                addCrimeRecord();
                break;
            case 2:
                editCrimeRecord();
                break;
            case 3:
                deleteCrimeRecord();
                break;
            case 4:
                launchWebInterface();
                break;
            case 5:
                authService.logout();
                break;
        }
        
        if (choice != 5) {
            pauseForUser();
        }
    }
    
    private void handleNormalUserChoice(int choice) {
        switch (choice) {
            case 1:
                launchWebInterface();
                break;
            case 2:
                authService.logout();
                break;
        }
        
        if (choice != 2) {
            pauseForUser();
        }
    }
    
    private void addUser() {
        if (!authService.hasPermission("ADD_USER")) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }
        
        System.out.println("\n=== ADD NEW USER ===");
        String username = InputValidator.getValidString("Enter Email: ", false);
        String password = InputValidator.getValidString("Enter password: ", false);
        String role = InputValidator.getValidRole();
        
        userService.addUser(username, password, role);
    }
    
    private void manageUsers() {
        if (!authService.hasPermission("MANAGE_USERS")) {
            System.out.println("Access denied. Admin privileges required.");
            return;
        }
        
        while (true) {
            System.out.println("\n=== USER MANAGEMENT ===");
            System.out.println("1. View All Users");
            System.out.println("2. Update User Role");
            System.out.println("3. Delete User");
            System.out.println("4. Back to Main Menu");
            
            int choice = InputValidator.getValidInt("Select option (1-4): ", 1, 4);
            
            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    updateUserRole();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    return;
            }
            
            if (choice != 4) {
                pauseForUser();
            }
        }
    }
    
    private void viewAllUsers() {
        System.out.println("\n=== ALL USERS ===");
        List<com.crimemanagement.model.User> users = userService.getAllUsers();
        
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        System.out.printf("%-30s %-10s%n", "Email", "Role");
        System.out.println("-".repeat(40));
        
        for (com.crimemanagement.model.User user : users) {
            System.out.printf("%-30s %-10s%n", user.getEmail(), user.getRole());
        }
    }
    
    private void updateUserRole() {
        String email = InputValidator.getValidString("Enter email to update: ", false);
        String newRole = InputValidator.getValidRole();
        userService.updateUserRole(email, newRole);
    }
    
    private void deleteUser() {
        String email = InputValidator.getValidString("Enter email to delete: ", false);
        if (InputValidator.getYesNoConfirmation("Are you sure you want to delete user '" + email + "'?")) {
            userService.deleteUser(email);
        }
    }
    
    private void addCrimeRecord() {
        if (!authService.hasPermission("ADD_CRIME")) {
            System.out.println("Access denied. Admin or Special User privileges required.");
            return;
        }
        
        Crime crime = CrimeInputHelper.getCrimeInputFromUser();
        crimeService.addCrime(crime.getName(), crime.getCity(), crime.getCrimeType(), 
                             crime.getDetails(), crime.getPhotoPath());
    }
    
    private void editCrimeRecord() {
        if (!authService.hasPermission("EDIT_CRIME")) {
            System.out.println("Access denied. Admin or Special User privileges required.");
            return;
        }
        
        String crimeId = InputValidator.getValidString("Enter Crime ID to edit: ", false);
        Crime existingCrime = crimeService.getCrimeById(crimeId);
        
        if (existingCrime == null) {
            System.out.println("Crime record not found with ID: " + crimeId);
            return;
        }
        
        System.out.println("Current crime record:");
        crimeService.displayCrimeDetails(existingCrime);
        
        CrimeInputHelper.updateCrimeFromUser(existingCrime);
        
        crimeService.editCrime(crimeId, existingCrime.getName(), existingCrime.getCity(),
                              existingCrime.getCrimeType(), existingCrime.getDetails(),
                              existingCrime.getPhotoPath());
    }
    
    private void deleteCrimeRecord() {
        if (!authService.hasPermission("DELETE_CRIME")) {
            System.out.println("Access denied. Admin or Special User privileges required.");
            return;
        }
        
        String crimeId = InputValidator.getValidString("Enter Crime ID to delete: ", false);
        Crime existingCrime = crimeService.getCrimeById(crimeId);
        
        if (existingCrime == null) {
            System.out.println("Crime record not found with ID: " + crimeId);
            return;
        }
        
        System.out.println("Crime record to delete:");
        crimeService.displayCrimeDetails(existingCrime);
        
        if (InputValidator.getYesNoConfirmation("Are you sure you want to delete this record?")) {
            crimeService.deleteCrime(crimeId);
        }
    }
    

    
    private void pauseForUser() {
        try {
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (Exception e) {
            // Handle scanner errors silently
            try {
                // Try to create a new scanner if the old one is closed
                this.scanner = new Scanner(System.in);
            } catch (Exception ex) {
                // If we can't create a new scanner, just continue
            }
        }
    }

    private void launchWebInterface() {
        System.out.println("\n=== CRIME RECORD VIEW (WEB INTERFACE) ===");
        System.out.println("This will open a web browser showing crime records in FIFO order.");
        System.out.println("Features:");
        System.out.println("- View all records with search functionality");
        System.out.println("- Records displayed in FIFO order (latest first)");
        System.out.println("- Real-time updates every 30 seconds");
        System.out.println("- Photo information display");
        
        if (InputValidator.getYesNoConfirmation("Launch web interface?")) {
            WebLauncherService.launchWebInterface();
            System.out.println("\nWeb interface launched!");
            System.out.println("You can continue using the console while the web interface is open.");
            System.out.println("The web page will automatically refresh to show new records.");
        }
    }
}
