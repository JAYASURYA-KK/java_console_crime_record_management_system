package com.crimemanagement.util;

import com.crimemanagement.model.Crime;

import java.util.*;

public class DataStructureManager {
    
    // Demonstrate different data structure operations
    public static void demonstrateDataStructures(List<Crime> crimes) {
        if (crimes.isEmpty()) {
            System.out.println("No crime data available for demonstration.");
            return;
        }
        
        System.out.println("\n=== DATA STRUCTURE DEMONSTRATIONS ===");
        
        // ArrayList demonstration
        demonstrateArrayList(crimes);
        
        // Stack demonstration
        demonstrateStack(crimes);
        
        // HashMap demonstration
        demonstrateHashMap(crimes);
        
        // Additional data structure operations
        demonstrateQueue(crimes);
    }
    
    private static void demonstrateArrayList(List<Crime> crimes) {
        System.out.println("\n1. ARRAYLIST OPERATIONS:");
        ArrayList<Crime> crimeList = new ArrayList<>(crimes);
        
        System.out.println("   - Total crimes in ArrayList: " + crimeList.size());
        System.out.println("   - First crime: " + (crimeList.isEmpty() ? "None" : crimeList.get(0).getName()));
        System.out.println("   - Last crime: " + (crimeList.isEmpty() ? "None" : crimeList.get(crimeList.size() - 1).getName()));
        
        // Demonstrate searching in ArrayList
        if (!crimeList.isEmpty()) {
            String searchName = crimeList.get(0).getName();
            boolean found = false;
            for (Crime crime : crimeList) {
                if (crime.getName().equalsIgnoreCase(searchName)) {
                    found = true;
                    break;
                }
            }
            System.out.println("   - Linear search for '" + searchName + "': " + (found ? "Found" : "Not found"));
        }
    }
    
    private static void demonstrateStack(List<Crime> crimes) {
        System.out.println("\n2. STACK OPERATIONS (LIFO - Last In, First Out):");
        Stack<Crime> crimeStack = new Stack<>();
        
        // Push crimes onto stack
        for (Crime crime : crimes) {
            crimeStack.push(crime);
        }
        
        System.out.println("   - Crimes pushed onto stack: " + crimeStack.size());
        
        if (!crimeStack.isEmpty()) {
            System.out.println("   - Top of stack (most recent): " + crimeStack.peek().getName());
            
            // Demonstrate popping
            System.out.println("   - Popping crimes from stack (latest first):");
            @SuppressWarnings("unchecked")
            Stack<Crime> tempStack = (Stack<Crime>) crimeStack.clone(); // Safe cast as we know crimeStack is Stack<Crime>
            int count = 0;
            while (!tempStack.isEmpty() && count < 3) {
                Crime crime = tempStack.pop();
                System.out.println("     " + (count + 1) + ". " + crime.getName() + " (" + crime.getCrimeType() + ")");
                count++;
            }
            
            if (tempStack.size() > 0) {
                System.out.println("     ... and " + tempStack.size() + " more");
            }
        }
    }
    
    private static void demonstrateHashMap(List<Crime> crimes) {
        System.out.println("\n3. HASHMAP OPERATIONS (Fast Lookup):");
        
        // Create HashMap for fast city lookup
        HashMap<String, List<String>> cityToCriminals = new HashMap<>();
        HashMap<String, Integer> crimeTypeCount = new HashMap<>();
        
        for (Crime crime : crimes) {
            // Group criminals by city
            String city = crime.getCity().toLowerCase();
            cityToCriminals.computeIfAbsent(city, k -> new ArrayList<>()).add(crime.getName());
            
            // Count crime types
            String crimeType = crime.getCrimeType().toLowerCase();
            crimeTypeCount.put(crimeType, crimeTypeCount.getOrDefault(crimeType, 0) + 1);
        }
        
        System.out.println("   - Cities with crime records: " + cityToCriminals.size());
        System.out.println("   - Crime type distribution:");
        
        for (Map.Entry<String, Integer> entry : crimeTypeCount.entrySet()) {
            System.out.println("     " + entry.getKey() + ": " + entry.getValue() + " case(s)");
        }
        
        // Demonstrate O(1) lookup
        if (!cityToCriminals.isEmpty()) {
            String firstCity = cityToCriminals.keySet().iterator().next();
            List<String> criminalsInCity = cityToCriminals.get(firstCity);
            System.out.println("   - Fast lookup for '" + firstCity + "': " + criminalsInCity.size() + " criminal(s)");
        }
    }
    
    private static void demonstrateQueue(List<Crime> crimes) {
        System.out.println("\n4. QUEUE OPERATIONS (FIFO - First In, First Out):");
        Queue<Crime> crimeQueue = new LinkedList<>(crimes);
        
        System.out.println("   - Crimes in queue: " + crimeQueue.size());
        
        if (!crimeQueue.isEmpty()) {
            System.out.println("   - Front of queue (oldest): " + crimeQueue.peek().getName());
            
            // Demonstrate processing queue
            System.out.println("   - Processing crimes from queue (oldest first):");
            Queue<Crime> tempQueue = new LinkedList<>(crimeQueue);
            int count = 0;
            while (!tempQueue.isEmpty() && count < 3) {
                Crime crime = tempQueue.poll();
                System.out.println("     " + (count + 1) + ". Processing: " + crime.getName() + " (" + crime.getCrimeType() + ")");
                count++;
            }
            
            if (tempQueue.size() > 0) {
                System.out.println("     ... and " + tempQueue.size() + " more in queue");
            }
        }
    }
    
    public static void displayPerformanceComparison() {
        System.out.println("\n=== DATA STRUCTURE PERFORMANCE COMPARISON ===");
        System.out.println("Operation          | ArrayList | Stack    | HashMap  | Queue");
        System.out.println("-------------------|-----------|----------|----------|----------");
        System.out.println("Insert at end      | O(1)      | O(1)     | O(1)     | O(1)");
        System.out.println("Insert at start    | O(n)      | N/A      | O(1)     | O(1)");
        System.out.println("Search by value    | O(n)      | O(n)     | O(1)*    | O(n)");
        System.out.println("Delete by value    | O(n)      | O(n)     | O(1)*    | O(n)");
        System.out.println("Access by index    | O(1)      | O(n)     | N/A      | O(n)");
        System.out.println("Memory overhead    | Low       | Low      | Medium   | Low");
        System.out.println("\n* Average case for HashMap, O(n) worst case");
        
        System.out.println("\n=== WHEN TO USE EACH DATA STRUCTURE ===");
        System.out.println("ArrayList: When you need indexed access and frequent iteration");
        System.out.println("Stack: When you need LIFO behavior (latest crimes first)");
        System.out.println("HashMap: When you need fast lookups by key (name, city, type)");
        System.out.println("Queue: When you need FIFO behavior (process crimes in order)");
    }
}
