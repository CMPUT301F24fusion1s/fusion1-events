package com.example.fusion1_events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;


public class MainMenuActivity extends AppCompatActivity {

    private String userName;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_main_menu);

        // Initialize UserController
        userController = new UserController(new FirebaseManager());

        // Get user data from Intent
        Intent mainActivityIntent= getIntent();

        // Get the profile image from internal storage
        Bitmap profileImage = null;
        String fileName = getIntent().getStringExtra("image_path");
        try {
            FileInputStream fis = this.openFileInput(fileName);
            profileImage = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Entrant user = (Entrant) Objects.requireNonNull(mainActivityIntent.getExtras()).get("user");
        assert user != null;
        userName = user.getName();
        user.setProfileImage(profileImage);

        // Log user details for testing
        Log.d("MainMenuActivity", "User Name: " + userName);

        // Find the Profile button and other views
        ImageButton editProfile = findViewById(R.id.btnProfile);

        // Set click listener for the profile button
        //editProfile.setOnClickListener(v -> showUserProfile(user));
        editProfile.setOnClickListener(v -> showUserProfileFragment(user));
    }

    private void showUserProfileFragment(User user) {
        // Create an instance of UserProfileFragment with user data
        UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(user);

        // Replace the entire content of the activity with the fragment for full-screen display
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, userProfileFragment); // Use android.R.id.content
        transaction.addToBackStack(null); // Add to back stack for back navigation
        transaction.commit();
    }

    void showEditProfileFragment(User user) {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.setUser(user); // Set the user object directly

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, editProfileFragment); // Full-screen overlay
        transaction.addToBackStack(null); // Allow back navigation
        transaction.commit();
    }

}




