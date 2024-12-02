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

    /**
     * User methods fetching, deleting user and finally removing a user image
     * @param callback
     */
    public void getAllUsers(final FirebaseManager.UsersListCallback callback) {
        firebaseManager.getAllusers(new FirebaseManager.UsersListCallback() {
            @Override
            public void onSuccess(List<Entrant> users) {
                // Pass the result to the provided callback
                callback.onSuccess(users);
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

    public void removeUserImage(String userId, FirebaseManager.OperationCallback callback) {
        firebaseManager.removeUserImage(userId, new FirebaseManager.OperationCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }


    /**
     * Event methods of fetching, updating and deleting
     * @param eventsListCallback
     */
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


    /**
     *  facility stuff can you lend a nigger a pen
     */
    public void getAllFacilities(FirebaseManager.facilityCallback facilityCallback)
    {
        firebaseManager.getAllFacilities(new FirebaseManager.facilityCallback() {
            @Override
            public void onSuccess(List<Facility> facilities) {
                facilityCallback.onSuccess(facilities);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void deleteFacility(String facilityName, FirebaseManager.OperationCallback callback) {
        firebaseManager.deleteFacility(facilityName, callback);
    }

}
