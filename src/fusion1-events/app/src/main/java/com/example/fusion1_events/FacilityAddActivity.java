package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.R;

/**
 * Activity for adding a new facility
 */
public class FacilityAddActivity extends AppCompatActivity {

    private EditText etFacilityName;
    private EditText etFacilityLocation;
    private Button btnAdd;
    private FirebaseManager firebaseManager; // Instance of FirebaseManager
    private User user;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facility); // Ensure this matches your layout file name

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Initialize the EditText and Button
        etFacilityName = findViewById(R.id.NameInput);
        etFacilityLocation = findViewById(R.id.etFacilityLocation);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture the input data
                String facilityName = etFacilityName.getText().toString().trim();
                String facilityLocation = etFacilityLocation.getText().toString().trim();

                // Validate input
                if (facilityName.isEmpty() || facilityLocation.isEmpty()) {
                    Toast.makeText(FacilityAddActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Facility object
                Facility newFacility = new Facility(facilityName, facilityLocation, user.getUserId()); // Assuming you have the user ID

                // Add the facility to Firestore
                firebaseManager.addFacility(newFacility, new FirebaseManager.OnFacilityAddedListener() {
                    @Override
                    public void onFacilityAdded(String facilityId) {
                        // Facility added successfully, return to previous activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("FACILITY_NAME", facilityName);
                        resultIntent.putExtra("FACILITY_LOCATION", facilityLocation);
                        setResult(RESULT_OK, resultIntent); // Set the result to OK
                        finish(); // Close the activity
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(FacilityAddActivity.this, "Error adding facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Close the activity
    }

}