package com.example.fusion1_events;

import java.util.ArrayList;
import java.util.List;

public class AdminController {
    // Attributes for FirebaseManager reference
    private FirebaseManager firebaseManager;
    private ArrayList<Entrant> userList;


    /**
     * Constructor to initialize UserController with a FirebaseManager reference.
     *
     * @param firebaseManager The FirebaseManager instance used to interact with Firebase.
     */
    public AdminController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    public void getallusers()
    {

        firebaseManager.getAllusers(new FirebaseManager.UsersListCallback() {
            @Override
            public void  onSuccess(List<Entrant> users) {
                userList = (ArrayList<Entrant>) users;
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public ArrayList<Entrant> getUserList() {
        return userList;
    }
}
