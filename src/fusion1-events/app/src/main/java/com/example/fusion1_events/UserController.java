package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

public class UserController {
    // Attributes for FirebaseManager reference
    private FirebaseManager firebaseManager;

    // Constructor
    public UserController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    // Placeholder methods for login, signup, profile update
    // Method to handle user login based on device ID
    public void userLogin(String deviceId, FirebaseManager.UserCallback callback) {

        firebaseManager.getUserByDeviceId(deviceId, new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // User exists, return the user object to MainActivity
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(Exception e) {
                // User does not exist, notify MainActivity to proceed with new user registration
                callback.onFailure(e);
            }
        });
    }

    public void signUpUser(Entrant entrant) {

        firebaseManager.addUserToFirebase(entrant);
    }

    public void updateProfile(String userId, User updatedUser) {
        // TODO: Implement update profile logic
        firebaseManager.updateUserProfile(userId, updatedUser, new FirebaseManager.UpdateCallback() {
            @Override
            public void onSuccess() {
                Log.d("UserController", "Profile updated successfully.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UserController", "Failed to update profile: " + e.getMessage(), e);
            }
        });
    }

    public void replaceImage(){
//        return new Bitmap();
    };

}

