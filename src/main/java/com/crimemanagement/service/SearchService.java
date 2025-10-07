package com.crimemanagement.service;

import com.crimemanagement.config.DatabaseConfig;
import com.crimemanagement.model.Crime;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.*;

public class SearchService {
    private MongoCollection<Document> crimesCollection;
    private HashMap<String, List<Crime>> nameIndex;
    private HashMap<String, List<Crime>> cityIndex;
    private HashMap<String, List<Crime>> crimeTypeIndex;
    
    public SearchService() {
        this.crimesCollection = DatabaseConfig.getCrimesCollection();
        this.nameIndex = new HashMap<>();
        this.cityIndex = new HashMap<>();
        this.crimeTypeIndex = new HashMap<>();
        buildSearchIndexes();
    }
    
    public List<Crime> searchByName(String name) {
        System.out.println("Searching for criminal name: " + name);
        
        // First try exact match from HashMap
        List<Crime> exactMatches = nameIndex.get(name.toLowerCase());
        if (exactMatches != null && !exactMatches.isEmpty()) {
            System.out.println("Found " + exactMatches.size() + " exact match(es) in memory cache.");
            return new ArrayList<>(exactMatches);
        }
        
        // If no exact match, search database with partial matching
        return searchInDatabase("name", name);
    }
    
    public List<Crime> searchByCity(String city) {
        System.out.println("Searching for city: " + city);
        
        // First try exact match from HashMap
        List<Crime> exactMatches = cityIndex.get(city.toLowerCase());
        if (exactMatches != null && !exactMatches.isEmpty()) {
            System.out.println("Found " + exactMatches.size() + " exact match(es) in memory cache.");
            return new ArrayList<>(exactMatches);
        }
        
        // If no exact match, search database with partial matching
        return searchInDatabase("city", city);
    }
    
    public List<Crime> searchByCrimeType(String crimeType) {
        System.out.println("Searching for crime type: " + crimeType);
        
        // First try exact match from HashMap
        List<Crime> exactMatches = crimeTypeIndex.get(crimeType.toLowerCase());
        if (exactMatches != null && !exactMatches.isEmpty()) {
            System.out.println("Found " + exactMatches.size() + " exact match(es) in memory cache.");
            return new ArrayList<>(exactMatches);
        }
        
        // If no exact match, search database with partial matching
        return searchInDatabase("crimeType", crimeType);
    }
    
    public List<Crime> advancedSearch(String name, String city, String crimeType) {
        System.out.println("Performing advanced search...");
        
        List<Bson> filters = new ArrayList<>();
        
        if (name != null && !name.trim().isEmpty()) {
            filters.add(regex("name", Pattern.compile(name.trim(), Pattern.CASE_INSENSITIVE)));
        }
        
        if (city != null && !city.trim().isEmpty()) {
            filters.add(regex("city", Pattern.compile(city.trim(), Pattern.CASE_INSENSITIVE)));
        }
        
        if (crimeType != null && !crimeType.trim().isEmpty()) {
            filters.add(regex("crimeType", Pattern.compile(crimeType.trim(), Pattern.CASE_INSENSITIVE)));
        }
        
        if (filters.isEmpty()) {
            System.out.println("No search criteria provided.");
            return new ArrayList<>();
        }
        
        try {
            List<Crime> results = new ArrayList<>();
            Bson combinedFilter = and(filters);
            
            for (Document doc : crimesCollection.find(combinedFilter)) {
                results.add(documentToCrime(doc));
            }
            
            System.out.println("Advanced search found " + results.size() + " result(s).");
            return results;
            
        } catch (Exception e) {
            System.err.println("Error in advanced search: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void displaySearchResults(List<Crime> results, String searchTerm) {
        if (results.isEmpty()) {
            System.out.println("No records found for: " + searchTerm);
            return;
        }
        
        System.out.println("\n=== SEARCH RESULTS for '" + searchTerm + "' ===");
        System.out.println("Found " + results.size() + " record(s):");
        
        for (int i = 0; i < results.size(); i++) {
            Crime crime = results.get(i);
            System.out.println("\n" + (i + 1) + ". Crime Record:");
            displayCrimeDetails(crime);
        }
    }
    
    public void displaySearchMenu() {
        System.out.println("\n=== SEARCH OPTIONS ===");
        System.out.println("1. Search by Criminal Name");
        System.out.println("2. Search by City");
        System.out.println("3. Search by Crime Type");
        System.out.println("4. Advanced Search (Multiple Criteria)");
        System.out.println("5. Back to Main Menu");
    }
    
    public List<Crime> searchByDetails(String details) {
        System.out.println("Searching in crime details: " + details);
        return searchInDatabase("details", details);
    }

    public void performSearch(int searchType) {
        List<Crime> results = new ArrayList<>();
        String searchTerm = "";
        
        try (Scanner scanner = new Scanner(System.in)) {
            switch (searchType) {
                case 1:
                    System.out.print("Enter criminal name to search: ");
                    searchTerm = scanner.nextLine().trim();
                    results = searchByName(searchTerm);
                    displaySearchResults(results, "Name: " + searchTerm);
                    break;
                    
                case 2:
                    System.out.print("Enter city to search: ");
                    searchTerm = scanner.nextLine().trim();
                    results = searchByCity(searchTerm);
                    displaySearchResults(results, "City: " + searchTerm);
                    break;
                    
                case 3:
                    System.out.print("Enter crime type to search: ");
                    searchTerm = scanner.nextLine().trim();
                    results = searchByCrimeType(searchTerm);
                    displaySearchResults(results, "Crime Type: " + searchTerm);
                    break;
                    
                case 4:
                    performAdvancedSearch();
                    break;
                    
                default:
                    System.out.println("Invalid search option.");
            }
            
            if (!results.isEmpty() && searchType != 4) {
                offerDetailedView(results);
            }
        }
    }
    
    private void performAdvancedSearch() {
        System.out.println("\n=== ADVANCED SEARCH ===");
        System.out.println("Enter search criteria (leave blank to skip):");
        
        String name = null;
        String city = null;
        String crimeType = null;
        
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Criminal name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) name = null;
            
            System.out.print("City: ");
            city = scanner.nextLine().trim();
            if (city.isEmpty()) city = null;
            
            System.out.print("Crime type: ");
            crimeType = scanner.nextLine().trim();
            if (crimeType.isEmpty()) crimeType = null;
        }
        
        List<Crime> results = advancedSearch(name, city, crimeType);
        
        StringBuilder searchCriteria = new StringBuilder();
        if (name != null) searchCriteria.append("Name: ").append(name).append(" ");
        if (city != null) searchCriteria.append("City: ").append(city).append(" ");
        if (crimeType != null) searchCriteria.append("Type: ").append(crimeType);
        
        displaySearchResults(results, searchCriteria.toString());
        
        if (!results.isEmpty()) {
            offerDetailedView(results);
        }
    }
    
