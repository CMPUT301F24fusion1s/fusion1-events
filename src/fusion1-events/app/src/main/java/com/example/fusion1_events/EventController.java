package com.example.fusion1_events;

import android.graphics.Bitmap;

import java.util.Date;

import kotlin.NotImplementedError;

public class EventController {
    // Attributes for FirebaseManager reference
    private final FirebaseManager firebaseManager;

    // Constructor
    public EventController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    // Placeholder methods for event management
    public void createEvent(String organizerId, String name, Date date, String location, String description, Bitmap poster, int capacity) {
        Event event = new Event(name, date, location, description, poster, capacity);
        firebaseManager.storeNewEvent(event);
    }

    public void joinWaitingList(String userId, String eventId) {
        // TODO: Implement logic to add entrant to waiting list
        throw new NotImplementedError("Method not implemented.");
    }

    public void drawEntrants(String eventId, int count) {
        // TODO: Implement logic for drawing entrants
        throw new NotImplementedError("Method not implemented.");
    }
}
