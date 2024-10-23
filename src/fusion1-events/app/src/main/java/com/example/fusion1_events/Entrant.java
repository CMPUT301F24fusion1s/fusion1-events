package com.example.fusion1_events;

import android.app.Notification;
import android.location.Location;
import android.media.Image;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

public class Entrant extends User{
    protected Image profileImage;
    protected Location location;
    // By default user's notification is on.
    protected boolean notificationEnabled = true;

    public Entrant(String email, String name, String role, String phoneNumber, Image profileImage, Location location, boolean notificationEnabled) {
        super(email, name, role, phoneNumber);
        this.profileImage = profileImage;
        this.location = location;
        this.notificationEnabled = notificationEnabled;
    }


    public void joinWaitlist(Event event){
       // event.getWaitlist().joinEntrent(this);
    }
    public void quitWaitlist(Event event){
        //event.getWaitlist().ommitEntrent(this);
    }
    public void acceptOrganizerInvitation(Event event){
       // event.joinAttendeeList(this);
    }
    public void rejectOrganizerInvitation(Event event){
        //event.addToRejectionList(this);
    }
    public void uploadProfilePicture(Image image){
        if(image != null)
            this.profileImage = image;
    }
    public void removeProfilePicture(){
        // not sure if we should generate a default profile picture.
        this.profileImage = null;
    }
    public void turnNotificationOn(){
        this.notificationEnabled = true;
    }
    public void turnNotificationOff(){
        this.notificationEnabled = false;
    }


}