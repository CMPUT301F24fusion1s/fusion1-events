package com.example.fusion1_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserProfileFragment extends Fragment {

    private static final String ARG_USER = "user"; // Key for storing User object

    private User user;

    // Use this method to create a new instance with a User object
    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user); // Pass the entire User object
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getParcelable(ARG_USER); // Retrieve User object
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_page, container, false);

        // Display user details in TextViews
        TextView profileName = view.findViewById(R.id.tvProfileName);
        TextView profileEmail = view.findViewById(R.id.tvProfileEmail);
        TextView profilePhone = view.findViewById(R.id.tvProfilePhoneNumber);

        if (user != null) {
            profileName.setText(user.getName());
            profileEmail.setText(user.getEmail());
            profilePhone.setText(user.getPhoneNumber());
        }

        // Set up Edit Profile button to open EditProfileFragment
        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            // Call MainMenuActivity to open EditProfileFragment
            if (getActivity() instanceof MainMenuActivity) {
                ((MainMenuActivity) getActivity()).showEditProfileFragment(user);
            }
        });

        // Set up Back Arrow button to navigate back
        ImageButton backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
