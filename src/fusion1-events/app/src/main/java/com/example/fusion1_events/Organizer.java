package com.example.fusion1_events;

import android.media.Image;
import android.provider.ContactsContract;

import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;

public class Organizer extends Entrant{
    private Facility facility;
    private ArrayList<Event> eventList;

    public Organizer(String name, ContactsContract.CommonDataKinds.Email email, String phoneNumber, Image image,@NonNull Facility facility){
        super(name, email, phoneNumber, image);
        this.facility = facility;
    }

    public Facility getFacility() {
        return facility;
    }
    // not sure if we should have.
    public void setFacility(@NonNull Facility facility) {
        this.facility = facility;
    }

    public void createEvent(int capacity,Barcodde qrCode){
        eventList.add(new Event(capacity, facility, qrCode));
    }


}
