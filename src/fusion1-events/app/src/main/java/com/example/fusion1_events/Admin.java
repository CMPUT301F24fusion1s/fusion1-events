package com.example.fusion1_events;

import android.provider.ContactsContract;

import androidx.lifecycle.Lifecycle;

public class Admin extends User{

    public Admin(String name, ContactsContract.CommonDataKinds.Email email, String phoneNumber) {
        super(name, email, phoneNumber);
    }

    public void removeEvent(Event event){
        // remove this event from everywhere.
        event.getOrganizer().getEventList().remove(event);
    }
    public void removeUser(User user){
        // not sure how to do it
    }
    public void removeImage(Entrant entrant){
        entrant.removeProfilePicture();
    }
    public void removeQrCode(QRcode qRcode){
        FireBaseManager.deleteQrCode(qRcode);
    }
}
