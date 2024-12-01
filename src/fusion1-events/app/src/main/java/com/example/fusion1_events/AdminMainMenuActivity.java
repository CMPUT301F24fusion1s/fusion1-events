package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AdminMainMenuActivity extends AppCompatActivity{

    AdminController adminController;
    RecyclerView eventListView;
    EventAdapter eventListAdapter;
    int REQUEST_CODE_EVENT_ACTIVITY = 5;
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
        adminController = new AdminController(new FirebaseManager());

        viewProfilesButton.setOnClickListener(v -> show_profiles());

        viewFacilitiesButton.setOnClickListener(v -> {
            List<Facility> mockFacilities = new ArrayList<>();
            setContentView(R.layout.admin_facility_list); // Switch to the facilities layout
            adminController.getAllFacilities(new FirebaseManager.facilityCallback() {
                @Override
                public void onSuccess(List<Facility> facilities) {
                    populateFacilitiesList(facilities);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("AdminMainMenuActivity", "Failed to fetch facilities", e);
                    Toast.makeText(AdminMainMenuActivity.this, "Failed to load facilities.", Toast.LENGTH_SHORT).show();
                }
            });
        });


        viewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.admin_event_list);
                adminController.getAllEvents(new FirebaseManager.EventsListCallback() {
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

    private void populateFacilitiesList(List<Facility> facilities) {
        LinearLayout facilityListLayout = findViewById(R.id.facility_list_layout);

        // Clear any existing views
        facilityListLayout.removeAllViews();

        for (Facility facility : facilities) {
            // Inflate the facility item layout
            View facilityItem = getLayoutInflater().inflate(R.layout.facility_iteam_admin_list, null);

            // Set facility details
            TextView facilityName = facilityItem.findViewById(R.id.facility_name);
            TextView facilityLocation = facilityItem.findViewById(R.id.facility_location);
            facilityName.setText(facility.getName());
            facilityLocation.setText(facility.getLocation());

            // Set up the delete button
            ImageButton deleteButton = facilityItem.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(v -> {
                // Confirm deletion (optional)
                new AlertDialog.Builder(this)
                        .setTitle("Delete Facility")
                        .setMessage("Are you sure you want to delete this facility?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteFacility(facility, facilityItem, facilityListLayout);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            // Add the facility item to the layout
            facilityListLayout.addView(facilityItem);
        }
    }


    private void deleteFacility(Facility facility, View facilityItem, LinearLayout facilityListLayout) {
        adminController.deleteFacility(facility.getName(), new FirebaseManager.OperationCallback() {
            @Override
            public void onSuccess() {
                facilityListLayout.removeView(facilityItem); // Remove the facility view
                Toast.makeText(AdminMainMenuActivity.this, "Facility deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminMainMenuActivity.this, "Failed to delete facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showEvents(ArrayList<Event> events) {
        eventListAdapter = new EventAdapter(events);

        TextView textView = findViewById(R.id.admin_event_list_page_title);
        eventListView = findViewById(R.id.event_list);

        ImageButton event_list_back_arrow = findViewById(R.id.backArrowEvent);

        event_list_back_arrow.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminMainMenuActivity.class);
            startActivity(intent);
        });

        eventListView.setLayoutManager(new LinearLayoutManager(this));
        eventListView.setAdapter(eventListAdapter);
        Context context = this;
        eventListAdapter.setOnEventClickListener(new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(Event event) {
                Intent intent = new Intent(context,AdminEventActivity.class);
                intent.putExtra("event", event);
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
                intent.putExtra("poster", tempFileName);
                startActivityForResult(intent, REQUEST_CODE_EVENT_ACTIVITY);
            }
        });
    }

    void show_profiles() {

        setContentView(R.layout.activity_profile_list);  // switch to profile layout

        adminController.getAllUsers(new FirebaseManager.UsersListCallback() {
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

            imageView.setOnClickListener(v-> removeUserImage(imageView,user.getDeviceId()) );

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

            ImageButton user_list_back_arrow = findViewById(R.id.backArrowUsers);

            user_list_back_arrow.setOnClickListener(v -> {
               Intent intent = new Intent(this, AdminMainMenuActivity.class);
               startActivity(intent);
            });
        }
    }


    private void deleteUser(Entrant user, View profileItem, LinearLayout profileListLayout) {
        // Call FirebaseManager to delete the user
        adminController.deleteUser(user.getDeviceId());
        // delete the profile from the view
        profileListLayout.removeView(profileItem);

        Toast.makeText(this,"device ID"+ user.getDeviceId(), Toast.LENGTH_SHORT).show() ;
    }

    private void deleteFacility()
    {

    }

    private void removeUserImage(ImageView imageView, String deviceId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete user image")
                .setMessage("Are you sure you want to delete this user image?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Reset local image
                    imageView.setImageResource(R.drawable.ic_user); // Reset to placeholder image

                    // Delegate backend operation to AdminController
                    adminController.removeUserImage(deviceId, new FirebaseManager.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AdminMainMenuActivity.this, "Profile image removed successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(AdminMainMenuActivity.this, "Failed to remove profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EVENT_ACTIVITY && resultCode == RESULT_OK) {
            adminController.getAllEvents(new FirebaseManager.EventsListCallback() {
                @Override
                public void onSuccess(List<Event> events) {
                    showEvents((ArrayList<Event>) events);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AdminMainMenuActivity.this, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
