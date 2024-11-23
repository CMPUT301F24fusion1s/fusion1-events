package com.example.fusion1_events;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class AdminMainMenuActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. This method sets up the layout and initializes the UI components
     * for the main menu that the admin interacts with.
     *
     * @param savedInstancesState If the activity is being re-initialized after previously being shut down,
     *                            this Bundle contains the most recent data supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstancesState){
       super.onCreate(savedInstancesState);
       setContentView(R.layout.activity_admin_main_menu);

        // Initialize buttons to interact with different sections of the admin main menu
        Button viewEventButton = findViewById(R.id.btn_view_event);
        Button viewProfilesButton = findViewById(R.id.btn_view_profiles);
        Button viewFacilitiesButton = findViewById(R.id.btn_view_facilities);
        Button browseImagesButton = findViewById(R.id.btn_browse_images);



        viewProfilesButton.setOnClickListener(v -> show_profiles());



    }

    void show_profiles()
    {
        ArrayAdapter<Entrant> userListAdapter;
        setContentView(R.layout.activity_profile_list);  // switch to profile layout

        AdminController admincontroller = new AdminController(new FirebaseManager());

        ArrayList<Entrant> userList = (ArrayList<Entrant>) admincontroller.getallusers();
        userListAdapter = new ArrayAdapter<>(getApplicationContext(), 0 , userList);
        userListAdapter.notifyDataSetChanged();

    }
}
