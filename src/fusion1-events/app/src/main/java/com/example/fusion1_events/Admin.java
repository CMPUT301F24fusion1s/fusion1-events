package com.example.fusion1_events;

import android.provider.ContactsContract;

import androidx.lifecycle.Lifecycle;

import java.util.UUID;

public class Admin extends User{


    public Admin(String email, String name, String role, String phoneNumber, String userId, String deviceId) {
        super(email, name, role, phoneNumber, userId, deviceId);
    }

    // no argument constructor

    public Admin() {
        super();
    }


    public void removeEvent(Event event){
        // remove this event from everywhere.
        //event.getOrganizer().getEventList().remove(event);
    }
    public void removeUser(User user){
        // not sure how to do it
    }
    public void removeImage(Entrant entrant){
        entrant.removeProfilePicture();
    }
   // public void removeQrCode(QRcode qRcode){
     //   FireBaseManager.deleteQrCode(qRcode);
    //}
}