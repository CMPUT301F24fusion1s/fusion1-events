package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class AdminMainMenuActivity extends AppCompatActivity{

    AdminController admincontroller;

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

        admincontroller = new AdminController(new FirebaseManager());

        viewProfilesButton.setOnClickListener(v -> show_profiles());

        viewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.admin_event_list);
                admincontroller.getAllEvents(new FirebaseManager.EventsListCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        showEvents((ArrayList<Event>) events);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });

    }

    private void showEvents(ArrayList<Event> events) {
        EventAdapter eventListAdapter = new EventAdapter(events);

        TextView textView = findViewById(R.id.admin_event_list_page_title);
        RecyclerView eventListView = findViewById(R.id.event_list);

        textView.setText("List of Events");
        eventListView.setLayoutManager(new LinearLayoutManager(this));
        eventListView.setAdapter(eventListAdapter);
        Context context = this;
        eventListAdapter.setOnEventClickListener(new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(Event event) {
                Intent intent = new Intent(context,AdminEventActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });
    }

    void show_profiles() {

        setContentView(R.layout.activity_profile_list);  // switch to profile layout

        admincontroller.getAllUsers(new FirebaseManager.UsersListCallback() {
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
            View profileItem = getLayoutInflater().inflate(R.layout.porfile_iteam, null);

            // Set user details
            TextView nameTextView = profileItem.findViewById(R.id.profile_name);
            TextView emailTextView = profileItem.findViewById(R.id.profile_email);
            TextView deviceIdTextView = profileItem.findViewById(R.id.profile_device_id);
            TextView phoneTextView = profileItem.findViewById(R.id.profile_phone);
            ImageView imageView = profileItem.findViewById(R.id.profile_image);

            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            deviceIdTextView.setText(user.getDeviceId());
            phoneTextView.setText(user.getPhoneNumber());

            if (user.getProfileImage() != null)
                imageView.setImageBitmap(user.getProfileImage());


            // Set up the delete button
            ImageButton deleteButton = profileItem.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(v -> {
                // Confirm deletion (optional)
                new AlertDialog.Builder(this)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteUser(user, profileItem, profileListLayout);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            // Add the profile item to the parent layout
            profileListLayout.addView(profileItem);

            ImageButton back_arrow = findViewById(R.id.backArrow1);

            back_arrow.setOnClickListener(v -> {
               Intent intent = new Intent(this, AdminMainMenuActivity.class);
               startActivity(intent);
            });
        }
    }

    private void deleteUser(Entrant user, View profileItem, LinearLayout profileListLayout) {
        // Call FirebaseManager to delete the user
        admincontroller.deleteUser(user.getDeviceId());
        // delete the profile from the view
        profileListLayout.removeView(profileItem);

        Toast.makeText(this,"device ID"+ user.getDeviceId(), Toast.LENGTH_SHORT).show() ;
    }
}
