package com.example.fusion1_events;


import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The FirebaseManager class manages all interactions with Firebase Firestore, including user and event management.
 * It provides methods for adding, updating, and retrieving users and events from the Firestore database.
 */
public class FirebaseManager {

    // Initialize Firebase Firestore instance
    private FirebaseFirestore db;
    // Maximum size of image that can be retrieved
    final long imageSize = 10 * 1024 * 1024;

    /**
     * Constructor to initialize the Firebase Firestore instance.
     */
    public FirebaseManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds a new user to the 'users' collection in Firebase Firestore.
     *
     * @param entrant The Entrant object to add to Firestore.
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
     * This method determines the appropriate User subclass (e.g., Admin, Entrant, Organizer) to instantiate.
     *
     * @param role The role of the user (e.g., Admin, Entrant, Organizer).
     * @param document The Firestore document containing user data.
     * @return The User object created based on the role, or null if creation fails.
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

    /**
     * Updates a user's profile information in Firebase Firestore.
     *
     * @param userId The ID of the user to update.
     * @param updatedUser The updated User object containing the new profile information.
     * @param callback A callback to handle success or failure events.
     */
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

    /**
     * This method is user to return
     * @param callback
     */

    public void getAllusers(final UsersListCallback callback)
    {
        //
         //AtomicReference<List<Entrant>> users = null;

        CollectionReference all_users = db.collection("users");

        all_users.whereEqualTo("role","Entrant").get().addOnCompleteListener(task -> {

       if(task.isSuccessful() && task.getResult() != null) {
           List<Entrant> users = new ArrayList<>();

           for (DocumentSnapshot document : task.getResult()) {
               Map<String, Object> entrantDocument = document.getData();
               assert entrantDocument != null;
               users.add(Entrant.extractUser(entrantDocument));


           }
           //
           callback.onScuccess(users);
       }
       else
           {
               callback.onFailure(task.getException() != null ?
                       task.getException() :
                       new Exception("Unknown error occurred."));
           }
        });


    }


    // Callback interface for asynchronous user retrieval
    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    // Interface for update callback
    public interface UpdateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Callback interface for fetching all user profiles
    public interface UsersListCallback{
        void onScuccess(List<Entrant> users);
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
        Map<String, Object> eventData = event.toMap();

        // Update the event in Firestore
        eventsCollection.whereEqualTo("qrCodeHash", event.getId().toString())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Get the first (and should be only) matching document
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Update the document using its ID
                        eventsCollection.document(document.getId())
                                .update(eventData)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("FirebaseManager", "Event updated with ID: " + event.getId()))
                                .addOnFailureListener(e ->
                                        Log.e("FirebaseManager", "Error updating event", e));
                    } else {
                        Log.e("FirebaseManager", "No event found with QR hash: " + event.getId());
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FirebaseManager", "Error querying for event", e));
    }

    /**
     * Retrieves an Event object from Firestore by its ID.
     *
     * @param qrHash The hash of the event to retrieve. This is equivalent to Event ID.
     * @param callback A callback to return the Event object asynchronously.
     */
    public void getEventByQRHash(String qrHash, final EventCallback callback) {
        CollectionReference eventsCollection = db.collection("events");

        // Query events where qrCodeHash matches the scanned hash
        eventsCollection.whereEqualTo("qrCodeHash", qrHash)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {
                            // Get the first (and should be only) matching document
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            callback.onSuccess(Event.fromFirestoreDocument(document));
                        } else {
                            callback.onFailure(new Exception("Event not found."));
                        }
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException() :
                                new Exception("Unknown error occurred."));
                        Log.e("FirebaseManager", "Failed to retrieve event by QR hash", task.getException());
                    }
                });
    }

    public void getAllEvents(final EventsListCallback callback) {
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection
                .orderBy("date", Query.Direction.ASCENDING)  // Sort by date
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Event> events = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            events.add(Event.fromFirestoreDocument(document));
                        }
                        callback.onSuccess(events);
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException() :
                                new Exception("Unknown error occurred."));
                    }
                });
    }

    public void getUserEvents(UUID userId, final EventsListCallback callback) {
        CollectionReference eventsCollection = db.collection("events");

        // First, get events where user is the organizer
        eventsCollection
                .whereEqualTo("organizerId", userId.toString())
                .get()
                .addOnCompleteListener(organizerTask -> {
                    if (organizerTask.isSuccessful()) {
                        List<Event> userEvents = new ArrayList<>();

                        // Add events where user is organizer
                        for (DocumentSnapshot doc : organizerTask.getResult()) {
                            userEvents.add(Event.fromFirestoreDocument(doc));
                        }

                        // Now get events where user is in waitlist
                        eventsCollection
                                .whereArrayContains("waitlist", userId.toString())
                                .get()
                                .addOnCompleteListener(waitlistTask -> {
                                    if (waitlistTask.isSuccessful()) {
                                        // Add events where user is in waitlist
                                        for (DocumentSnapshot doc : waitlistTask.getResult()) {
                                            userEvents.add(Event.fromFirestoreDocument(doc));
                                        }

                                        // Sort events by date
                                        Collections.sort(userEvents, (e1, e2) -> e1.getDate().compareTo(e2.getDate()));

                                        callback.onSuccess(userEvents);
                                    } else {
                                        callback.onFailure(waitlistTask.getException() != null ?
                                                waitlistTask.getException() :
                                                new Exception("Error fetching waitlist events"));
                                    }
                                });
                    } else {
                        callback.onFailure(organizerTask.getException() != null ?
                                organizerTask.getException() :
                                new Exception("Error fetching organized events"));
                    }
                });
    }


    public void deleteUser(String deviceId){
        CollectionReference usersCollection = db.collection("users");
        usersCollection.document(deviceId).delete();
    }

    // Callback interface for retrieving multiple events
    public interface EventsListCallback {
        void onSuccess(List<Event> events);
        void onFailure(Exception e);
    }

    // Callback interface for asynchronous event retrieval
    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }



}
