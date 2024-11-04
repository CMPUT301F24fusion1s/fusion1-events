package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainMenuActivity extends AppCompatActivity {

    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userDeviceId;
    private String userId;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_main_menu);

        // Initialize UserController
        userController = new UserController(new FirebaseManager());

        // Get user data from Intent
        userName = getIntent().getStringExtra("userName");
        userEmail = getIntent().getStringExtra("userEmail");
        userPhoneNumber = getIntent().getStringExtra("userPhoneNumber");
        userDeviceId = getIntent().getStringExtra("userDeviceId");
        userId = getIntent().getStringExtra("userId");

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

        // Set up the back arrow button
        Button editProfileBtn = findViewById(R.id.editProfileButton);
        editProfileBtn.setOnClickListener(v -> editProfile());


    }
    private void editProfile()
    {
        setContentView(R.layout.user_edit_profile_page);

        EditText eidtProfileName = findViewById(R.id.e_name);
        EditText eidtProfileEmail = findViewById(R.id.e_email);
        EditText eidtProfilePhone = findViewById(R.id.e_phone);

        eidtProfileName.setText(userName);
        eidtProfileEmail.setText(userEmail);
        eidtProfilePhone.setText(userPhoneNumber);

        Button saveChanges = findViewById(R.id.saveChangesButton);

        saveChanges.setOnClickListener(v -> {
            User updateduser = new User();
            updateduser.setName(eidtProfileName.getText().toString());
            updateduser.setEmail(eidtProfileEmail.getText().toString());
            updateduser.setPhoneNumber(eidtProfilePhone.getText().toString());
            userController.updateProfile(userId, updateduser);
                });

    }


}

