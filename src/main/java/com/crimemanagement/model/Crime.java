package com.crimemanagement.model;

import java.time.LocalDateTime;

public class Crime {
    private String id;
    private String name;
    private String city;
    private String crimeType;
    private String details;
    private String photoPath;
    private LocalDateTime createdAt;
    
    public Crime() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Crime(String name, String city, String crimeType, String details, String photoPath) {
        this.name = name;
        this.city = city;
        this.crimeType = crimeType;
        this.details = details;
        this.photoPath = photoPath;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCrimeType() {
        return crimeType;
    }
    
    public void setCrimeType(String crimeType) {
        this.crimeType = crimeType;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getPhotoPath() {
        return photoPath;
    }
    
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Crime{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", crimeType='" + crimeType + '\'' +
                ", details='" + details + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
