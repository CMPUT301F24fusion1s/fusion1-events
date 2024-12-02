package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationPageActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.notification_page);

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize notification list and adapter
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        ImageButton editProfile = findViewById(R.id.btnProfile);
        editProfile.setOnClickListener(v -> showUserProfileFragment(currentUser));

        // Fetch notifications from Firestore based on the current user's ID
        fetchNotifications();
    }

    private void fetchNotifications() {
        if (currentUser == null || currentUser.getUserId() == null) {
            Toast.makeText(this, "Current user not found", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            UUID userId = UUID.fromString(currentUser.getUserId());
            firebaseManager.getNotificationsByUserId(userId, new FirebaseManager.NotificationsListCallback() {
                @Override
                public void onSuccess(List<Notification> notifications) {
                    notificationList.clear(); // Clear the existing list
                    notificationList.addAll(notifications); // Add the fetched notifications
                    notificationAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("NotificationPageActivity", "Error fetching notifications: ", e);
                    Toast.makeText(NotificationPageActivity.this,
                            "Error loading notifications: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid user ID format", Toast.LENGTH_LONG).show();
            Log.e("NotificationPage2", "Invalid user ID format: ", e);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.notification_page;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.notifications;
    }
}
