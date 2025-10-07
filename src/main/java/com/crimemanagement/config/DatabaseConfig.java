package com.crimemanagement.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class DatabaseConfig {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "crime_db";
    
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    public static void connect() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("Connected to MongoDB successfully!");
            
            // Initialize collections if they don't exist
            initializeCollections();
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    public static MongoDatabase getDatabase() {
        if (database == null) {
            connect();
        }
        return database;
    }
    
    public static MongoCollection<Document> getUsersCollection() {
        return getDatabase().getCollection("users");
    }
    
    public static MongoCollection<Document> getCrimesCollection() {
        return getDatabase().getCollection("crimes");
    }
    
    private static void initializeCollections() {
        // Create default admin user if not exists
        MongoCollection<Document> users = getUsersCollection();
        Document adminQuery = new Document("username", "admin");
        
        if (users.find(adminQuery).first() == null) {
            Document adminUser = new Document("username", "admin")
                    .append("password", "admin123")
                    .append("role", "admin");
            users.insertOne(adminUser);
            System.out.println("Default admin user created (username: admin, password: admin123)");
        }
    }
    
    public static void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Disconnected from MongoDB");
        }
    }
}
