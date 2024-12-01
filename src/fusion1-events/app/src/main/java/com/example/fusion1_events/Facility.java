package com.example.fusion1_events;

public class Facility {
    private String name;
    private String location;


    // Constructor
    public Facility(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
