package com.example.fusion1_events;


import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                                callback.onSuccess((Entrant)user);
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

    public void addFacility(Facility facility, FirebaseManager.OperationCallback callback) {
        db.collection("Facilities")
                .document(facility.getName()) // Use name as document ID, or use UUID.randomUUID().toString()
                .set(facility)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
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

    public void removeUserImage(String deviceID, OperationCallback callback)
    {
        db.collection("users").
                document(deviceID).update("profileImage",null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseManager", "User image removed successfully from Firestore.");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseManager", "Error removing user image from Firestore", e);
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });


    }
    /**
     *
     */
    public void getAllFacilities(final facilityCallback callback) {
        db.collection("Facilities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Facility> facilities = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String location = document.getString("location");
                    facilities.add(new Facility(name, location));
                }
                Log.d("FirebaseManager", "Fetched facilities: " + facilities.size());
                callback.onSuccess(facilities);
            } else {
                Log.e("FirebaseManager", "Failed to fetch facilities", task.getException());
                callback.onFailure(task.getException());
            }
        });
    }

    public void deleteFacility(String facilityName, OperationCallback callback) {
        db.collection("Facilities").document(facilityName) // Use a unique identifier for each facility
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }


    public interface facilityCallback {
        void onSuccess(List<Facility> facilities);
        void onFailure(Exception e);
    }

    /**
     *
     * @param event
     */

    public void deleteEvent(Event event) {
        CollectionReference eventsCollection = db.collection("events");
        eventsCollection.document(event.getId().toString()).delete();
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

    // Interface for update callback
    public interface OperationCallback {
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
        eventsCollection
                .document(event.getQrCodeHash())
                .set(eventData)
                .addOnSuccessListener(documentReference -> Log.d("FirebaseManager", "Event added with ID: " + event.getQrCodeHash()))
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
        eventsCollection.document(event.getQrCodeHash())
                .update(eventData)
                .addOnSuccessListener(querySnapshot -> {
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

    public void getUserEvents(String userId, final EventsListCallback callback) {
        CollectionReference eventsCollection = db.collection("events");
        String userIdString = userId.toString();
        List<Event> userEvents = new ArrayList<>();

        // Create tasks for different event queries
        Task<QuerySnapshot> organizerTask = eventsCollection
                .whereEqualTo("organizerId", userId)
                .get();

        Task<QuerySnapshot> waitlistTask = eventsCollection
                .whereArrayContains("waitingEntrants", userId)
                .get();

        Task<QuerySnapshot> invitedTask = eventsCollection
                .whereArrayContains("invitedEntrants", userId)
                .get();

        Task<QuerySnapshot> enrolledTask = eventsCollection
                .whereArrayContains("enrolledEntrants", userId)
                .get();

        Task<QuerySnapshot> cancelledTask = eventsCollection
                .whereArrayContains("cancelledEntrants", userId)
                .get();

        // Execute all queries in parallel
        Tasks.whenAllComplete(organizerTask, waitlistTask, invitedTask)
                .addOnSuccessListener(tasks -> {
                    try {
                        // Process organizer events
                        if (organizerTask.isSuccessful()) {
                            addEventsFromSnapshot(organizerTask.getResult(), userEvents);
                        }

                        // Process waitlist events
                        if (waitlistTask.isSuccessful()) {
                            addEventsFromSnapshot(waitlistTask.getResult(), userEvents);
                        }

                        // Process invited events
                        if (invitedTask.isSuccessful()) {
                            addEventsFromSnapshot(invitedTask.getResult(), userEvents);
                        }

                        // Process enrolled events
                        if (enrolledTask.isSuccessful()) {
                            addEventsFromSnapshot(enrolledTask.getResult(), userEvents);
                        }

                        // Process cancelled events
                        if (cancelledTask.isSuccessful()) {
                            addEventsFromSnapshot(cancelledTask.getResult(), userEvents);
                        }

                        // Sort events by date and return
                        userEvents.sort(Comparator.comparing(Event::getDate));
                        callback.onSuccess(userEvents);

                    } catch (Exception e) {
                        callback.onFailure(new Exception("Error processing events", e));
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(new Exception("Error fetching events", e)));
    }

    // Helper method to process QuerySnapshot and add events to the list
    private void addEventsFromSnapshot(QuerySnapshot snapshot, List<Event> eventsList) {
        for (DocumentSnapshot doc : snapshot) {
            eventsList.add(Event.fromFirestoreDocument(doc));
        }
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


    public void getNotificationsByUserId(UUID userId, final NotificationsListCallback callback) {
        CollectionReference notificationsCollection = db.collection("notification");

        // Convert UUID to String for Firestore query
        String userIdString = userId.toString();

        notificationsCollection
                .whereEqualTo("userid", userIdString) // Filter notifications by user ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Notification> notifications = new ArrayList<>();
                        // QueryDocumentSnapshot
                        for (DocumentSnapshot document : task.getResult()) {
                            Notification notification = new Notification(
                                    document.getString("Event_title"),
                                    document.getString("message"),
                                    document.getTimestamp("time_stamp") != null ?
                                            document.getTimestamp("time_stamp").toDate().toString() : "No timestamp",
                                    document.getString("userid"), // Add a comma here
                                    document.getBoolean("isDelivered") != null ?
                                            document.getBoolean("isDelivered") : false // Default to false if null
                            );
                            notifications.add(notification);
                        }
                        callback.onSuccess(notifications); // Return the fetched notifications
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException() :
                                new Exception("Unknown error occurred."));
                    }
                });
    }



    // Callback interface for retrieving notifications
    public interface NotificationsListCallback {
        void onSuccess(List<Notification> notifications);
        void onFailure(Exception e);
    }

    public void checkUserMessages(UUID userId, final MessageCheckCallback callback) {
        CollectionReference notificationsCollection = db.collection("notification");

        notificationsCollection
                .whereEqualTo("userid", userId.toString()) // Check for messages for the user
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean hasMessages = !task.getResult().isEmpty(); // Check if there are any messages
                        callback.onCheckComplete(hasMessages);
                    } else {
                        callback.onFailure(task.getException() != null ?
                                task.getException() :
                                new Exception("Unknown error occurred."));
                    }
                });
    }

    // Callback interface for checking messages
    public interface MessageCheckCallback {
        void onCheckComplete(boolean hasMessages);
        void onFailure(Exception e);
    }

    public void updateNotificationsAsDelivered(UUID userId) {
        CollectionReference notificationsCollection = db.collection("notification");
        notificationsCollection
                .whereEqualTo("userid", userId.toString()) // Find all notifications for the user
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Update each notification's isDelivered status to true
                            notificationsCollection.document(document.getId())
                                    .update("isDelivered", true)
                                    .addOnSuccessListener(aVoid -> Log.d("FirebaseManager", "Notification marked as delivered."))
                                    .addOnFailureListener(e -> Log.e("FirebaseManager", "Error marking notification as delivered", e));
                        }
                    } else {
                        Log.e("FirebaseManager", "Error fetching notifications: ", task.getException());
                    }
                });
    }


    /**
     * Retrieves all facilities from the Firestore database.
     *
     * @param callback A callback to return the list of facilities.
     */
    public void getAllFacilities(final FacilitiesListCallback callback) {
        CollectionReference facilitiesCollection = db.collection("Facilities");

        facilitiesCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Facility> facilities = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Facility facility = document.toObject(Facility.class);
                            facilities.add(facility);
                        }
                        callback.onSuccess(facilities);
                    } else {
                        Log.e("FirebaseManager", "Error getting facilities: ", task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }


    /**
     * Retrieves facilities for a specific user from Firestore.
     *
     * @param userId The UUID of the user whose facilities to retrieve.
     * @param callback A callback to return the list of facilities.
     */
    public void getFacilitiesByUserId(UUID userId, final FacilitiesListCallback callback) {
        CollectionReference facilitiesCollection = db.collection("Facilities");

        // Convert UUID to String for Firestore query
        String userIdString = userId.toString();

        facilitiesCollection
                .whereEqualTo("userId", userIdString) // Filter facilities by user ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Facility> facilities = new ArrayList<>();
                        // QueryDocumentSnapshot
                        for (DocumentSnapshot document : task.getResult()) {
                            Facility facility = document.toObject(Facility.class); // Convert document to Facility object
                            facilities.add(facility);
                        }
                        callback.onSuccess(facilities); // Return the fetched facilities
                    } else {
                        Log.e("FirebaseManager", "Error getting facilities: ", task.getException());
                        callback.onFailure(task.getException() != null ?
                                task.getException() :
                                new Exception("Unknown error occurred."));
                    }
                });
    }

    /**
     * Add facilities for a specific user to Firestore.
     *
     * @param facility The Facility to add.
     */
    public void addFacility(Facility facility, OnFacilityAddedListener listener) {
        db.collection("Facilities")
                .add(facility)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FirebaseManager", "Facility added with ID: " + documentReference.getId());
                    listener.onFacilityAdded(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("FirebaseManager", "Error adding facility", e);
                    listener.onFailure(e);
                });
    }

    public interface OnFacilityAddedListener {
        void onFacilityAdded(String facilityId); // Called when facility is added successfully
        void onFailure(Exception e); // Called when there is an error
    }

    // Callback interface for retrieving facilities
    public interface FacilitiesListCallback {
        void onSuccess(List<Facility> facilities);
        void onFailure(Exception e);
    }

    public void deleteFacility(String facilityName, OnFacilityDeletedListener listener) {
        CollectionReference facilitiesCollection = db.collection("Facilities");

        facilitiesCollection
                .whereEqualTo("name", facilityName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming there is only one facility with that name
                        for (DocumentSnapshot document : task.getResult()) {
                            facilitiesCollection.document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FirebaseManager", "Facility deleted successfully.");
                                        listener.onFacilityDeleted();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirebaseManager", "Error deleting facility", e);
                                        listener.onFailure(e);
                                    });
                        }
                    } else {
                        Log.e("FirebaseManager", "Facility not found: " + facilityName);
                        listener.onFailure(new Exception("Facility not found."));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseManager", "Error querying facilities: ", e);
                    listener.onFailure(e);
                });
    }


    // Callback interface for deleting facilities
    public interface OnFacilityDeletedListener {
        void onFacilityDeleted();
        void onFailure(Exception e);
    }
}

