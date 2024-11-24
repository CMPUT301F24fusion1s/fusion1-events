package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminEventActivity extends AppCompatActivity {
    ImageView eventPoster;
    TextView date;
    TextView description;
    TextView location;
    Bundle bundle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_event_details);

        date = findViewById(R.id.event_date);
        description = findViewById(R.id.event_description);
        eventPoster = findViewById(R.id.event_image);
        location = findViewById(R.id.event_location);

        Intent intent = getIntent();
        bundle = intent.getExtras();
        assert bundle != null;
        Event event = bundle.getParcelable("event");

        date.setText("Date: " + event.getDate().toString());
        description.setText("Description" + event.getDescription());
        if(event.getPoster() != null)
            eventPoster.setImageBitmap(event.getPoster());
        location.setText("Location" + event.getLocation());
    }
}
