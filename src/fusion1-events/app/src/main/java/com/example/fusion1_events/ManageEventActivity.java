package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageEventActivity extends AppCompatActivity {
    private Event event;
    private User currentUser;
    private DeviceManager deviceManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_event);

        // Initialize managers
        deviceManager = new DeviceManager(this);
        firebaseManager = new FirebaseManager();

        // Get event and load poster from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event = extras.getParcelable("event");
            loadEventPoster(extras.getString("poster_image_path"));
        } else {
            Toast.makeText(this, "Error: Event data not found", Toast.LENGTH_SHORT).show();
            finish();
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
                Toast.makeText(ManageEventActivity.this,
                        "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupUI() {
        // Set up back button with user data passing
        findViewById(R.id.backText).setOnClickListener(v -> navigateToEventDetailsPage());
        setupListeners();
    }

    private void navigateToEventDetailsPage() {
        Intent intent = new Intent(this, EventDetailsActivity.class);
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

    private void setupListeners() {
        findViewById(R.id.editEventBtn).setOnClickListener(v -> editEvent());
        findViewById(R.id.runLotteryBtn).setOnClickListener(v -> runLottery());
        findViewById(R.id.viewEntrantsBtn).setOnClickListener(v -> viewEntrants());
        findViewById(R.id.viewMapBtn).setOnClickListener(v -> viewMap());
        findViewById(R.id.sendNotificationBtn).setOnClickListener(v -> sendNotification());
        findViewById(R.id.deleteEventBtn).setOnClickListener(v -> deleteEvent());
    }

    private void editEvent() {
        Intent intent = new Intent(this, EventUpdateActivity.class);
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
        bundle.putParcelable("user", currentUser);
        bundle.putString("poster_image_path", tempFileName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void runLottery() {
        event.runLottery();
        firebaseManager.updateExistingEvent(event);
        Toast toast = Toast.makeText(this, "Lottery has been run", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void viewEntrants() {

    }

    private void viewMap() {
        List<String> entrantsID = event.getWaitlist().getEnrolledEntrants();
        UserController userController = new UserController(new FirebaseManager());

        List<Entrant> entrants = new ArrayList<>();
        for(String id : entrantsID)
        {
            userController.userLogin(id, new FirebaseManager.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    entrants.add((Entrant) user);
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);



    }

    private void sendNotification() {

    }

    private void deleteEvent() {

    }
}
