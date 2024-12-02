package com.example.fusion1_events; // Change to your actual package name

import java.io.Serializable;

public class Facility implements Serializable {
    private String name;
    private String location;
    private String userID; // Optional: Include this if you need to store the user ID

    // No-argument constructor required for Firebase deserialization
    public Facility() {
        // Required for Firestore deserialization
    }

    // Constructor with parameters
    public Facility(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Facility(String name, String location, String userID) {
        this.name = name;
        this.location = location;
        this.userID = userID; // Initialize userID
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getUserID() {
        return userID; // Getter for userID
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUserID(String userID) {
        this.userID = userID; // Setter for userID
    }
}
