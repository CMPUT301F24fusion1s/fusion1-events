package com.example.fusion1_events;

import android.app.Notification;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.UUID;

public class Entrant extends User{
    protected Bitmap profileImage;
    protected Location location;
    private ArrayList<Event> eventList;
    // By default user's notification is on.
    protected boolean notificationEnabled = true;
    private String profileImageUrl;

    public Entrant(String email, String name, String role, String phoneNumber, String userId, String deviceId, Bitmap profileImage, Location location, boolean notificationEnabled) {
        super(email, name, role, phoneNumber, userId, deviceId);
        this.profileImage = profileImage;
        this.location = location;
        this.notificationEnabled = notificationEnabled;
    }

    // no argument constractor
    public Entrant()
    {
        super();
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
    public void uploadProfilePicture(Bitmap image){
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


    public Bitmap getProfileImage() {
        return this.profileImage;
    }
}