    private void offerDetailedView(List<Crime> results) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("\nView detailed record? Enter record number (1-" + results.size() + ") or 0 to skip: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice > 0 && choice <= results.size()) {
                    Crime selectedCrime = results.get(choice - 1);
                    System.out.println("\n=== DETAILED VIEW ===");
                    displayCrimeDetails(selectedCrime);
                    
                    // Offer to display photo as ASCII art
                    if (selectedCrime.getPhotoPath() != null && !selectedCrime.getPhotoPath().isEmpty()) {
                        System.out.print("Display criminal photo as ASCII art? (y/n): ");
                        String response = scanner.nextLine().trim().toLowerCase();
                        if (response.equals("y") || response.equals("yes")) {
                            // This will be implemented in the next task
                            System.out.println("ASCII art display will be available in the next update.");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Skipping detailed view.");
            }
        }
    }
    
    private List<Crime> searchInDatabase(String field, String value) {
        try {
            List<Crime> results = new ArrayList<>();
            Pattern pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
            Bson filter = regex(field, pattern);
            
            for (Document doc : crimesCollection.find(filter)) {
                results.add(documentToCrime(doc));
            }
            
            System.out.println("Database search found " + results.size() + " result(s).");
            return results;
            
        } catch (Exception e) {
            System.err.println("Error searching database: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void buildSearchIndexes() {
        try {
            System.out.println("Building search indexes...");
            
            nameIndex.clear();
            cityIndex.clear();
            crimeTypeIndex.clear();
            
            for (Document doc : crimesCollection.find()) {
                Crime crime = documentToCrime(doc);
                
                // Index by name
                String name = crime.getName().toLowerCase();
                nameIndex.computeIfAbsent(name, k -> new ArrayList<>()).add(crime);
                
                // Index by city
                String city = crime.getCity().toLowerCase();
                cityIndex.computeIfAbsent(city, k -> new ArrayList<>()).add(crime);
                
                // Index by crime type
                String crimeType = crime.getCrimeType().toLowerCase();
                crimeTypeIndex.computeIfAbsent(crimeType, k -> new ArrayList<>()).add(crime);
            }
            
            System.out.println("Search indexes built successfully!");
            System.out.println("- Name index: " + nameIndex.size() + " unique names");
            System.out.println("- City index: " + cityIndex.size() + " unique cities");
            System.out.println("- Crime type index: " + crimeTypeIndex.size() + " unique types");
            
        } catch (Exception e) {
            System.err.println("Error building search indexes: " + e.getMessage());
        }
    }
    
    public void rebuildIndexes() {
        buildSearchIndexes();
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
    
    private void displayCrimeDetails(Crime crime) {
        System.out.println("   ID: " + crime.getId());
        System.out.println("   Name: " + crime.getName());
        System.out.println("   City: " + crime.getCity());
        System.out.println("   Crime Type: " + crime.getCrimeType());
        System.out.println("   Details: " + crime.getDetails());
        System.out.println("   Photo Path: " + crime.getPhotoPath());
        System.out.println("   Created: " + crime.getCreatedAt());
        System.out.println("   " + "-".repeat(50));
    }
    
    public Map<String, Integer> getCrimeStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        for (Map.Entry<String, List<Crime>> entry : crimeTypeIndex.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        
        return stats;
    }
}
