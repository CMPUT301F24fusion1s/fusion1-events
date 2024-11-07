package com.example.fusion1_events;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.UUID;

import kotlin.NotImplementedError;

public class EventController {
    // Attributes for FirebaseManager reference
    private final FirebaseManager firebaseManager;

    // Constructor
    public EventController(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    /**
     * Creates a new event with the specified details.
     *
     * @param organizerId ID of the event organizer, should use the current user's ID.
     * @param name Name of the event.
     * @param date Date of the event.
     * @param location Location of the event.
     * @param description Description of the event.
     * @param poster Poster image for the event.
     * @param capacity Capacity of the event.
     */
    public void createEvent(UUID organizerId, String name, Date date, String location, String description, Bitmap poster, int capacity, Boolean geolocationRequired) {
        Event event = new Event(organizerId, name, date, location, description, poster, capacity, geolocationRequired);
        firebaseManager.storeNewEvent(event);
    }

    /**
     * Allow a user to join the waiting list for an event.
     *
     * @param userId ID of the user joining the waiting list.
     * @param eventId ID of the event to join the waiting list for.
     */
    public void joinWaitingList(UUID userId, UUID eventId) {
        // TODO: Implement logic to add entrant to waiting list
        throw new NotImplementedError("Method not implemented.");
    }

    /**
     * Draw a specified number of entrants from the waiting list for an event.
     *
     * @param eventId ID of the event to draw entrants for.
     * @param count Number of entrants to draw.
     */
    public void drawEntrants(UUID eventId, int count) {
        // TODO: Implement logic for drawing entrants
        throw new NotImplementedError("Method not implemented.");
    }
}
