package com.example.fusion1_events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * UserProfileFragment is a Fragment that displays the user's profile details.
 * It shows information such as the user's name, email, phone number, and profile picture.
 * The fragment also provides an option to edit the profile and a button to navigate back.
 */
public class UserProfileFragment extends Fragment {

    // Key for storing the User object in the fragment's arguments
    private static final String ARG_USER = "user"; // Key for storing User object

    // User object that contains the profile information to be displayed
    private User user;

    /**
     * Static factory method to create a new instance of UserProfileFragment with a User object.
     *
     * @param user The User object containing the profile data.
     * @return A new instance of UserProfileFragment with the given User data.
     */
    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user); // Pass the entire User object
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to do initial creation of the fragment.
     * Retrieves the User object from the arguments if it exists.
     *
     * @param savedInstanceState The saved state of the fragment.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getParcelable(ARG_USER); // Retrieve User object
        }
    }

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root View for the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_page, container, false);

        // Display user details in TextViews
        TextView profileName = view.findViewById(R.id.tvProfileName);
        TextView profileEmail = view.findViewById(R.id.tvProfileEmail);
        TextView profilePhone = view.findViewById(R.id.tvProfilePhoneNumber);
        ImageView profileImage = view.findViewById(R.id.profileImage_main);

        if (user != null) {
            profileName.setText(user.getName());
            profileEmail.setText(user.getEmail());
            profilePhone.setText(user.getPhoneNumber());
            if(user.getProfileImage() == null)
                profileImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_user));
            else
                profileImage.setImageBitmap(user.getProfileImage());

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
/**
 * Summary:
 * - The UserProfileFragment displays user details such as name, email, phone number, and profile picture.
 * - The fragment provides an Edit Profile button to allow users to edit their information.
 * - A back arrow button is also available to navigate back to the previous screen.
 * - The User object is passed to the fragment using a Bundle, and the details are displayed accordingly.
 */
