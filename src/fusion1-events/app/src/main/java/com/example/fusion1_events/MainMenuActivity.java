package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainMenuActivity extends AppCompatActivity {

    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userDeviceId;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_main_menu);

        // Get user data from Intent
        userName = getIntent().getStringExtra("userName");
        userEmail = getIntent().getStringExtra("userEmail");
        userPhoneNumber = getIntent().getStringExtra("userPhoneNumber");
        userDeviceId = getIntent().getStringExtra("userDeviceId");

        // Log user details for testing
        Log.d("MainMenuActivity", "User Name: " + userName);

        // Find the Profile button and other views
        ImageButton editProfile = findViewById(R.id.btnProfile);

        // Set click listener for the profile button
        editProfile.setOnClickListener(v -> showUserProfile());
    }

    private void showUserProfile() {
        // Set the content to display the user profile layout
        setContentView(R.layout.user_profile_page);

        // Populate the profile layout with user information
        TextView profileName = findViewById(R.id.tvProfileName);
        TextView profileEmail = findViewById(R.id.tvProfileEmail);
        TextView profilePhone = findViewById(R.id.tvProfilePhoneNumber);


        // Set the text fields with user information
        profileName.setText(userName);
        profileEmail.setText(userEmail);
        profilePhone.setText(userPhoneNumber);

    }
}

