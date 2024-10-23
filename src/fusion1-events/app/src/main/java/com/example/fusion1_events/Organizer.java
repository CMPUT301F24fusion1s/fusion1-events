package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.provider.ContactsContract;

import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;

public class Organizer extends Entrant{
    private Facility facility;
    private ArrayList<Event> eventList;

    // Updated constructor
    public Organizer(String name, String email, String phoneNumber,String role, Image profileImage, Location location, boolean notificationEnabled, Facility facility, ArrayList<Event> eventList) {
        super(email, name, phoneNumber,role, profileImage, location, notificationEnabled);
        this.facility = facility;
        this.eventList = eventList;
    }


    public Facility getFacility() {
        return facility;
    }
    // not sure if we should have.
    public void setFacility(@NonNull Facility facility) {
        this.facility = facility;
    }

//    public void createEvent(int capacity,Barcodde qrCode){
//        eventList.add(new Event(capacity, facility, qrCode));
//    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }
}