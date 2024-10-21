package com.example.fusion1_events;

import android.app.Notification;
import android.provider.ContactsContract;

public class Entrant extends User{
    public Entrant(String name, ContactsContract.CommonDataKinds.Email email, String phoneNumber) {
        super(name, email, phoneNumber);
    }

    public void joinWaitlist(Event event){
        event.getWaitlist().joinEntrent(this);
    }
    public void quitWaitlist(Event event){
        event.getWaitlist().ommitEntrent(this);
    }
    public void acceptOrganizerInvitation(Event event){
        event.joinAttendeeList(this);
    }
    public void rejectOrganizerInvitation(Event event){
        event.addToRejectionList(this);
    }
}
