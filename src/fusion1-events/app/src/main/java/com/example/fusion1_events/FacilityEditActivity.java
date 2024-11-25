package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FacilityEditActivity extends AppCompatActivity {

    private EditText etFacilityName;
    private EditText etFacilityLocation;
    private Button btnSave;
    private int facilityPosition; // Variable to hold the position of the facility being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_facility);

        // Initialize the EditText and Button
        etFacilityName = findViewById(R.id.etFacilityName);
        etFacilityLocation = findViewById(R.id.etFacilityLocation);
        btnSave = findViewById(R.id.btnSave);

        // Get the data from the intent
        Intent intent = getIntent();
        String facilityName = intent.getStringExtra("FACILITY_NAME");
        String facilityLocation = intent.getStringExtra("FACILITY_LOCATION");
        facilityPosition = intent.getIntExtra("FACILITY_POSITION", -1); // Get the position of the facility

        // Set the current values in the EditText fields
        etFacilityName.setText(facilityName);
        etFacilityLocation.setText(facilityLocation);

        // Set up the Save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get updated values
                String updatedName = etFacilityName.getText().toString().trim(); // Trim whitespace
                String updatedLocation = etFacilityLocation.getText().toString().trim(); // Trim whitespace

                // Validate input
                if (updatedName.isEmpty() || updatedLocation.isEmpty()) {
                    Toast.makeText(FacilityEditActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create an Intent to return the updated data
                Intent resultIntent = new Intent();
                resultIntent.putExtra("UPDATED_NAME", updatedName);
                resultIntent.putExtra("UPDATED_LOCATION", updatedLocation);
                resultIntent.putExtra("FACILITY_POSITION", facilityPosition); // Pass the position back
                setResult(RESULT_OK, resultIntent); // Set the result to OK
                finish(); // Close the activity and return to the previous one
            }
        });
    }
}
