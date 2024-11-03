package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_main_menu);

        // Profile button

        ImageButton editprofile = findViewById(R.id.btnProfile);


        editprofile.setOnClickListener(v -> {
            // Call the userLogin method from UserController
            Log.d("profile button ", " pressed");
            setContentView(R.layout.user_profile_page);

        });
    }
}

