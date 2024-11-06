package com.example.fusion1_events;

import android.provider.ContactsContract;

/**
 *  Each user can user one device because we are
 */
// TODO maybe change the email type so user does not input any value
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private String email;
    private String name;
    private String role;
    private String phoneNumber;
    private String userId;
    private String deviceId;


    public User(String email, @NonNull String name, String role, String phoneNumber, String userId, String deviceId) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.deviceId = deviceId;
    }
    // no argument constructor for serialization
    public User()
    {

    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and setters for userId and deviceId
    public String getUserId() {
        return userId; // Convert String back to UUID
    }

    public void setUserId(String userId) {
        this.userId = userId; // Convert UUID to String for storage
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null && !name.isEmpty())
            this.name = name;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber != null && !phoneNumber.isEmpty()) {
            phoneNumber = phoneNumber.trim();
            this.phoneNumber = phoneNumber;
        }
    }

    public void updateInfo(String name, String email, String phoneNumber){
        setName(name);
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

}
