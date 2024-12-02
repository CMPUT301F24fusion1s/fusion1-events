package com.example.fusion1_events;

public class Notification {
    private String eventTitle; // Corresponds to "Event_title"
    private String message;     // Corresponds to "message"
    private String timeStamp;   // Corresponds to "time_stamp"
    private String userId;      // Corresponds to "userid"
    private boolean isDelivered;

    // Empty constructor required for Firestore
    public Notification() {
    }

    // Constructor for creating a Notification object
    public Notification(String eventTitle, String message, String timeStamp, String userId, boolean isDelivered) {
        this.eventTitle = eventTitle;
        this.message = message;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.isDelivered = isDelivered;
    }

    // Getters
    public String getEventTitle() {
        return eventTitle;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }
}
