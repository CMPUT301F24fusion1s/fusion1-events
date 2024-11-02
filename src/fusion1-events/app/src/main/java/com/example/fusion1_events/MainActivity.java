package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// This for the device ID
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// This for the firebase libraries
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private DeviceManager deviceManager;
    private FirebaseManager firebaseManager;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);  // This the first layout

        // Device manager and fire base
        deviceManager = new DeviceManager(this);
        firebaseManager = new FirebaseManager();
        userController = new UserController(firebaseManager);  // Added UserController initialization

        String deviceId = deviceManager.getOrCreateDeviceId();  // getting the device ID which is used to sign in
        // for testing could be commented or removed latter
        Log.d("DeviceID", "Device ID: " + deviceId);

        // Find the Register button my it's ID
        Button registraitionbutton = findViewById(R.id.registerButton);

        // Set click listener for the button
        registraitionbutton.setOnClickListener(v -> {
            // Call the userLogin method from UserController
            Log.d("register button ", " pressed");
            userController.userLogin(deviceId, new FirebaseManager.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    // User exists, check the type and navigate to the appropriate layout
                    Log.d("MainActivity", "User found: " + user.toString());
                    if (user instanceof Admin) {
                        navigateToMainMenu(AdminMainMenuActivity.class);
                    }
//                    } else if (user instanceof Entrant) {
//                        navigateToMainMenu(EntrantMainMenuActivity.class);
//                    } else if (user instanceof Organizer) {
//                        navigateToMainMenu(OrganizerMainMenuActivity.class);
//                    }
                }

                @Override
                public void onFailure(Exception e) {
                    // User does not exist; switch to the registration layout
                    Log.e("FirebaseManager", "User not found or error: " + e.getMessage(), e);
                    // User does not exist; switch to the registration layout
                    Log.d("MainActivity", "Navigating to register new user layout.");
                    setContentView(R.layout.register_new_user);
                    setupNewUserRegistration(deviceId);
                }
            });
        });
    }

    private void navigateToMainMenu(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
        finish(); // Close the current activity to prevent going back to it
    }

        private void setupNewUserRegistration(String deviceId){
            // Find the input fields and Register button in the new layout
            EditText nameField = findViewById(R.id.et_name);
            EditText emailField = findViewById(R.id.et_email);
            EditText phoneNumberField = findViewById(R.id.et_phone_number);
            Button registerButton = findViewById(R.id.btn_register);

            // Set click listener for the register button in the new layout
            registerButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();
                String email = emailField.getText().toString().trim();
                String phoneNumber = phoneNumberField.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty()) {
                    // Create an Entrant object with the collected data
                    Entrant entrant = new Entrant(email, name, "entrant", phoneNumber, UUID.randomUUID(),deviceId,null, null, true);


                    // Use UserController to sign up the user
                    userController.signUpUser(entrant);
                    Toast.makeText(MainActivity.this, "User registration in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



