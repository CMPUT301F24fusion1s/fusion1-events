package com.example.fusion1_events;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationSendMessage extends AppCompatActivity {

    private Spinner recipientsSpinner;
    private EditText messageInput;
    private FirebaseManager firebaseManager;
    private String selectedUserId; // To store the selected user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_selected_entrants); // Ensure this matches your XML layout name

        // Initialize FirebaseManager
        firebaseManager = new FirebaseManager();

        // Initialize views
        recipientsSpinner = findViewById(R.id.recipients_spinner);
        messageInput = findViewById(R.id.message_input);

        // Set up the recipients spinner
        setupRecipientsSpinner();

        // Back button listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Send message button listener
        findViewById(R.id.send_message_button).setOnClickListener(v -> sendMessage());
    }

    private void setupRecipientsSpinner() {
        // Example data, replace this with actual user data from Firestore
        List<String> recipients = new ArrayList<>();
        recipients.add("User 1: 67e2c70e-90ab-451a-a949-e6599d9afb82"); // Example recipient
        recipients.add("User 2: 123e4567-e89b-12d3-a456-426614174000"); // Add more users as needed

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recipients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipientsSpinner.setAdapter(adapter);

        // Listener to get the selected user ID from the spinner
        recipientsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                selectedUserId = selectedItem.split(": ")[1]; // Extract user ID from the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUserId = null; // Reset if nothing is selected
            }
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (selectedUserId == null || message.isEmpty()) {
            Toast.makeText(this, "Please select a recipient and enter a message.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Notification object representing the message
        Notification newNotification = new Notification("Message", message, null, selectedUserId, false);

        // Send the message to Firestore
        firebaseManager.sendNotification(newNotification, new FirebaseManager.NotificationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(NotificationSendMessage.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after sending
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(NotificationSendMessage.this, "Error sending message: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
