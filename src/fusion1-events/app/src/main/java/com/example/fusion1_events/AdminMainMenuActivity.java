package com.example.fusion1_events;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstancesState){
       super.onCreate(savedInstancesState);
       setContentView(R.layout.activity_admin_main_menu);

       // buttons
        Button viewEventButton = findViewById(R.id.btn_view_event);
        Button viewProfilesButton = findViewById(R.id.btn_view_profiles);
        Button viewFacilitiesButton = findViewById(R.id.btn_view_facilities);
        Button browseImagesButton = findViewById(R.id.btn_browse_images);

    }
}
