// EditProfileFragment.java
package com.example.fusion1_events;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditProfileFragment extends Fragment {

    private EditText editProfileName, editProfileEmail, editProfilePhone;
    private TextView removeProfilePic, replaceProfilePic;
    private ImageView profileImage;
    private Button saveChangesButton;
    private User user;
    private UserController userController = new UserController(new FirebaseManager()); // Assumes FirebaseManager setup
    private final int RESULT_LOAD_IMG = 0, RESULT_OK = -1;

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
        replaceProfilePic = view.findViewById(R.id.replace);
        removeProfilePic = view.findViewById(R.id.remove);
        profileImage = view.findViewById(R.id.profileImage);

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

        removeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileImage.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_user));
            }
        });

        replaceProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();

                // Open InputStream for image processing
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Convert Bitmap to Drawable
                Drawable drawableImage = new BitmapDrawable(getResources(), selectedImage);
                profileImage.setImageDrawable(drawableImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Image not found", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked an image", Toast.LENGTH_LONG).show();
        }
    }

}

