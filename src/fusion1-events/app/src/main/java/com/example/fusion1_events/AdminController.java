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

    public  List<Entrant> getallusers()
    {
       List<Entrant> usersList = firebaseManager.getAllusers(new FirebaseManager.UsersListCallback() {
            @Override
            public void onScuccess(List<Entrant> users) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        return usersList;
    }
}
