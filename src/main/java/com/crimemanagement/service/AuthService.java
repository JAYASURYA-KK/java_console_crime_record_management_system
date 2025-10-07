package com.crimemanagement.service;

import com.crimemanagement.config.DatabaseConfig;
import com.crimemanagement.model.User;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class AuthService {
    private MongoCollection<Document> usersCollection;
    private User currentUser;
    
    public AuthService() {
        this.usersCollection = DatabaseConfig.getUsersCollection();
    }
    
    public boolean login(String email, String password) {
        try {
            Document query = new Document("email", email)
                    .append("password", password);
            
            Document userDoc = usersCollection.find(query).first();
            
            if (userDoc != null) {
                currentUser = new User(
                    userDoc.getString("email"),
                    userDoc.getString("password"),
                    userDoc.getString("role")
                );
                System.out.println("Login successful! Welcome, " + email);
                return true;
            } else {
                System.out.println("Invalid email or password!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }
    
    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getEmail() + "!");
            currentUser = null;
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean hasPermission(String action) {
        if (currentUser == null) {
            return false;
        }
        
        String role = currentUser.getRole();
        
        switch (action) {
            case "ADD_USER":
            case "MANAGE_USERS":
                return "admin".equals(role);
                
            case "ADD_CRIME":
            case "EDIT_CRIME":
            case "DELETE_CRIME":
                return "admin".equals(role) || "special".equals(role);
                
            case "VIEW_CRIMES":
            case "SEARCH_CRIMES":
                return true; // All users can view and search
                
            default:
                return false;
        }
    }
    
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}
