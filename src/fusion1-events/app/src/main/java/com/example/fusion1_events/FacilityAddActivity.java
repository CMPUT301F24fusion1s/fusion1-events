package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class FacilityAddActivity extends AppCompatActivity {

    private EditText etFacilityName;
    private EditText etFacilityLocation;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facility); // Ensure this matches your layout file name

        etFacilityName = findViewById(R.id.NameInput); // Ensure the ID matches your layout
        etFacilityLocation = findViewById(R.id.etFacilityLocation); // Ensure the ID matches your layout
        btnAdd = findViewById(R.id.btnAdd); // Ensure the ID matches your layout

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture the input data
                String facilityName = etFacilityName.getText().toString();
                String facilityLocation = etFacilityLocation.getText().toString();

                // Create an Intent to return the data
                Intent resultIntent = new Intent();
                resultIntent.putExtra("FACILITY_NAME", facilityName);
                resultIntent.putExtra("FACILITY_LOCATION", facilityLocation);
                setResult(RESULT_OK, resultIntent); // Set the result to OK
                finish(); // Close the activity and return to the previous one
            }
        });
    }
}
