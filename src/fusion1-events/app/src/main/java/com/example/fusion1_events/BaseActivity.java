package com.example.fusion1_events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class BaseActivity extends AppCompatActivity {
    protected User currentUser;
    protected NavigationBarView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // Get user data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getParcelable("user");
            loadUserProfileImage(extras.getString("profile_image_path"));
        }

        // Initialize and setup bottom navigation
        setupBottomNavigation();
    }

    /**
     * Displays the UserProfileFragment containing the user's profile information.
     *
     * @param user The User object whose profile information will be displayed.
     */
    protected void showUserProfileFragment(User user) {
        // Create an instance of UserProfileFragment with user data
        UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(user);

        // Replace the entire content of the activity with the fragment for full-screen display
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, userProfileFragment); // Use android.R.id.content
        transaction.addToBackStack(null); // Add to back stack for back navigation
        transaction.commit();
    }

    /**
     * Displays the EditProfileFragment to allow editing of the user's profile information.
     *
     * @param user The User object whose profile information will be edited.
     */
    protected void showEditProfileFragment(User user) {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.setUser(user); // Set the user object directly

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, editProfileFragment); // Full-screen overlay
        transaction.addToBackStack(null); // Allow back navigation
        transaction.commit();
    }

    private void loadUserProfileImage(String imagePath) {
        if (imagePath != null && currentUser != null) {
            try {
                FileInputStream fis = this.openFileInput(imagePath);
                Bitmap profileImage = BitmapFactory.decodeStream(fis);
                fis.close();
                currentUser.setProfileImage(profileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == getNavigationMenuItemId()) {
                return true;
            }

            if (item.getItemId() == R.id.home) {
                navigateToActivity(MainMenuActivity.class);
                return true;
            } else if (item.getItemId() == R.id.camera) {
                navigateToActivity(ScanQRCodeActivity.class);
                return true;
            } else if (item.getItemId() == R.id.events) {
                navigateToActivity(EventsPageActivity.class);
                return true;
            }
            return false;
        });
    }

    protected void navigateToActivity(Class<?> activityClass) {
        if (this.getClass() != activityClass) {
            Intent intent = new Intent(this, activityClass);

            // Pass user data
            if (currentUser != null) {
                Bundle bundle = new Bundle();
                String tempFileName = "temp_profile_image.jpg";

                try {
                    if (currentUser.getProfileImage() != null) {
                        FileOutputStream fos = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                        currentUser.getProfileImage().compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bundle.putParcelable("user", currentUser);
                bundle.putString("profile_image_path", tempFileName);
                intent.putExtras(bundle);
            }

            startActivity(intent);
            if (activityClass != MainMenuActivity.class) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(getNavigationMenuItemId());
        }
    }

    // Abstract method to be implemented by child activities
    protected abstract int getLayoutResourceId();

    // Abstract method to be implemented by child activities
    protected abstract int getNavigationMenuItemId();
}
