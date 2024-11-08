package com.example.fusion1_events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class EventCreationActivity extends AppCompatActivity {

    private EditText titleInput, yearInput, monthInput, dayInput, hourInput, minuteInput, locationInput, maxWinnersInput, descriptionInput;
    private Spinner ampmSpinner;
    private CheckBox geoLocationCheckbox;
    private Button addImageButton, createButton;
    private FirebaseManager firebaseManager;
    private EventController eventController;
    private Bitmap selectedPoster;
    private User currentUser;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_creation);

        // Get user data from intent
        currentUser = getIntent().getParcelableExtra("user");

        // Initialize FirebaseManager and EventController
        firebaseManager = new FirebaseManager();
        eventController = new EventController(firebaseManager);

        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        yearInput = findViewById(R.id.yearInput);
        monthInput = findViewById(R.id.monthInput);
        dayInput = findViewById(R.id.dayInput);
        hourInput = findViewById(R.id.hourInput);
        minuteInput = findViewById(R.id.minuteInput);
        ampmSpinner = findViewById(R.id.ampmSpinner);
        locationInput = findViewById(R.id.locationInput);
        maxWinnersInput = findViewById(R.id.maxWinnersInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        geoLocationCheckbox = findViewById(R.id.geoLocationCheckbox);
        addImageButton = findViewById(R.id.addImageButton);
        createButton = findViewById(R.id.createButton);

        // Initialize ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            selectedPoster = BitmapFactory.decodeStream(imageStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Set onClickListener for add image button
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Set onClickListener for create button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void createEvent() {
        // Get user inputs
        String title = titleInput.getText().toString();
        int year = Integer.parseInt(yearInput.getText().toString());
        int month = Integer.parseInt(monthInput.getText().toString());
        int day = Integer.parseInt(dayInput.getText().toString());
        int hour = Integer.parseInt(hourInput.getText().toString());
        int minute = Integer.parseInt(minuteInput.getText().toString());
        String ampm = ampmSpinner.getSelectedItem().toString();
        String location = locationInput.getText().toString();
        int maxWinners = Integer.parseInt(maxWinnersInput.getText().toString());
        String description = descriptionInput.getText().toString();
        boolean geoLocationRequired = geoLocationCheckbox.isChecked();

        // Convert to Date object
        Date date = new Date(year - 1900, month - 1, day, hour, minute);

        // Create a new event
        UUID organizerId = UUID.fromString(currentUser.getUserId());
        eventController.createEvent(organizerId, title, date, location, description, selectedPoster, maxWinners, geoLocationRequired);

        // Show success message
        Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();

        // Finish the activity
        finish();
    }
}