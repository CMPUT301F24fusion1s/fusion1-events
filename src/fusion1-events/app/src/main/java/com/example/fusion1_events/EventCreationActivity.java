package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Activity for creating a new event, handles user input and saves the event to the database
 */
public class EventCreationActivity extends EventCreateUpdateBase {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void saveEvent() {
        try {
            eventController.saveEvent(getNewEvent());

            // Show success message
            Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();

            // Navigate back
            navigateBack();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please fill in all numeric fields correctly", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void navigateBack() {
        // Navigate back to EventsPageActivity
        Intent intent = new Intent(this, EventsPageActivity.class);

        // Pass back the user data
        if (currentUser != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", currentUser);

            // Handle profile image if it exists
            if (currentUser.getProfileImage() != null) {
                String tempFileName = "temp_image.jpg";
                try {
                    FileOutputStream fos = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                    currentUser.getProfileImage().compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                    bundle.putString("image_path", tempFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            intent.putExtras(bundle);
        }

        startActivity(intent);
        finish();
    }

    /**
     * Create a new event object from user inputs
     *
     * @return the new event object
     */
    private Event getNewEvent() {
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
        return eventController.createEvent(null, organizerId, title, date, location, description,
                selectedPoster, maxWinners, waitlistLimit, geoLocationRequired);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBack();
    }
}
