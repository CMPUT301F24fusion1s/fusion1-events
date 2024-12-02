package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Activity for editing/updating and existing event.
 */
public class EventUpdateActivity extends EventCreateUpdateBase {
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get event and load poster from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event = extras.getParcelable("event");
            currentUser = extras.getParcelable("user");
            loadEventPoster(extras.getString("poster_image_path"));
        } else {
            Toast.makeText(this, "Error: Event data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Load current user first, then setup UI
        setupUI();
    }

    /**
     * Load event poster from internal storage.
     *
     * @param imagePath
     */
    private void loadEventPoster(String imagePath) {
        if (imagePath != null && event != null) {
            try {
                FileInputStream fis = this.openFileInput(imagePath);
                Bitmap posterImage = BitmapFactory.decodeStream(fis);
                fis.close();
                event.setPoster(posterImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Setup UI with existing event data.
     */
    private void setupUI() {
        Button updateButton = findViewById(R.id.createButton);
        updateButton.setText(R.string.update_event);

        TextView pageTitle = findViewById(R.id.tvEventTitle);
        pageTitle.setText(R.string.update_event);

        // Prefill event data
        titleInput.setText(event.getName());

        // Split date and time into separate fields
        Date eventDate = event.getDate();
        yearInput.setText(String.valueOf(eventDate.getYear() + 1900));
        monthInput.setText(String.valueOf(eventDate.getMonth() + 1));
        dayInput.setText(String.valueOf(eventDate.getDate()));
        hourInput.setText(String.valueOf(eventDate.getHours() % 12));
        minuteInput.setText(String.valueOf(eventDate.getMinutes()));
        ampmSpinner.setSelection(eventDate.getHours() / 12);

        locationInput.setText(event.getLocation());
        geoLocationCheckbox.setChecked(event.getGeolocationRequired());
        maxWinnersInput.setText(String.valueOf(event.getCapacity()));
        maxEntrantsInput.setText(String.valueOf(event.getWaitlistLimit()));
        descriptionInput.setText(event.getDescription());
        selectedPoster = event.getPoster();
    }

    @Override
    protected void saveEvent() {
        try {
            updateEventDetails();
            eventController.updateEvent(event);

            // Show success message
            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();

            // Navigate back
            navigateBack();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please fill in all numeric fields correctly", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void navigateBack() {
        Intent intent = new Intent(this, ManageEventActivity.class);
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
        finish();
    }

    /**
     * Update event details with new user inputs.
     */
    private void updateEventDetails() {
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
        int waitlistLimit = Integer.parseInt(maxEntrantsInput.getText().toString());
        String description = descriptionInput.getText().toString();
        boolean geoLocationRequired = geoLocationCheckbox.isChecked();

        // Convert to Date object
        Date date = new Date(year - 1900, month - 1, day, hour, minute);

        String organizerId = currentUser.getDeviceId();

        // Create a new event
        Event.WaitList waitList = event.getWaitlist();
        event = eventController.createEvent(event.getId(), organizerId, title, date, location, description,
                selectedPoster, maxWinners, waitlistLimit, geoLocationRequired);
        event.setWaitlist(waitList);
    }
}
