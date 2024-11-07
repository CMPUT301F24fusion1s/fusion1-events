package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import androidx.lifecycle.Lifecycle;

import java.util.UUID;

/**
 * The Admin class represents an administrator user in the system, who has special privileges such as managing events,
 * removing users, and modifying entrant data. This class extends the User class, inheriting common user attributes and behavior,
 * while providing additional functionality specific to administrative roles.
 */
public class Admin extends User{

    /**
     * Constructor to initialize an Admin object with specified details.
     *
     * @param email The email address of the admin.
     * @param name The name of the admin.
     * @param role The role of the user (e.g., "Admin").
     * @param phoneNumber The phone number of the admin.
     * @param userId The user ID of the admin.
     * @param deviceId The device ID associated with the admin.
     * @param profileImage The profile image of the admin as a Bitmap.
     */
    public Admin(String email, String name, String role, String phoneNumber, String userId, String deviceId, Bitmap profileImage) {
        super(email, name, role, phoneNumber, userId, deviceId, profileImage);
    }

    /**
     * No-argument constructor to create an empty Admin object.
     */
    public Admin() {
        super();
    }

    /**
     * Removes an event from the system. This method is responsible for deleting the event from all relevant locations.
     *
     * @param event The Event object to be removed.
     */
    public void removeEvent(Event event){
        // remove this event from everywhere.
        //event.getOrganizer().getEventList().remove(event);
    }

    /**
     * Removes a user from the system.
     *
     * @param user The User object to be removed.
     */
    public void removeUser(User user){
        // not sure how to do it
    }

    /**
     * Removes the profile image of an entrant.
     *
     * @param entrant The Entrant object whose profile image is to be removed.
     */
    public void removeImage(Entrant entrant){
        entrant.setProfileImage(null);
    }
   // public void removeQrCode(QRcode qRcode){
     //   FireBaseManager.deleteQrCode(qRcode);
    //}
}