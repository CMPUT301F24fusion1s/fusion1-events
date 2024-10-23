package com.example.fusion1_events;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseManager {

    // Initialize Firebase Firestore instance
    private FirebaseFirestore db;

    public FirebaseManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieves a User object by the deviceId.
     * This method is a skeleton implementation and requires further handling for complete data validation.
     *
     * @param deviceId The deviceId used to identify the user.
     * @param callback A callback to return the User object asynchronously.
     */
    public void getUserByDeviceId(String deviceId, final UserCallback callback) {
        // Reference to the users collection
        CollectionReference usersCollection = db.collection("users");

        // Query Firestore by deviceId
        usersCollection.whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            // Get the first document matching the query (assuming deviceId is unique)
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // Extract data from document (requires further error handling)
                            String userId = document.getId();
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String phoneNumber = document.getString("phoneNumber");
                            String role = document.getString("role");
                            String facilityName = document.getString("facilityName");
                            String location = document.getString("location");

                            // TODO: Add proper null checks and data validation
                            User user = null;
                            if (role != null) {
                                Log.d("FirebaseManager", "Role: " + role);
                                // TODO: Add logic to create user based on role
                            } else {
                                callback.onFailure(new Exception("Role is missing."));
                                return;
                            }

                            // Pass the user object to the callback (requires further handling)
                            if (user != null) {
                                callback.onSuccess(user);
                            } else {
                                callback.onFailure(new Exception("User creation failed."));
                            }
                        } else {
                            callback.onFailure(task.getException());
                            Log.e("FirebaseManager", "Failed to retrieve user by deviceId", task.getException());
                        }
                    }
                });
    }

    // Callback interface for asynchronous user retrieval
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }
}
