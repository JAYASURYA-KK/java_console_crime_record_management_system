package com.crimemanagement.util;

import com.crimemanagement.model.Crime;

public class CrimeInputHelper {
    
    public static Crime getCrimeInputFromUser() {
        System.out.println("\n=== Enter Crime Record Details ===");
        
        String name = InputValidator.getValidString("Enter criminal name: ", false);
        String city = InputValidator.getValidString("Enter city: ", false);
        String crimeType = getCrimeType();
        String details = InputValidator.getValidString("Enter crime details: ", false);
        String photoPath = InputValidator.getValidString("Enter photo path (or press Enter to skip): ", true);
        
        if (photoPath.isEmpty()) {
            photoPath = "photos/default.jpg";
        }
        
        return new Crime(name, city, crimeType, details, photoPath);
    }
    
    public static void updateCrimeFromUser(Crime crime) {
        System.out.println("\n=== Update Crime Record ===");
        System.out.println("Current details (press Enter to keep current value):");
        
        System.out.println("Current name: " + crime.getName());
        String name = InputValidator.getValidString("New name: ", true);
        if (!name.isEmpty()) {
            crime.setName(name);
        }
        
        System.out.println("Current city: " + crime.getCity());
        String city = InputValidator.getValidString("New city: ", true);
        if (!city.isEmpty()) {
            crime.setCity(city);
        }
        
        System.out.println("Current crime type: " + crime.getCrimeType());
        if (InputValidator.getYesNoConfirmation("Update crime type?")) {
            crime.setCrimeType(getCrimeType());
        }
        
        System.out.println("Current details: " + crime.getDetails());
        String details = InputValidator.getValidString("New details: ", true);
        if (!details.isEmpty()) {
            crime.setDetails(details);
        }
        
        System.out.println("Current photo path: " + crime.getPhotoPath());
        String photoPath = InputValidator.getValidString("New photo path: ", true);
        if (!photoPath.isEmpty()) {
            crime.setPhotoPath(photoPath);
        }
    }
    
    private static String getCrimeType() {
        System.out.println("\nSelect Crime Type:");
        System.out.println("1. Theft");
        System.out.println("2. Fraud");
        System.out.println("3. Assault");
        System.out.println("4. Burglary");
        System.out.println("5. Robbery");
        System.out.println("6. Murder");
        System.out.println("7. Drug Offense");
        System.out.println("8. Cybercrime");
        System.out.println("9. Other");
        
        int choice = InputValidator.getValidInt("Enter choice (1-9): ", 1, 9);
        
        switch (choice) {
            case 1: return "Theft";
            case 2: return "Fraud";
            case 3: return "Assault";
            case 4: return "Burglary";
            case 5: return "Robbery";
            case 6: return "Murder";
            case 7: return "Drug Offense";
            case 8: return "Cybercrime";
            case 9: return InputValidator.getValidString("Enter crime type: ", false);
            default: return "Other";
        }
    }
}
