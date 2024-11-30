package com.example.fusion1_events;

import java.util.List;

public class AdminController {
    // Attributes for FirebaseManager reference
    private FirebaseManager firebaseManager;


    /**
     * Constructor to initialize UserController with a FirebaseManager reference.
     *
     * @param firebaseManager The FirebaseManager instance used to interact with Firebase.
     */
    public AdminController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    public void getAllUsers(final FirebaseManager.UsersListCallback callback) {
        firebaseManager.getAllusers(new FirebaseManager.UsersListCallback() {
            @Override
            public void onScuccess(List<Entrant> users) {
                // Pass the result to the provided callback
                callback.onScuccess(users);
            }

            @Override
            public void onFailure(Exception e) {
                // Pass the error to the provided callback
                callback.onFailure(e);
            }
        });
    }

    public void deleteUser(String deviceId){
        firebaseManager.deleteUser(deviceId);
    }

    public void getAllEvents(FirebaseManager.EventsListCallback eventsListCallback) {
        firebaseManager.getAllEvents(new FirebaseManager.EventsListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                eventsListCallback.onSuccess(events);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    public void deleteEvent(Event event) {
        firebaseManager.deleteEvent(event);
    }

    public void updateEvent(Event event) {
        firebaseManager.updateExistingEvent(event);
    }
}
