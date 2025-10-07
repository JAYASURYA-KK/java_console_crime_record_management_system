package com.crimemanagement.service;

import com.crimemanagement.config.DatabaseConfig;
import com.crimemanagement.model.Crime;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class CrimeService {
    private MongoCollection<Document> crimesCollection;
    private ArrayList<Crime> crimeList; // In-memory storage
    private Stack<Crime> crimeStack; // For latest-first display
    private WebSocketService webSocketService;
    
    public CrimeService() {
        this(null);
    }

    public CrimeService(WebSocketService webSocketService) {
        this.crimesCollection = DatabaseConfig.getCrimesCollection();
        this.crimeList = new ArrayList<>();
        this.crimeStack = new Stack<>();
        this.webSocketService = webSocketService;
        
        // Register this instance with the shared holder only if not already set (preserve console instance)
        if (SharedServiceHolder.getCrimeService() == null) {
            SharedServiceHolder.setCrimeService(this);
        }
        
        // If no WebSocket service was provided, try to get it from the shared holder
        if (this.webSocketService == null) {
            this.webSocketService = SharedServiceHolder.getWebSocketService();
        }
        
        loadCrimesFromDatabase();
    }
    
    public void setWebSocketService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
        if (webSocketService != null) {
            SharedServiceHolder.setWebSocketService(webSocketService);
        }
    }
    public boolean addCrime(String name, String city, String crimeType, String details, String photoPath) {
        try {
            Crime crime = new Crime(name, city, crimeType, details, photoPath);
            
            // Save to MongoDB
            Document crimeDoc = new Document("name", name)
                    .append("city", city)
                    .append("crimeType", crimeType)
                    .append("details", details)
                    .append("photoPath", photoPath)
                    .append("createdAt", Date.from(crime.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
            
            crimesCollection.insertOne(crimeDoc);
            
            // Set the generated ID
            crime.setId(crimeDoc.getObjectId("_id").toString());
            
            // Add to in-memory structures
            crimeList.add(crime);
            crimeStack.push(crime);
            
            System.out.println("Crime record added successfully!");
            System.out.println("Record ID: " + crime.getId());
            
            // Notify web clients through WebSocket if service is available
            if (webSocketService != null) {
                webSocketService.notifyCrimeUpdate();
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error adding crime record: " + e.getMessage());
            return false;
        }
    }
    
    public boolean editCrime(String crimeId, String name, String city, String crimeType, String details, String photoPath) {
        try {
            ObjectId objectId = new ObjectId(crimeId);
            Document query = new Document("_id", objectId);
            
            Document update = new Document("$set", new Document("name", name)
                    .append("city", city)
                    .append("crimeType", crimeType)
                    .append("details", details)
                    .append("photoPath", photoPath));
            
            long modifiedCount = crimesCollection.updateOne(query, update).getModifiedCount();
            
            if (modifiedCount > 0) {
                // Update in-memory structures
                updateInMemoryStructures(crimeId, name, city, crimeType, details, photoPath);
                System.out.println("Crime record updated successfully!");
                if (webSocketService != null) {
                    webSocketService.notifyCrimeUpdate();
                }
                return true;
            } else {
                System.out.println("Crime record not found!");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error updating crime record: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteCrime(String crimeId) {
        try {
            ObjectId objectId = new ObjectId(crimeId);
            Document query = new Document("_id", objectId);
            
            long deletedCount = crimesCollection.deleteOne(query).getDeletedCount();
            
            if (deletedCount > 0) {
                // Remove from in-memory structures
                removeFromInMemoryStructures(crimeId);
                System.out.println("Crime record deleted successfully!");
                // Notify web clients through WebSocket if service is available
                if (webSocketService != null) {
                    webSocketService.notifyCrimeUpdate();
                }
                return true;
            } else {
                System.out.println("Crime record not found!");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error deleting crime record: " + e.getMessage());
            return false;
        }
    }
    
    public List<Crime> getAllCrimes() {
        return new ArrayList<>(crimeList);
    }
    
    public Stack<Crime> getCrimesStack() {
        // Return a copy to prevent external modification
        Stack<Crime> stackCopy = new Stack<>();
        stackCopy.addAll(crimeStack);
        return stackCopy;
    }
    
    public Crime getCrimeById(String crimeId) {
        try {
            ObjectId objectId = new ObjectId(crimeId);
            Document query = new Document("_id", objectId);
            Document crimeDoc = crimesCollection.find(query).first();
            
            if (crimeDoc != null) {
                return documentToCrime(crimeDoc);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving crime record: " + e.getMessage());
        }
        return null;
    }
    
    public void displayAllCrimes() {
        if (crimeStack.isEmpty()) {
            System.out.println("No crime records found.");
            return;
        }
        
        System.out.println("\n=== CRIME RECORDS (Latest First) ===");
        Stack<Crime> tempStack = new Stack<>();
        tempStack.addAll(crimeStack);
        
        int count = 1;
        while (!tempStack.isEmpty()) {
            Crime crime = tempStack.pop();
            System.out.println("\n" + count + ". Crime Record:");
            displayCrimeDetails(crime);
            count++;
        }
    }
    
    public void displayCrimeDetails(Crime crime) {
        System.out.println("   ID: " + crime.getId());
        System.out.println("   Name: " + crime.getName());
        System.out.println("   City: " + crime.getCity());
        System.out.println("   Crime Type: " + crime.getCrimeType());
        System.out.println("   Details: " + crime.getDetails());
        System.out.println("   Photo Path: " + crime.getPhotoPath());
        System.out.println("   Created: " + crime.getCreatedAt());
        System.out.println("   " + "-".repeat(50));
    }
    
    private void loadCrimesFromDatabase() {
        try {
            crimeList.clear();
            crimeStack.clear();
            
            // Load crimes sorted by creation date (newest first)
            for (Document doc : crimesCollection.find().sort(Sorts.descending("createdAt"))) {
                Crime crime = documentToCrime(doc);
                crimeList.add(crime);
                crimeStack.push(crime);
            }
            
            System.out.println("Loaded " + crimeList.size() + " crime records from database.");
            
        } catch (Exception e) {
            System.err.println("Error loading crimes from database: " + e.getMessage());
        }
    }
    
    private Crime documentToCrime(Document doc) {
        Crime crime = new Crime();
        crime.setId(doc.getObjectId("_id").toString());
        crime.setName(doc.getString("name"));
        crime.setCity(doc.getString("city"));
        crime.setCrimeType(doc.getString("crimeType"));
        crime.setDetails(doc.getString("details"));
        crime.setPhotoPath(doc.getString("photoPath"));
        
        Date createdAt = doc.getDate("createdAt");
        if (createdAt != null) {
            crime.setCreatedAt(LocalDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault()));
        }
        
        return crime;
    }
    
    private void updateInMemoryStructures(String crimeId, String name, String city, String crimeType, String details, String photoPath) {
        // Update in ArrayList
        for (Crime crime : crimeList) {
            if (crime.getId().equals(crimeId)) {
                crime.setName(name);
                crime.setCity(city);
                crime.setCrimeType(crimeType);
                crime.setDetails(details);
                crime.setPhotoPath(photoPath);
                break;
            }
        }
        
        // Update in Stack (recreate stack to maintain order)
        Stack<Crime> newStack = new Stack<>();
        Stack<Crime> tempStack = new Stack<>();
        
        // Reverse the stack to maintain order
        while (!crimeStack.isEmpty()) {
            tempStack.push(crimeStack.pop());
        }
        
        while (!tempStack.isEmpty()) {
            Crime crime = tempStack.pop();
            if (crime.getId().equals(crimeId)) {
                crime.setName(name);
                crime.setCity(city);
                crime.setCrimeType(crimeType);
                crime.setDetails(details);
                crime.setPhotoPath(photoPath);
            }
            newStack.push(crime);
        }
        
        crimeStack = newStack;
    }
    
    private void removeFromInMemoryStructures(String crimeId) {
        // Remove from ArrayList
        crimeList.removeIf(crime -> crime.getId().equals(crimeId));
        
        // Remove from Stack (recreate stack)
        Stack<Crime> newStack = new Stack<>();
        Stack<Crime> tempStack = new Stack<>();
        
        while (!crimeStack.isEmpty()) {
            tempStack.push(crimeStack.pop());
        }
        
        while (!tempStack.isEmpty()) {
            Crime crime = tempStack.pop();
            if (!crime.getId().equals(crimeId)) {
                newStack.push(crime);
            }
        }
        
        crimeStack = newStack;
    }
    
    public int getTotalCrimeCount() {
        return crimeList.size();
    }
    
    public List<Crime> getAllCrimesInFIFOOrder() {
        List<Crime> fifoList = new ArrayList<>();
        Stack<Crime> tempStack = new Stack<>();
        tempStack.addAll(crimeStack);
        
        // Pop from stack to get FIFO order (latest first)
        while (!tempStack.isEmpty()) {
            fifoList.add(tempStack.pop());
        }
        
        return fifoList;
    }
}
