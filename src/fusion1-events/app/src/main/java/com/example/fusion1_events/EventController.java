package com.example.fusion1_events;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.UUID;

public class EventController {
    // Attributes for FirebaseManager reference
    private final FirebaseManager firebaseManager;

    // Constructor
    public EventController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    public Event createEvent(UUID eventId, UUID organizerId, String name, Date date, String location, String description, Bitmap poster, int capacity, int waitlistLimit, Boolean geolocationRequired) {
        return new Event(eventId, organizerId, name, date, location, description, poster, capacity, waitlistLimit, geolocationRequired);
    }

    /**
     * Creates a new event with the specified details.
     *
     * @param event The event to be saved.
     */
    public void saveEvent(Event event) {
        firebaseManager.storeNewEvent(event);
    }

    /**
     * Updates an existing event with the specified details.
     *
     * @param event The event to be updated.
     */
    public void updateEvent(Event event) {
        firebaseManager.updateExistingEvent(event);
    }
}
