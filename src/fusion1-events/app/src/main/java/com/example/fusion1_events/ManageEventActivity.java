package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The ManageEventActivity class represents a user interface for managing an event.
 * It provides functionality for editing, viewing QR code, running lottery, viewing entrants, viewing map, sending notification, and deleting an event.
 */
public class ManageEventActivity extends AppCompatActivity {
    private Event event;
    private User currentUser;
    private DeviceManager deviceManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_event);

        // Initialize managers
        deviceManager = new DeviceManager(this);
        firebaseManager = new FirebaseManager();

        // Get event and load poster from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            event = extras.getParcelable("event");
            loadEventPoster(extras.getString("poster_image_path"));
        } else {
            Toast.makeText(this, "Error: Event data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Generate QR code for event, since it is not loaded at this point
        // Also generates the QR code hash
        event.generateQRCode();

        // Load current user first, then setup UI
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        firebaseManager.getUserByDeviceId(deviceManager.getOrCreateDeviceId(), new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                // Now that we have the user, setup the UI
                setupUI();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageEventActivity.this,
                        "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupUI() {
        // Set up back button with user data passing
        findViewById(R.id.backText).setOnClickListener(v -> navigateToEventDetailsPage());
        setupListeners();
    }

    private void navigateToEventDetailsPage() {
        Intent intent = new Intent(this, EventDetailsActivity.class);
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

    private void setupListeners() {
        findViewById(R.id.editEventBtn).setOnClickListener(v -> editEvent());
        findViewById(R.id.viewQRCodeBtn).setOnClickListener(v -> viewQrCode());
        findViewById(R.id.runLotteryBtn).setOnClickListener(v -> runLottery());
        findViewById(R.id.viewEntrantsBtn).setOnClickListener(v -> viewEntrants());
        findViewById(R.id.viewMapBtn).setOnClickListener(v -> viewMap());
        findViewById(R.id.sendNotificationBtn).setOnClickListener(v -> sendNotification());
        findViewById(R.id.deleteEventBtn).setOnClickListener(v -> deleteEvent());
    }

    private void editEvent() {
        Intent intent = new Intent(this, EventUpdateActivity.class);
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
        bundle.putParcelable("user", currentUser);
        bundle.putString("poster_image_path", tempFileName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void viewQrCode() {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.qr_code_view_popup, null);

        // Get the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // Calculate 75% of the screen width for the popup size
        int popupSize = (int) (screenWidth * 0.75);

        // Create the popup window
        boolean focusable = true; // Lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(container, popupSize, ViewGroup.LayoutParams.WRAP_CONTENT, focusable);

        // Set up the QR code image
        ImageView qrCodeImageView = container.findViewById(R.id.qrCodeImageView);
        qrCodeImageView.setImageBitmap(event.getQrCode());

        // Set the ImageView size programmatically to fill the popup
        ViewGroup.LayoutParams layoutParams = qrCodeImageView.getLayoutParams();
        layoutParams.width = popupSize;
        layoutParams.height = popupSize; // Ensure it is a square
        qrCodeImageView.setLayoutParams(layoutParams);

        // Tap to dismiss
        container.setOnClickListener(v -> popupWindow.dismiss());

        // Show the popup window
        popupWindow.showAtLocation(findViewById(R.id.viewQRCodeBtn), Gravity.CENTER, 0, 0);
    }


    private void runLottery() {
        event.runLottery();
        firebaseManager.updateExistingEvent(event);
        Toast toast = Toast.makeText(this, "Lottery has been run", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void viewEntrants() {
        Intent intent = new Intent(this, ViewEntrantsListsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("waitlist", event.getWaitlist());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void viewMap() {
        List<String> entrantsID = event.getWaitlist().getAllEntrants();
        UserController userController = new UserController(new FirebaseManager());

        ArrayList<Entrant> entrants = new ArrayList<>();
        for(String id : entrantsID)
        {
            userController.userLogin(id, new FirebaseManager.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    entrants.add((Entrant) user);
                    if(entrants.size() == entrantsID.size()) toViewMapActivity(entrants);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast toast = Toast.makeText(ManageEventActivity.this, "Error loading entrants: " + e.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

    private void toViewMapActivity(ArrayList<Entrant> entrants){

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putParcelableArrayListExtra("entrants", (ArrayList<? extends Parcelable>) entrants);
        startActivity(intent);

    }

    private void sendNotification() {

    }

    private void deleteEvent() {
        firebaseManager.deleteEvent(event);

        Intent intent = new Intent(this, EventsPageActivity.class);

        // Pass user data
        if (currentUser != null) {
            Bundle bundle = new Bundle();
            String tempFileName = "temp_profile_image.jpg";

            try {
                if (currentUser.getProfileImage() != null) {
                    FileOutputStream fos = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                    currentUser.getProfileImage().compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            bundle.putParcelable("user", currentUser);
            bundle.putString("profile_image_path", tempFileName);
            intent.putExtras(bundle);
        }

        navigateUpTo(intent);
    }
}
