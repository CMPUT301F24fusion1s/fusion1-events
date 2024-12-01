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

    protected abstract void saveEvent();

    protected abstract void navigateBack();
}
