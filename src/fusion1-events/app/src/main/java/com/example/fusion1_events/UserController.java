package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.util.List;

/**
 * The UserController class manages user-related operations such as login, sign-up, and profile updates.
 * It interacts with Firebase through a FirebaseManager instance to manage user data.
 */
public class UserController{
    // Attributes for FirebaseManager reference
    private FirebaseManager firebaseManager;

    /**
     * Constructor to initialize UserController with a FirebaseManager reference.
     *
     * @param firebaseManager The FirebaseManager instance used to interact with Firebase.
     */
    public UserController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    /**
     * Handles user login based on the device ID. If the user exists, returns the user object,
     * otherwise, notifies the caller to proceed with user registration.
     *
     * @param deviceId The device ID to identify the user.
     * @param callback The callback interface to handle success or failure events.
     */
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

    /**
     * Registers a new user in Firebase.
     *
     * @param entrant The Entrant object containing the new user's information.
     */
    public void signUpUser(Entrant entrant) {

        firebaseManager.addUserToFirebase(entrant);
    }

    /**
     * Updates the profile information for a specific user.
     *
     * @param userId The ID of the user whose profile is being updated.
     * @param updatedUser The updated User object containing the new profile information.
     */
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

    /**
     * Replaces the current profile image for a specific user.
     *
     * @param selectedImage The new profile image represented as a Bitmap.
     * @param user The User object whose profile image is being replaced.
     * @param imageUri The URI of the new image (not used in current implementation).
     */
    public void replaceImage(Bitmap selectedImage, User user, Uri imageUri){
        Entrant entrantUser = (Entrant) user;
        entrantUser.setProfileImage(selectedImage);
    }



}

