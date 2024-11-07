package com.example.fusion1_events;

import static com.example.fusion1_events.UtilityMethods.decodeBase64ToBitmap;
import static com.example.fusion1_events.UtilityMethods.convertStringListToUuidList;


import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class FirebaseManager {

    // Initialize Firebase Firestore instance
    private FirebaseFirestore db;
    // max size of image i can retrieve
    final long imageSize = 10 * 1024 * 1024;
    public FirebaseManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Add new user to user collection
     */

    public void addUserToFirebase(Entrant entrant)
    {
        // Add the entrant to the 'user' collection in Firebase
        db.collection("users")
                .document(entrant.getDeviceId())
                .set(entrant)
                .addOnSuccessListener(documentReference ->{
                  // Log success or handle success scenario
                    Log.d("FirebaseManager", "User added with ID: " + entrant.getDeviceId());
                })
                .addOnFailureListener(e -> {
                    Log.w("FirebaseManager", "Error adding user",e);
                });


    }

    /**
     * Retrieves a User object by the deviceId.
     * This method is a skeleton implementation and requires further handling for complete data validation.
     *
     * @param deviceId The deviceId used to identify the user.
     * @param callback A callback to return the User object asynchronously.
     */
    public void getUserByDeviceId(String deviceId, final UserCallback callback) {
        CollectionReference usersCollection = db.collection("users");

        usersCollection.whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String role = document.getString("role");

                        if (role != null) {
                            Log.d("FirebaseManager", "Role: " + role);
                            User user = createUserFromRole(role, document);

                            if (user != null) {
                                callback.onSuccess(user);
                            } else {
                                callback.onFailure(new Exception("Failed to create user instance from document."));
                            }
                        } else {
                            callback.onFailure(new Exception("Role is missing in the user document."));
                        }
                    } else {
                        String errorMessage = (task.getException() != null) ? task.getException().getMessage() : "Unknown error retrieving user.";
                        Log.e("FirebaseManager", "Error retrieving user by deviceId: " + errorMessage, task.getException());
                        callback.onFailure(new Exception("User not found or task failed."));
                    }
                });
    }

    /**
     * Creates a User object based on the role specified in the document.
     */
    private User createUserFromRole(String role, DocumentSnapshot document) {
        switch (role) {
            case "Admin":
                return document.toObject(Admin.class);
            case "Entrant":
                Log.d("FirebaseManager", "Entrant ");
                Map<String, Object> entrantDocument = document.getData();
                assert entrantDocument != null;
                return Entrant.extractUser(entrantDocument);
            case "Organizer":
                return document.toObject(Organizer.class);
            default:
                return document.toObject(User.class);
        }
    }

    public void updateUserProfile(String userId, User updatedUser, UpdateCallback callback) {
        String name = updatedUser.getName();
        String email = updatedUser.getEmail();
        String phone = updatedUser.getPhoneNumber();
        Entrant entrant = (Entrant) updatedUser;
        boolean notification = entrant.getNotificationEnabled();
        String profileImage  = null;
        if(entrant.getProfileImage() != null)
            profileImage = UtilityMethods.encodeBitmapToBase64(((Entrant) updatedUser).getProfileImage());

        // Create a map to store the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phoneNumber", phone);
        updates.put("profileImage", profileImage);
        updates.put("notification", notification);

        // Ensure you are using the correct userId for the document reference
        db.collection("users").document(updatedUser.getDeviceId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseManager", "User profile updated successfully.");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseManager", "Error updating user profile", e);
                    callback.onFailure(e);
                });
    }


    // Callback interface for asynchronous user retrieval
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    // Interface for callback
    public interface UpdateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    /**
     * Stores a new Event object in Firestore.
     *
     * @param event The Event object to store.
     */
    public void storeNewEvent(Event event) {
        CollectionReference eventsCollection = db.collection("events");

        // Create a map to store event data
        Map<String, Object> eventData =  event.toMap();

        // Add the event to Firestore
        eventsCollection.add(eventData)
                .addOnSuccessListener(documentReference -> Log.d("FirebaseManager", "Event added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("FirebaseManager", "Error adding event", e));
    }

    /**
     * Updates an existing Event object in Firestore.
     *
     * @param event The Event object to update.
     */
    public void updateExistingEvent(Event event) {
        CollectionReference eventsCollection = db.collection("events");

        // Create a map to store event data
        Map<String, Object> eventData =  event.toMap();

        // Update the event in Firestore
        eventsCollection.document(event.getId().toString())
                .update(eventData)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseManager", "Event updated with ID: " + event.getId()))
                .addOnFailureListener(e -> Log.e("FirebaseManager", "Error updating event", e));
    }

    /**
     * Retrieves a list of Event objects from Firestore.
     *
     * @param callback A callback to return the list of Event objects asynchronously.
     */
    public void getEventById(String eventId, final EventCallback callback) {
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            callback.onSuccess(Event.fromFirestoreDocument(document));
                        } else {
                            callback.onFailure(new Exception("Event not found."));
                        }
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException() : new Exception("Unknown error occurred."));
                        Log.e("FirebaseManager", "Failed to retrieve event by eventId", task.getException());
                    }
                });
    }

    // Callback interface for asynchronous event retrieval
    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }

}
