package com.example.fusion1_events;

import java.io.Serializable;

/**
 * Facility model class to store facility details
 */
public class Facility implements Serializable {
    private String name;
    private String location;
    private String userId;

    public Facility() {
        // Required for Firestore deserialization
    }

    // Constructor with parameters
    public Facility(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Facility(String name, String location, String userId) {
        this.name = name;
        this.location = location;
        this.userId = userId; // Initialize userId
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getuserId() {
        return userId;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }
}
