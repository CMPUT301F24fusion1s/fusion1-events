package com.example.fusion1_events;

import static com.example.fusion1_events.UtilityMethods.encodeBitmapToBase64;
import static com.example.fusion1_events.UtilityMethods.decodeBase64ToBitmap;
import static com.example.fusion1_events.UtilityMethods.convertUuidListToStringList;
import static com.example.fusion1_events.UtilityMethods.convertStringListToUuidList;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
                            // Extract data from document
                            UUID organizerId = UUID.fromString(document.getString("organizerId"));
                            String name = document.getString("name");
                            Date date = document.getDate("date");
                            String location = document.getString("location");
                            String description = document.getString("description");
                            String posterBase64 = document.getString("poster");
                            List<String> waitlistStrings = (List<String>) document.get("waitlist");
                            int capacity = document.getLong("capacity") != null ? Objects.requireNonNull(document.getLong("capacity")).intValue() : 0;

                            // Convert Base64 string back to Bitmap
                            Bitmap poster = null;
                            if (posterBase64 != null) {
                                poster = decodeBase64ToBitmap(posterBase64);
                            }

                            // Convert list of strings back to list of UUIDs
                            List<UUID> waitlist = convertStringListToUuidList(waitlistStrings);

                            // Create Event object
                            Event event = new Event(organizerId, name, date, location, description, poster, capacity);
                            event.setWaitlist(waitlist);
                            callback.onSuccess(event);
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
