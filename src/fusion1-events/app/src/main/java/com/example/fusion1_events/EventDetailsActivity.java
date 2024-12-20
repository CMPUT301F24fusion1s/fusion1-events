package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

/**
 * Activity for displaying event details and allowing users to do actions based on their role.
 */
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

    /**
     * Load the current user from Firebase using the device ID.
     */
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

    /**
     * Set up the basic UI elements and their actions.
     */
    private void setupUI() {
        // Set up back button with user data passing
        findViewById(R.id.backText).setOnClickListener(v -> navigateToEventsPage());
        setupEventDetails();
        setupPrimaryActionButton();
    }

    /**
     * Navigate back to the events page.
     */
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

    /**
     * Load the event poster image from the internal storage.
     *
     * @param imagePath The path to the image file.
     */
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

    /**
     * Populate the event details on the screen.
     */
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

    /**
     * Set up the primary action button based on the user's role in the event.
     */
    private void setupPrimaryActionButton() {
        Button primaryActionButton = findViewById(R.id.primaryActionButton);

        if (currentUser != null) {
            if (currentUser.getDeviceId().equals(event.getOrganizerId())) {
                // User is the organizer
                primaryActionButton.setText(R.string.manage_event);
                primaryActionButton.setOnClickListener(v -> manageEvent());
            } else {
                // User is not the organizer
                if (event.getWaitlist().getWaitingEntrants().contains(currentUser.getDeviceId())) {
                    primaryActionButton.setText(R.string.leave_waitlist);
                    primaryActionButton.setBackgroundColor(getResources().getColor(R.color.orange, null));
                    primaryActionButton.setOnClickListener(v -> leaveWaitlist());
                } else if (event.getWaitlist().getInvitedEntrants().contains(currentUser.getDeviceId())) {
                    primaryActionButton.setText(R.string.reply_to_invite);
                    primaryActionButton.setBackgroundColor(getResources().getColor(R.color.green, null));
                    primaryActionButton.setOnClickListener(v -> replyToInvite());
                } else if (event.getWaitlist().getEnrolledEntrants().contains(currentUser.getDeviceId())) {
                    primaryActionButton.setText(R.string.leave_event);
                    primaryActionButton.setBackgroundColor(getResources().getColor(R.color.red, null));
                    primaryActionButton.setOnClickListener(v -> cancelEnrolment());
                } else if (event.getWaitlist().getCancelledEntrants().contains(currentUser.getDeviceId())) {
                    primaryActionButton.setText(R.string.invitation_declined);
                    primaryActionButton.setBackgroundColor(getResources().getColor(R.color.gray, null));
                    primaryActionButton.setEnabled(false);
                } else {
                    primaryActionButton.setText(R.string.join_waitlist);
                    primaryActionButton.setBackgroundColor(getResources().getColor(R.color.black, null));
                    primaryActionButton.setOnClickListener(v -> joinWaitlist());
                }
            }
        }
    }

    /**
     * Perform the manage event action.
     * Used when the user is the organizer of the event.
     */
    private void manageEvent() {
        Intent intent = new Intent(this, ManageEventActivity.class);
        Bundle bundle = new Bundle();
        String tempFileName = "temp_event_poster.jpg";

        try {
            if (event.getPoster() != null) {
                FileOutputStream fos = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                event.getPoster().compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        bundle.putParcelable("event", event);
        bundle.putString("poster_image_path", tempFileName);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * Join the waitlist for the event.
     * This action is only available to users who are not the organizer, and are not already on the waitlist.
     */
    private void joinWaitlist() {
        event.getWaitlist().addWaitingEntrant(currentUser.getDeviceId());
        firebaseManager.updateExistingEvent(event);
        setupPrimaryActionButton(); // Refresh button state
        Toast.makeText(EventDetailsActivity.this,
                "Successfully joined waitlist", Toast.LENGTH_SHORT).show();
    }

    /**
     * Leave the waitlist for the event.
     * This action is only available to users who are on the waitlist.
     */
    private void leaveWaitlist() {
        event.getWaitlist().removeWaitingEntrant(currentUser.getDeviceId());
        firebaseManager.updateExistingEvent(event);
        setupPrimaryActionButton(); // Refresh button state
        Toast.makeText(EventDetailsActivity.this,
                "Successfully left waitlist", Toast.LENGTH_SHORT).show();
    }

    /**
     * Cancel enrolment for the event after previously accepting an invitation.
     * This action is only available to users who are enrolled in the event.
     */
    private void cancelEnrolment() {
        // Ask for confirmation, noting that this action cannot be undone
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Enrolment")
                .setMessage("Are you sure you want to leave the event? You cannot rejoin once you leave.")
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Cancel enrolment
                    event.getWaitlist().cancelEnrolledEntrant(currentUser.getDeviceId());
                    firebaseManager.updateExistingEvent(event);
                    setupPrimaryActionButton();
                    Toast.makeText(EventDetailsActivity.this, "Successfully left event", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // Handle cancel logic here
                    dialog.dismiss();
                });

        builder.create().show();
    }

    /**
     * Reply to an invitation to the event. Allows user to accept or decline the invitation.
     * This action is only available to users who have been invited to the event.
     */
    private void replyToInvite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reply to Invite")
                .setMessage("Would you like to accept or decline the invitation?")
                .setPositiveButton("Accept", (dialog, id) -> {
                    // Accept invite
                    event.getWaitlist().enrollInvitedEntrant(currentUser.getDeviceId());
                    firebaseManager.updateExistingEvent(event);
                    setupPrimaryActionButton();
                    Toast.makeText(EventDetailsActivity.this, "Invitation Accepted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Decline", (dialog, id) -> {
                    // Decline invite
                    event.getWaitlist().cancelInvitedEntrant(currentUser.getDeviceId());

                    // Run the lottery again to replace the declined entrant
                    event.reRunLottery();

                    firebaseManager.updateExistingEvent(event);

                    setupPrimaryActionButton();
                    Toast.makeText(EventDetailsActivity.this, "Invitation Declined", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", (dialog, id) -> {
                    // Handle cancel logic here
                    dialog.dismiss();
                });

        builder.create().show();
    }
}
