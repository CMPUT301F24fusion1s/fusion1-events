package com.example.fusion1_events;

public class Notification {
    private String message;
    private String type; // e.g., "urgent", "regular"
    private String timestamp; // Optional: to track when the notification was created

    public Notification() {
        // Firestore requires an empty constructor
    }

    public Notification(String message, String type, String timestamp) {
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
