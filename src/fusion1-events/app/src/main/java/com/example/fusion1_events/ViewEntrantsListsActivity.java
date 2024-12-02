package com.example.fusion1_events;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewEntrantsListsActivity extends AppCompatActivity {
    private Event.WaitList waitList;
    private Spinner entrantClassSpinner;
    private RecyclerView waitlistRecyclerView;
    private WaitlistAdapter waitlistAdapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrants_lists);

        firebaseManager = new FirebaseManager();

        // Get waitlist from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            waitList = extras.getParcelable("waitlist");
        } else {
            Toast.makeText(this, "Error: Waitlist data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup UI
        setupUI();
    }

    private void setupUI() {
        // Set up back button with user data passing
        findViewById(R.id.backText).setOnClickListener(v -> finish());
        entrantClassSpinner = findViewById(R.id.entrantClassSpinner);
        waitlistRecyclerView = findViewById(R.id.waitlistRecyclerView);
        setupRecyclerView();

        // Set up spinner listener
        entrantClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                filterEntrantsByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupRecyclerView() {
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitlistAdapter = new WaitlistAdapter(new ArrayList<>());
        waitlistRecyclerView.setAdapter(waitlistAdapter);
    }

    private void filterEntrantsByStatus(String status) {
        // Filter the entrants based on the selected status
        ArrayList<String> filteredEntrants = new ArrayList<>();

        switch (status) {
            case "Waiting":
                filteredEntrants.addAll(waitList.getWaitingEntrants());
                break;
            case "Invited":
                filteredEntrants.addAll(waitList.getInvitedEntrants());
                break;
            case "Enrolled":
                filteredEntrants.addAll(waitList.getEnrolledEntrants());
                break;
            case "Cancelled":
                filteredEntrants.addAll(waitList.getCancelledEntrants());
                break;
            default:
                filteredEntrants.addAll(waitList.getAllEntrants());
                break;
        }

        firebaseManager.getUsersById(filteredEntrants, new FirebaseManager.UsersListCallback() {
            @Override
            public void onSuccess(List<Entrant> users) {
                List<String> entrantNames = new ArrayList<>();
                for (Entrant user : users) {
                    entrantNames.add(user.getName());
                }
                waitlistAdapter.setEntrants(entrantNames);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ViewEntrantsListsActivity.this,
                        "Error loading entrants: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
