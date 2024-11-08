package com.example.fusion1_events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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

public class EventsPageActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;
    private FloatingActionButton fabAddEvent;
    private BottomNavigationView bottomNavigation;
    private FirebaseManager firebaseManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_page);

        // Get user data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getParcelable("user");

            // Load profile image if it exists
            String imagePath = extras.getString("image_path");
            if (imagePath != null) {
                try {
                    FileInputStream fis = this.openFileInput(imagePath);
                    Bitmap profileImage = BitmapFactory.decodeStream(fis);
                    fis.close();
                    currentUser.setProfileImage(profileImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        // Initialize views
        rvEvents = findViewById(R.id.rvEvents);
        fabAddEvent = findViewById(R.id.fabAddEvent);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup FAB click listener
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EventCreationActivity
                Intent intent = new Intent(EventsPageActivity.this, EventCreationActivity.class);

                // Pass the current user
                intent.putExtra("user", currentUser);

                startActivity(intent);
            }
        });

        // Load fake event data for testing
        loadUserEvents();
    }

    private void setupRecyclerView() {
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(new ArrayList<>());
        rvEvents.setAdapter(eventAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.events); // Set events as selected
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            // Handle navigation item clicks here
            // For now, just return true to indicate the item was selected
            return true;
        });
    }

    private void loadUserEvents() {
        // Make sure we have a current user
        if (currentUser == null) {
            Toast.makeText(this, "No user data available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator (optional)
        // progressBar.setVisibility(View.VISIBLE);

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
}
