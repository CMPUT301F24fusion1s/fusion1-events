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

/**
 * The EditProfileFragment class represents a user interface for editing a user's profile information.
 * It allows users to update their name, email, phone number, and profile picture.
 * This fragment interacts with the UserController class to perform operations related to updating the user data in a backend system.
 * It also provides mechanisms for selecting, replacing, or removing the user's profile picture.
 */
public class EditProfileFragment extends Fragment {
    // UI Elements for editing profile information
    private EditText editProfileName, editProfileEmail, editProfilePhone;
    private TextView removeProfilePic, replaceProfilePic;
    private ImageView profileImage;
    private Button saveChangesButton;
    private User user; // Need to be in UserController Class
    private UserController userController = new UserController(new FirebaseManager()); // Assumes FirebaseManager setup
    private final int RESULT_LOAD_IMG = 0, RESULT_OK = -1;

    // The User object representing the current user
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
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
            Entrant entrant = (Entrant) user;
            Bitmap userPicture = entrant.getProfileImage();
            if (userPicture != null)
                profileImage.setImageBitmap(userPicture);
            else
                profileImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_user));
        }

        // Set up the save button listener to save the updated profile data
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
                user.removeProfileImage();
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
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImage.setImageBitmap(selectedImage);
                // Save selected image by user in his model class
                userController.replaceImage(selectedImage, user, imageUri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Image not found", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked an image", Toast.LENGTH_LONG).show();
        }
    }
}
/**
 * Note:
 * - This fragment allows users to update their profile, including personal information and profile picture.
 * - It uses UserController to interact with backend services such as Firebase for updating user details.
 * - The image picker functionality enables users to select an image from their gallery.
 * - Permissions for accessing external storage must be handled to ensure compatibility with Android 11 and higher.
 */


