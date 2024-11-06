package com.example.fusion1_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import javax.annotation.Nullable;

public class EventDetailsFragment extends Fragment {
    private Event event;
    private String userId;
    DeviceManager deviceManager;
    FirebaseManager firebaseManager;

    public EventDetailsFragment newInstance(Event event, String userId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getParcelable("event");
            userId = getArguments().getString("userId");
            deviceManager = new DeviceManager(getContext());
            firebaseManager = new FirebaseManager();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_details, container, false);

        // Display event details in the view
        TextView eventName = view.findViewById(R.id.eventTitle);
        TextView eventDate = view.findViewById(R.id.eventDateTime);
        TextView eventLocation = view.findViewById(R.id.location);
        TextView eventMaxParticipants = view.findViewById(R.id.maxParticipants);
        TextView eventGeolocationRequired = view.findViewById(R.id.geolocationRequired);
        TextView eventDescription = view.findViewById(R.id.description);
        ImageView eventPoster = view.findViewById(R.id.eventPoster);

        if (event != null) {
            eventName.setText(event.getName());
            eventDate.setText(event.getDate().toString());
            eventLocation.setText(event.getLocation());
            eventMaxParticipants.setText(String.valueOf(event.getCapacity()));
            eventGeolocationRequired.setText(event.getGeolocationRequired() ? "Yes" : "No");
            eventDescription.setText(event.getDescription());
            eventPoster.setImageBitmap(event.getPoster());
        }

        // Depending on whether the user is the organizer, show different options
        // The Join Waitlist button should only be visible to non-organizers
        // The Join Waitlist button should become an Edit Event button for organizers
        // If the user is non-organizer and has already joined the waitlist, the button should change to "Leave Waitlist"

        Button primaryActionButton = view.findViewById(R.id.primaryActionButton);

        firebaseManager.getUserByDeviceId(userId, new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user.getUserId().equals(event.getOrganizerId().toString())) {
                    // User is the organizer
                    primaryActionButton.setText("Edit Event");
                } else {
                    // User is not the organizer
                    if (event.getWaitlist().contains(user.getUserId())) {
                        primaryActionButton.setText("Leave Waitlist");
                    } else {
                        primaryActionButton.setText("Join Waitlist");
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                // User not found, show error
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void editEvent() {
        // Navigate to the EditEventFragment
//        EditEventFragment editEventFragment = new EditEventFragment();
//        editEventFragment.newInstance(event);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, editEventFragment)
//                .addToBackStack(null)
//                .commit();

        // Temporary implementation
        Toast.makeText(getContext(), "Edit Event button clicked", Toast.LENGTH_SHORT).show();
    }

    private void joinWaitlist() {
        firebaseManager.getUserByDeviceId(deviceManager.getOrCreateDeviceId(), new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                event.getWaitlist().add(user.getUserId());
                firebaseManager.updateExistingEvent(event);
            }

            @Override
            public void onFailure(Exception e) {
                // User not found, show error
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void leaveWaitlist() {

        firebaseManager.getUserByDeviceId(deviceManager.getOrCreateDeviceId(), new FirebaseManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                event.getWaitlist().remove(user.getUserId());
                firebaseManager.updateExistingEvent(event);
            }

            @Override
            public void onFailure(Exception e) {
                // User not found, show error
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
