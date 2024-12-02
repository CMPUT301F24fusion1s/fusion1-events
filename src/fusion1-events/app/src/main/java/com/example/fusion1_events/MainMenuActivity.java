package com.example.fusion1_events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.NotificationManager;
import android.app.NotificationChannel;

import java.util.List;
import java.util.UUID;

/**
 * The MainMenuActivity class represents the main menu screen of the application.
 * It provides functionalities for navigating to user profile and editing profile information.
 */
public class MainMenuActivity extends BaseActivity {

    /**
     * Called when the activity is first created. Initializes the user interface components and sets up the event listeners.
     *
     * @param savedInstancesState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied.
     */
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        // Log user details for testing
        Log.d("MainMenuActivity", "User Name: " + currentUser.getName());

        // Find the Profile button and other views
        ImageButton editProfile = findViewById(R.id.btnProfile);

        // Set click listener for the profile button
        editProfile.setOnClickListener(v -> showUserProfileFragment(currentUser));

        // Check for messages when the activity is created
        checkForMessages();
    }

    private void checkForMessages() {
        if (currentUser == null || currentUser.getUserId() == null) {
            Toast.makeText(this, "Current user not found", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            firebaseManager.getNotificationsByUserId(userId, new FirebaseManager.NotificationsListCallback() {
                @Override
                public void onSuccess(List<Notification> notifications) {
                    for (Notification notification : notifications) {
                        if (!notification.isDelivered()) { // Check if not delivered
                            sendNotification("Fusion1", "You have a new message!");

                            // Update the notification as delivered
                            firebaseManager.updateNotificationsAsDelivered(userId);
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("MainMenuActivity", "Error fetching notifications: ", e);
                    Toast.makeText(MainMenuActivity.this,
                            "Error loading notifications: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid user ID format", Toast.LENGTH_LONG).show();
            Log.e("MainMenuActivity", "Invalid user ID format: ", e);
        }
    }

    private void sendNotification(String title, String message) {
        // Create a notification channel for Android O and above
        String channelId = "event_notifications";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Event Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Use a valid icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to HIGH for heads-up notifications
                .setAutoCancel(true); // Automatically remove the notification when tapped

        // Send the notification
        notificationManager.notify(0, builder.build());
        Log.d("MainMenuActivity", "Notification sent: " + title + " - " + message);
    }

    /**
     * @return The layout resource ID for the current activity.
     */
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_menu;
    }

    /**
     * @return The navigation menu item ID for the current activity.
     */
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.home;
    }
}
