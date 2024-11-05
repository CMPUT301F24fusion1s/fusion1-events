// EditProfileFragment.java
package com.example.fusion1_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditProfileFragment extends Fragment {

    private EditText editProfileName, editProfileEmail, editProfilePhone;
    private Button saveChangesButton;
    private User user;
    private UserController userController = new UserController(new FirebaseManager()); // Assumes FirebaseManager setup

    public void setUser(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_edit_profile_page, container, false);

        // Initialize views
        editProfileName = view.findViewById(R.id.e_name);
        editProfileEmail = view.findViewById(R.id.e_email);
        editProfilePhone = view.findViewById(R.id.e_phone);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);

        // Populate fields with the current user data
        if (user != null) {
            editProfileName.setText(user.getName());
            editProfileEmail.setText(user.getEmail());
            editProfilePhone.setText(user.getPhoneNumber());
        }

        // Set up the save button
        saveChangesButton.setOnClickListener(v -> {
            // Update user object with new values
            user.setName(editProfileName.getText().toString());
            user.setEmail(editProfileEmail.getText().toString());
            user.setPhoneNumber(editProfilePhone.getText().toString());

            // Call to update user profile
            userController.updateProfile(user.getUserId(), user);

            // Go back to UserProfileFragment after saving
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}

