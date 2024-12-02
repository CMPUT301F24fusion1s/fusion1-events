package com.example.fusion1_events;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.UUID;

/**
 * Controller class for the Event entity. This class is responsible for creating and updating events.
 */
public class EventController {
    // Attributes for FirebaseManager reference
    private final FirebaseManager firebaseManager;

    public EventController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    /**
     * Creates a new event with the specified details.
     *
     * @param eventId The unique identifier of the event.
     * @param organizerId The unique identifier of the event organizer.
     * @param name The name of the event.
     * @param date The date of the event.
     * @param location The location of the event.
     * @param description The description of the event.
     * @param poster The poster of the event.
     * @param capacity The capacity of the event.
     * @param waitlistLimit The waitlist limit of the event.
     * @param geolocationRequired The geolocation requirement of the event.
     * @return The created event.
     */
    public Event createEvent(UUID eventId, String organizerId, String name, Date date, String location, String description, Bitmap poster, int capacity, int waitlistLimit, Boolean geolocationRequired) {
        return new Event(eventId, organizerId, name, date, location, description, poster, capacity, waitlistLimit, geolocationRequired);
    }

    /**
        * Saves a new event to the database.
     *
     * @param event The event to be saved.
     */
    public void saveEvent(Event event) {
        firebaseManager.storeNewEvent(event);
    }

    /**
     * Updates an existing event in the database.
     *
     * @param event The event to be updated.
     */
    public void updateEvent(Event event) {
        firebaseManager.updateExistingEvent(event);
    }
}
