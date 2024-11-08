package com.example.fusion1_events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventsPageActivity extends BaseActivity {

    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;
    private FloatingActionButton fabAddEvent;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        // Initialize views
        rvEvents = findViewById(R.id.rvEvents);
        fabAddEvent = findViewById(R.id.fabAddEvent);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup FAB click listener
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(EventCreationActivity.class);
            }
        });

        // Edit profile button initialization
        ImageButton editProfile = findViewById(R.id.btnProfile);
        editProfile.setOnClickListener(v -> showUserProfileFragment(currentUser));

        loadUserEvents();
    }

    private void setupRecyclerView() {
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(new ArrayList<>());

        eventAdapter.setOnEventClickListener(event -> {
            showEventDetailsFragment(event, currentUser.getDeviceId());
        });

        rvEvents.setAdapter(eventAdapter);
    }

    private void loadUserEvents() {
        // Make sure we have a current user
        if (currentUser == null) {
            Toast.makeText(this, "No user data available", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseManager.getUserEvents(UUID.fromString(currentUser.getUserId()),
                new FirebaseManager.EventsListCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        runOnUiThread(() -> {
                            // Hide loading indicator if you added one
                            // progressBar.setVisibility(View.GONE);

                            if (events.isEmpty()) {
                                // Show empty state message or view
                                Toast.makeText(EventsPageActivity.this,
                                        "No events found",
                                        Toast.LENGTH_SHORT).show();
                            }

                            eventAdapter.updateEvents(events);
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> {
                            // Hide loading indicator if you added one
                            // progressBar.setVisibility(View.GONE);

                            Toast.makeText(EventsPageActivity.this,
                                    "Error loading events: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load events from Firebase when the activity resumes
        loadUserEvents();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_events_page;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.events;
    }
}
