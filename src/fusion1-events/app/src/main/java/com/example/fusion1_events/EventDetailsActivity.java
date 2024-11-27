package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EventDetailsActivity extends AppCompatActivity {
    private Event event;
    private User currentUser;
    private DeviceManager deviceManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        // Initialize managers
        deviceManager = new DeviceManager(this);
        firebaseManager = new FirebaseManager();

        // Get event and load poster from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event = extras.getParcelable("event");
            loadEventPoster(extras.getString("poster_image_path"));
        }

        // Load current user first, then setup UI
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        firebaseManager.getUserByDeviceId(deviceManager.getOrCreateDeviceId(), new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                // Now that we have the user, setup the UI
                setupUI();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EventDetailsActivity.this,
                        "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupUI() {
        // Set up back button with user data passing
        findViewById(R.id.backText).setOnClickListener(v -> navigateToEventsPage());
        setupEventDetails();
        setupPrimaryActionButton();
    }

    private void navigateToEventsPage() {
        Intent intent = new Intent(this, EventsPageActivity.class);

        // Pass user data
        if (currentUser != null) {
            Bundle bundle = new Bundle();
            String tempFileName = "temp_profile_image.jpg";

            try {
                if (currentUser.getProfileImage() != null) {
                    FileOutputStream fos = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                    currentUser.getProfileImage().compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            bundle.putParcelable("user", currentUser);
            bundle.putString("profile_image_path", tempFileName);
            intent.putExtras(bundle);
        }

        startActivity(intent);
        finish();
    }

    private void loadEventPoster(String imagePath) {
        if (imagePath != null && event != null) {
            try {
                FileInputStream fis = this.openFileInput(imagePath);
                Bitmap posterImage = BitmapFactory.decodeStream(fis);
                fis.close();
                event.setPoster(posterImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupEventDetails() {
        if (event == null) return;

        // Find views
        TextView eventName = findViewById(R.id.eventTitle);
        TextView eventDate = findViewById(R.id.eventDateTime);
        TextView eventLocation = findViewById(R.id.location);
        TextView eventMaxParticipants = findViewById(R.id.maxParticipants);
        TextView eventGeolocationRequired = findViewById(R.id.geolocationRequired);
        TextView eventDescription = findViewById(R.id.description);
        ImageView eventPoster = findViewById(R.id.eventPoster);

        // Set event details
        eventName.setText(event.getName());
        eventDate.setText(event.getDate().toString());
        eventLocation.setText(event.getLocation());
        eventMaxParticipants.setText(String.valueOf(event.getCapacity()));
        eventGeolocationRequired.setText(event.getGeolocationRequired() ? "Yes" : "No");
        eventDescription.setText(event.getDescription());
        if (event.getPoster() != null) {
            eventPoster.setImageBitmap(event.getPoster());
        }
    }

    private void setupPrimaryActionButton() {
        Button primaryActionButton = findViewById(R.id.primaryActionButton);

        if (currentUser != null) {
            if (currentUser.getUserId().equals(event.getOrganizerId().toString())) {
                // User is the organizer
                primaryActionButton.setText("Edit Event");
                primaryActionButton.setOnClickListener(v -> editEvent());
            } else {
                // User is not the organizer
                if (event.getWaitlist().getWaitingEntrants().contains(currentUser.getUserId())) {
                    primaryActionButton.setText("Leave Waitlist");
                    primaryActionButton.setOnClickListener(v -> leaveWaitlist());
                } else if (event.getWaitlist().getInvitedEntrants().contains(currentUser.getUserId())) {
                    primaryActionButton.setText("Reply to Invite");
                    primaryActionButton.setOnClickListener(v -> replyToInvite());
                } else {
                    primaryActionButton.setText("Join Waitlist");
                    primaryActionButton.setOnClickListener(v -> joinWaitlist());
                }
            }
        }
    }

    private void editEvent() {
        // TODO: Implement edit event functionality
        Toast.makeText(this, "Edit Event button clicked", Toast.LENGTH_SHORT).show();
    }

    private void joinWaitlist() {
        event.getWaitlist().addWaitingEntrant(currentUser.getUserId());
        firebaseManager.updateExistingEvent(event);
        setupPrimaryActionButton(); // Refresh button state
        Toast.makeText(EventDetailsActivity.this,
                "Successfully joined waitlist", Toast.LENGTH_SHORT).show();
    }

    private void leaveWaitlist() {
        event.getWaitlist().removeWaitingEntrant(currentUser.getUserId());
        firebaseManager.updateExistingEvent(event);
        setupPrimaryActionButton(); // Refresh button state
        Toast.makeText(EventDetailsActivity.this,
                "Successfully left waitlist", Toast.LENGTH_SHORT).show();
    }

    private void replyToInvite() {
        Toast.makeText(EventDetailsActivity.this,
                "User clicked Reply to Invite button", Toast.LENGTH_SHORT).show();
    }
}
