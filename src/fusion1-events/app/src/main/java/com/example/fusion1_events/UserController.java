package com.example.fusion1_events;

public class UserController {
    // Attributes for FirebaseManager reference
    private FirebaseManager firebaseManager;

    // Constructor
    public UserController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    // Placeholder methods for login, signup, profile update
    public void login(String deviceId) {
        // TODO: Implement login logic
    }

    public void signUpUser(Entrant entrant) {

        firebaseManager.addUserToFirebase(entrant);
    }

    public void updateProfile(String userId, User updatedUser) {
        // TODO: Implement update profile logic
    }
}

