package com.example.fusion1_events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * An abstract base class that provides common functionality for creating and updating events.
 * This is used because the event create and update screens are shared, and so the activities share a lot of common functionality.
 */
public abstract class EventCreateUpdateBase extends AppCompatActivity {
    protected EditText titleInput, yearInput, monthInput, dayInput, hourInput, minuteInput, locationInput, maxWinnersInput, maxEntrantsInput, descriptionInput;
    protected Spinner ampmSpinner;
    protected CheckBox geoLocationCheckbox;
    protected Button addImageButton, createButton;
    protected FirebaseManager firebaseManager;
    protected EventController eventController;
    protected Bitmap selectedPoster;
    protected ActivityResultLauncher<Intent> imagePickerLauncher;
    protected User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_creation);

        // Get user data from intent
        currentUser = getIntent().getParcelableExtra("user");
        if (currentUser == null) {
            Toast.makeText(this, "Error: User data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize FirebaseManager and EventController
        firebaseManager = new FirebaseManager();
        eventController = new EventController(firebaseManager);

        initializeViews();
        setupImagePicker();
        setupClickListeners();
    }

    /**
     * Get all the views from the layout and store them in the corresponding fields.
     */
    private void initializeViews() {
        titleInput = findViewById(R.id.titleInput);
        yearInput = findViewById(R.id.yearInput);
        monthInput = findViewById(R.id.monthInput);
        dayInput = findViewById(R.id.dayInput);
        hourInput = findViewById(R.id.hourInput);
        minuteInput = findViewById(R.id.minuteInput);
        ampmSpinner = findViewById(R.id.ampmSpinner);
        locationInput = findViewById(R.id.locationInput);
        maxWinnersInput = findViewById(R.id.maxWinnersInput);
        maxEntrantsInput = findViewById(R.id.maxEntrantsInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        geoLocationCheckbox = findViewById(R.id.geoLocationCheckbox);
        addImageButton = findViewById(R.id.addImageButton);
        createButton = findViewById(R.id.createButton);
    }

    /**
     * Set up the image picker to allow the user to select an image from their device, for the poster.
     */
    private void setupImagePicker() {
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
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    /**
     * Open the image picker to allow the user to select an image from their device.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void setupClickListeners() {
        addImageButton.setOnClickListener(v -> openImagePicker());
        createButton.setOnClickListener(v -> saveEvent());

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> navigateBack());
    }

    /**
     * The method that is called when the user clicks the primary button.
     */
    protected abstract void saveEvent();

    /**
     * The method that is called when the user clicks the back button, in order to navigate up to the previous activity.
     */
    protected abstract void navigateBack();
}
