package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The MainMenuActivity class represents the main menu screen of the application.
 * It provides functionalities for navigating to user profile and editing profile information.
 */
public class MainMenuActivity extends BaseActivity {

    /**
     * Called when the activity is first created. Initializes the user interface components and sets up the event listeners.
     *
     * @param savedInstancesState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

        // Log user details for testing
        Log.d("MainMenuActivity", "User Name: " + currentUser.getName());

        // Find the Profile button and other views
        ImageButton editProfile = findViewById(R.id.btnProfile);

        // Set click listener for the profile button
        editProfile.setOnClickListener(v -> showUserProfileFragment(currentUser));
    }

    /**
     * @return The layout resource ID for the current activity.
     */
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_menu;
    }

    /**
     * @return The navigation menu item ID for the current activity.
     */
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.home;
    }
}
