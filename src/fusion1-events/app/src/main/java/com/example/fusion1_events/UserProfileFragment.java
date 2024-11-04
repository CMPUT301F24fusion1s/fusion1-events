package com.example.fusion1_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserProfileFragment extends Fragment {

    private static final String ARG_USER_NAME = "userName";
    private static final String ARG_USER_EMAIL = "userEmail";
    private static final String ARG_USER_PHONE = "userPhoneNumber";
    private static final String ARG_USER_DEVICE_ID = "userDeviceId";

    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userDeviceId;

    public static UserProfileFragment newInstance(String userName, String userEmail, String userPhoneNumber, String userDeviceId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, userName);
        args.putString(ARG_USER_EMAIL, userEmail);
        args.putString(ARG_USER_PHONE, userPhoneNumber);
        args.putString(ARG_USER_DEVICE_ID, userDeviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_USER_NAME);
            userEmail = getArguments().getString(ARG_USER_EMAIL);
            userPhoneNumber = getArguments().getString(ARG_USER_PHONE);
            userDeviceId = getArguments().getString(ARG_USER_DEVICE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_page, container, false);

        // Populate the profile layout with user information
        TextView profileName = view.findViewById(R.id.tvProfileName);
        TextView profileEmail = view.findViewById(R.id.tvProfileEmail);
        TextView profilePhone = view.findViewById(R.id.tvProfilePhoneNumber);

        profileName.setText(userName);
        profileEmail.setText(userEmail);
        profilePhone.setText(userPhoneNumber);

        // Set up the back arrow button
        // TODO more buttons need to be set
        ImageButton backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
