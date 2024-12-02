package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventsPageActivity extends BaseActivity {

    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;
    private FloatingActionButton fabAddEvent;
    private TextView tvManageFacility;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        // Initialize views
        rvEvents = findViewById(R.id.rvEvents);
        fabAddEvent = findViewById(R.id.fabAddEvent);
        tvManageFacility = findViewById(R.id.tvManageFacility);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup FAB click listener
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(EventCreationActivity.class);
            }
        });

        // Manage Facility click listener
        tvManageFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(FacilityPageActivity.class);
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

        eventAdapter.setOnEventClickListener(this::navigateToEventDetails);

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
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    /**
     * Navigates to the specified activity class.
     *
     * @param event The event to show details for.
     */
    private void navigateToEventDetails(Event event) {
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
