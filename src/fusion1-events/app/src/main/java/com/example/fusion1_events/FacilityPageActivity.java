package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FacilityPageActivity extends BaseActivity implements FacilityAdapter.OnFacilityClickListener {

    private static final int ADD_FACILITY_REQUEST = 1; // Request code for adding facility
    private static final int EDIT_FACILITY_REQUEST = 2; // Request code for editing facility
    private RecyclerView rvFacilities;
    private FacilityAdapter adapter; // Adapter for RecyclerView
    private List<Facility> facilitiesList; // List to hold facilities
    private FirebaseManager firebaseManager; // Firebase manager instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_page); // Ensure this matches your layout file name

        rvFacilities = findViewById(R.id.rvFacilities);
        FloatingActionButton fabAddFacility = findViewById(R.id.fabAddFacility);

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        // Initialize the facilities list and adapter
        facilitiesList = new ArrayList<>();
        adapter = new FacilityAdapter(facilitiesList, this); // Pass the listener to the adapter

        // Set up the RecyclerView
        rvFacilities.setLayoutManager(new LinearLayoutManager(this)); // Use a vertical layout
        rvFacilities.setAdapter(adapter); // Set the adapter to the RecyclerView

        // Load facilities from Firestore
        loadFacilities();
        setupBottomNavigation();

        // Set the FAB click listener
        fabAddFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacilityPageActivity.this, FacilityAddActivity.class);
                intent.putExtra("user", currentUser);
                startActivityForResult(intent, ADD_FACILITY_REQUEST); // Start FacilityAddActivity for result
            }
        });

        // Edit profile button initialization
        ImageButton editProfile = findViewById(R.id.btnProfile);
        editProfile.setOnClickListener(v -> showUserProfileFragment(currentUser));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.facility_page;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.events;
    }

    private void loadFacilities() {
        UUID userId = UUID.fromString(currentUser.getUserId()); // Get the current user's ID
        firebaseManager.getFacilitiesByUserId(userId, new FirebaseManager.FacilitiesListCallback() {
            @Override
            public void onSuccess(List<Facility> facilities) {
                runOnUiThread(() -> {
                    facilitiesList.clear(); // Clear the existing list
                    facilitiesList.addAll(facilities); // Add all facilities from Firestore
                    adapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(FacilityPageActivity.this, "Error loading facilities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_FACILITY_REQUEST && resultCode == RESULT_OK) {
            // Get the data from the intent
            String facilityName = data.getStringExtra("FACILITY_NAME");
            String facilityLocation = data.getStringExtra("FACILITY_LOCATION");

            // Create a new Facility object and add it to the list
            Facility newFacility = new Facility(facilityName, facilityLocation);
            facilitiesList.add(newFacility); // Add to your list

            // Notify the adapter to refresh the RecyclerView
            adapter.notifyDataSetChanged();
        } else if (requestCode == EDIT_FACILITY_REQUEST && resultCode == RESULT_OK) {
            // Handle the updated facility data
            String updatedName = data.getStringExtra("UPDATED_NAME");
            String updatedLocation = data.getStringExtra("UPDATED_LOCATION");
            int position = data.getIntExtra("FACILITY_POSITION", -1); // Get the position of the edited facility

            if (position >= 0 && position < facilitiesList.size()) {
                // Update the facility in the list
                Facility facilityToUpdate = facilitiesList.get(position);
                facilityToUpdate.setName(updatedName); // Assuming you have a setter in your Facility class
                facilityToUpdate.setLocation(updatedLocation); // Assuming you have a setter in your Facility class

                // Notify the adapter to refresh the RecyclerView
                adapter.notifyItemChanged(position);
            } else {
                Toast.makeText(this, "Invalid facility position", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onEditClick(Facility facility) {
        // Start the FacilityEditActivity with the facility data
        Intent intent = new Intent(this, FacilityEditActivity.class);
        intent.putExtra("FACILITY_NAME", facility.getName());
        intent.putExtra("FACILITY_LOCATION", facility.getLocation());
        intent.putExtra("FACILITY_POSITION", facilitiesList.indexOf(facility)); // Pass the position of the facility
        startActivityForResult(intent, EDIT_FACILITY_REQUEST);
    }

    @Override
    public void onDeleteClick(Facility facility) {
        String facilityName = facility.getName();

        // Remove the facility from the list
        facilitiesList.remove(facility);
        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
        Toast.makeText(this, "Facility deleted", Toast.LENGTH_SHORT).show();

        // Delete the facility from Firestore by name
        firebaseManager.deleteFacility(facilityName, new FirebaseManager.OnFacilityDeletedListener() {
            @Override
            public void onFacilityDeleted() {
                Toast.makeText(FacilityPageActivity.this, "Facility deleted from database", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(FacilityPageActivity.this, "Error deleting facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }}
