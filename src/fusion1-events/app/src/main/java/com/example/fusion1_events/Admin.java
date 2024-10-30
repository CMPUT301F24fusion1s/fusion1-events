package com.example.fusion1_events;

import android.provider.ContactsContract;

import androidx.lifecycle.Lifecycle;

public class Admin extends User{


    public Admin(String email, String name, String role, String phoneNumber, String userId,String deviceId) {
        super(email, name, role, phoneNumber,userId,deviceId);
    }
}