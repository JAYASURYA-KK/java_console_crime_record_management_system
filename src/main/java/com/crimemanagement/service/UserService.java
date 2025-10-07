package com.crimemanagement.service;

import com.crimemanagement.config.DatabaseConfig;
import com.crimemanagement.model.User;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private MongoCollection<Document> usersCollection;
    
    public UserService() {
        this.usersCollection = DatabaseConfig.getUsersCollection();
    }
    
    public boolean addUser(String username, String password, String role) {
        try {
            // Check if user already exists
            Document existingUser = usersCollection.find(new Document("email", username)).first();
            if (existingUser != null) {
                System.out.println("User with username '" + username + "' already exists!");
                return false;
            }
            
            // Validate role
            if (!isValidRole(role)) {
                System.out.println("Invalid role! Valid roles are: admin, special, normal");
                return false;
            }
            
            Document newUser = new Document("email", username)
                    .append("password", password)
                    .append("role", role.toLowerCase());
            
            usersCollection.insertOne(newUser);
            System.out.println("User '" + username + "' added successfully with role: " + role);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            for (Document doc : usersCollection.find()) {
                User user = new User(
                    doc.getString("email"),
                    "***", // Don't show passwords
                    doc.getString("role")
                );
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }
    
    public boolean deleteUser(String username) {
        try {
            // Prevent deletion of admin user
            if ("admin".equals(username)) {
                System.out.println("Cannot delete the admin user!");
                return false;
            }
            
            Document query = new Document("email", username);
            long deletedCount = usersCollection.deleteOne(query).getDeletedCount();
            
            if (deletedCount > 0) {
                System.out.println("User '" + username + "' deleted successfully!");
                return true;
            } else {
                System.out.println("User '" + username + "' not found!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateUserRole(String username, String newRole) {
        try {
            if (!isValidRole(newRole)) {
                System.out.println("Invalid role! Valid roles are: admin, special, normal");
                return false;
            }
            
            Document query = new Document("email", username);
            Document update = new Document("$set", new Document("role", newRole.toLowerCase()));
            
            long modifiedCount = usersCollection.updateOne(query, update).getModifiedCount();
            
            if (modifiedCount > 0) {
                System.out.println("User '" + username + "' role updated to: " + newRole);
                return true;
            } else {
                System.out.println("User '" + username + "' not found!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating user role: " + e.getMessage());
            return false;
        }
    }
    
    private boolean isValidRole(String role) {
        return role != null && 
               (role.equalsIgnoreCase("admin") || 
                role.equalsIgnoreCase("special") || 
                role.equalsIgnoreCase("normal"));
    }
}
