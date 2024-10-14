package com.example.fusion1_events;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// This for the device ID
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// This for the firebase libraries
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;





public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get Device ID
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // Send an output log
        Log.d("DeviceID", "Device ID: " + deviceId);

        // Check if user exists, otherwise add the user
        checkAndAddUser(deviceId);
    }

    /**
     *
     * @param deviceId
     */
    private void checkAndAddUser(String deviceId) {
        // Check if the user exists in Firestore
        db.collection("users").document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        // User doesn't exist, create a new user
                        createUser(deviceId);
                    } else {
                        // User already exists
                        Log.d("Firestore", "User already exists: " + document.getData());
                    }
                } else {
                    Log.d("Firestore", "Error checking user", task.getException());
                }
            }
        });
    }

    private void createUser(String deviceId) {
        // Create user data
        Map<String, Object> user = new HashMap<>();
        user.put("userId", deviceId);
        user.put("name", "Default User");  // You can allow the user to input their name later
        user.put("role", "entrant");

        // Add user to Firestore
        db.collection("users").document(deviceId).set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User added successfully with deviceId: " + deviceId))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding user", e));
    }
}
