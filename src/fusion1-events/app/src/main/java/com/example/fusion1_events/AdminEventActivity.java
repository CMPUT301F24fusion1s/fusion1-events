package com.example.fusion1_events;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminEventActivity extends AppCompatActivity {
    AdminController adminController;

    ImageView poster;
    ImageView qrCode;
    TextView date;
    TextView description;
    TextView location;
    TextView capacity;
    Bundle bundle;
    String eventDate;
    String eventLocation;
    Bitmap eventQRCode;
    TextView backBtn;
    Bitmap eventPoster;
    int eventCapacity;
    Button deleteBtn;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_event_details);

        date = findViewById(R.id.event_date);
        description = findViewById(R.id.event_description);
        poster = findViewById(R.id.event_image);
        location = findViewById(R.id.event_location);
        qrCode = findViewById(R.id.qr_code);
        capacity = findViewById(R.id.event_max_participants);
        deleteBtn = findViewById(R.id.delete_event);
        backBtn = findViewById(R.id.back_button);
        adminController = new AdminController(new FirebaseManager());

        Intent intent = getIntent();
        bundle = intent.getExtras();
        assert bundle != null;
        Event event = bundle.getParcelable("event");
        if (event != null) {
            eventDate = event.getDate().toString();
            eventPoster = event.getPoster();
            eventQRCode = event.getQrCode();
            eventLocation = event.getLocation();
            eventCapacity = event.getCapacity();
        }
        if (event.getDate() != null)
            date.setText("Date: " + event.getDate().toString());
        if (event.getDescription() != null)
            description.setText("Description: " + event.getDescription());
        if (event.getPoster() != null)
            poster.setImageBitmap(event.getPoster());
        if (event.getQrCode() != null)
            qrCode.setImageBitmap(event.getQrCode());
        if (event.getLocation() != null)
            location.setText("Location: " + event.getLocation());
        if (event.getCapacity() != 0)
            capacity.setText("Maximum Capcity: " + String.valueOf(event.getCapacity()));


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent(event);
                finish();

            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("Do you want to save changes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adminController.updateEvent(event);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do nothing.
                            }
                        })
                        .setTitle("Saving Alert")
                        .create();
                finish();
            }
        });

    }

    private void deleteEvent(Event event) {
        adminController.deleteEvent(event);
    }
}
