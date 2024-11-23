package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


public class AdminMainMenuActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. This method sets up the layout and initializes the UI components
     * for the main menu that the admin interacts with.
     *
     * @param savedInstancesState If the activity is being re-initialized after previously being shut down,
     *                            this Bundle contains the most recent data supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_admin_main_menu);

        // Initialize buttons to interact with different sections of the admin main menu
        Button viewEventButton = findViewById(R.id.btn_view_event);
        Button viewProfilesButton = findViewById(R.id.btn_view_profiles);
        Button viewFacilitiesButton = findViewById(R.id.btn_view_facilities);
        Button browseImagesButton = findViewById(R.id.btn_browse_images);


        viewProfilesButton.setOnClickListener(v -> show_profiles());


    }

    void show_profiles() {

        setContentView(R.layout.activity_profile_list);  // switch to profile layout

        AdminController admincontroller = new AdminController(new FirebaseManager());

        admincontroller.getallusers(new FirebaseManager.UsersListCallback() {
            @Override
            public void onScuccess(List<Entrant> users) {
                populateProfileList(users);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });


        // we need to call the firebase manager to fetch all user
        // and display them in the profile_list
    }

    private void populateProfileList(List<Entrant> users) {
        // define views
        LinearLayout profileListLayout = findViewById(R.id.profile_list_layout);
        Entrant entrant = new Entrant();
        for (Entrant user : users) {
            View profileIeam = getLayoutInflater().inflate(R.layout.porfile_iteam, null);

            // Set user details
            TextView nameTextView = profileIeam.findViewById(R.id.profile_name);
            TextView emailTextView = profileIeam.findViewById(R.id.profile_email);
            TextView deviceIdTextView = profileIeam.findViewById(R.id.profile_device_id);
            TextView phoneTextView = profileIeam.findViewById(R.id.profile_phone);
            ImageView imageView = profileIeam.findViewById(R.id.profile_image);

            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            deviceIdTextView.setText(user.getDeviceId());
            phoneTextView.setText(user.getPhoneNumber());

            if (user.getProfileImage() != null)
                imageView.setImageBitmap(user.getProfileImage());


            // Set up the delete button
            ImageButton deleteButton = profileIeam.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(v -> {
                // Confirm deletion (optional)
                new AlertDialog.Builder(this)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteUser(user, profileIeam, profileListLayout);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            // Add the profile item to the parent layout
            profileListLayout.addView(profileIeam);
        }
    }

    private void deleteUser(Entrant user, View profileItem, LinearLayout profileListLayout) {
        // Call FirebaseManager to delete the user


        Toast.makeText(this,"device ID"+ user.getDeviceId(), Toast.LENGTH_SHORT).show() ;

        

    }
}
