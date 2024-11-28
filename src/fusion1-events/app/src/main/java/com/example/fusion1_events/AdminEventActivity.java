package com.example.fusion1_events;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AdminEventActivity extends AppCompatActivity {
    AdminController adminController;
    private final int RESULT_LOAD_IMG = 0, RESULT_OK = -1;
    Event event;

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
    TextView replaceBtn;
    TextView removeBtn;
    int eventCapacity;
    Button deleteBtn;
    AlertDialog.Builder builder;
    Intent intent1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_event_details);
        // Initialization
        date = findViewById(R.id.event_date);
        description = findViewById(R.id.event_description);
        poster = findViewById(R.id.event_image);
        location = findViewById(R.id.event_location);
        qrCode = findViewById(R.id.qr_code);
        capacity = findViewById(R.id.event_max_participants);
        deleteBtn = findViewById(R.id.delete_event);
        backBtn = findViewById(R.id.back_button);
        removeBtn = findViewById(R.id.remove_option);
        replaceBtn = findViewById(R.id.replace_option);
        adminController = new AdminController(new FirebaseManager());
        builder = new AlertDialog.Builder(this);

        // Getting Data from previous
        Intent intent = getIntent();
        bundle = intent.getExtras();
        assert bundle != null;
        event = bundle.getParcelable("event");
        if (event != null) {
            eventDate = event.getDate().toString();
            eventQRCode = event.getQrCode();
            eventLocation = event.getLocation();
            eventCapacity = event.getCapacity();
        }
        loadEventPoster(bundle.getString("poster"));

        if (event.getDate() != null)
            date.setText("Date: " + event.getDate().toString());
        if (event.getDescription() != null)
            description.setText("Description: " + event.getDescription());
        if (eventPoster != null)
            poster.setImageBitmap(event.getPoster());
        if (event.getQrCode() != null)
            qrCode.setImageBitmap(event.getQrCode());
        if (event.getLocation() != null)
            location.setText("Location: " + event.getLocation());
        if (event.getCapacity() != 0)
            capacity.setText("Maximum Capcity: " + String.valueOf(event.getCapacity()));

        Context context = this;

        intent1 = new Intent();


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent(event);
                setResult(RESULT_OK, intent1);
                finish();
            }
        });

        replaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Replace Poster")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                            }
                        })
                        .create().show();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Remove Poster")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bitmap defImg = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_image_24);
                                event.setPoster(defImg);
                                poster.setImageBitmap(defImg);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do nothing
                                dialogInterface.cancel();
                            }
                        })
                        .create().show();
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("Do you want to save changes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adminController.updateEvent(event);
                        setResult(RESULT_OK, intent1);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // just finis the activity.
                                finish();
                            }
                        })
                        .setTitle("Saving Alert")
                        .create()
                        .show();
            }
        });

    }

    private void deleteEvent(Event event) {
        adminController.deleteEvent(event);
    }


    /**
     * Handles the result from activities started with startActivityForResult().
     * In this case, it processes the selected image from the image picker.
     *
     * @param reqCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller.
     */
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();

                // Open InputStream for image processing
                InputStream imageStream = this.getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                event.setPoster(selectedImage);
                eventPoster = event.getPoster();
                if(eventPoster != null) poster.setImageBitmap(eventPoster);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image not found", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show();
        }
    }


    private void loadEventPoster(String imagePath) {
        if (imagePath != null && event != null) {
            try {
                FileInputStream fis = this.openFileInput(imagePath);
                Bitmap posterImage = BitmapFactory.decodeStream(fis);
                fis.close();
                event.setPoster(posterImage);
                eventPoster = posterImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


