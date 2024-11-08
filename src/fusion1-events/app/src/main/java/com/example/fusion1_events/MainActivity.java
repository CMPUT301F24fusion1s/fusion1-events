package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * The MainActivity class represents the starting activity of the application.
 * It is responsible for managing user login and registration, navigating to different menus based on the user type,
 * and managing the device ID to uniquely identify users.
 */
public class MainActivity extends AppCompatActivity {

	private DeviceManager deviceManager;
	private FirebaseManager firebaseManager;
	private UserController userController;
	private static Context appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = getApplicationContext();
		setContentView(R.layout.register_page);

		initializeManagers();
		setupInitialLogin();
	}

	private void initializeManagers() {
		deviceManager = new DeviceManager(this);
		firebaseManager = new FirebaseManager();
		userController = new UserController(firebaseManager);
	}

	private void setupInitialLogin() {
		String deviceId = deviceManager.getOrCreateDeviceId();
		Log.d("DeviceID", "Device ID: " + deviceId);

		Button registrationButton = findViewById(R.id.registerButton);
		registrationButton.setOnClickListener(v -> handleLoginAttempt(deviceId));
	}

	private void handleLoginAttempt(String deviceId) {
		Log.d("register button ", " pressed");
		userController.userLogin(deviceId, new FirebaseManager.UserCallback() {
			@Override
			public void onSuccess(User user) {
				Log.d("MainActivity", "User found: " + user.toString());
				Class<?> targetActivity = determineTargetActivity(user);
				if (targetActivity != null) {
					navigateToMainMenu(targetActivity, user);
				}
			}

			@Override
			public void onFailure(Exception e) {
				Log.e("FirebaseManager", "User not found or error: " + e.getMessage(), e);
				showRegistrationForm(deviceId);
			}
		});
	}

	private Class<?> determineTargetActivity(User user) {
		if (user instanceof Admin) {
			return AdminMainMenuActivity.class;
		} else if (user instanceof Entrant) {
			return MainMenuActivity.class;
		}
		return null;
	}

	private void showRegistrationForm(String deviceId) {
		Log.d("MainActivity", "Navigating to register new user layout.");
		setContentView(R.layout.register_new_user);
		setupNewUserRegistration(deviceId);
	}

	/**
	 * Sets up the user registration layout, allowing new users to register.
	 *
	 * @param deviceId The device ID used to identify the new user.
	 */
	private void setupNewUserRegistration(String deviceId) {
		EditText nameField = findViewById(R.id.et_name);
		EditText emailField = findViewById(R.id.et_email);
		EditText phoneNumberField = findViewById(R.id.et_phone_number);
		Button registerButton = findViewById(R.id.btn_register);

		registerButton.setOnClickListener(v -> {
			if (validateRegistrationFields(nameField, emailField, phoneNumberField)) {
				registerNewUser(deviceId, nameField.getText().toString().trim(),
						emailField.getText().toString().trim(),
						phoneNumberField.getText().toString().trim());
			}
		});
	}

	private boolean validateRegistrationFields(EditText nameField, EditText emailField, EditText phoneNumberField) {
		String name = nameField.getText().toString().trim();
		String email = emailField.getText().toString().trim();
		String phoneNumber = phoneNumberField.getText().toString().trim();

		if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
			Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void registerNewUser(String deviceId, String name, String email, String phoneNumber) {
		Entrant entrant = new Entrant(email, name, "Entrant", phoneNumber,
				UUID.randomUUID().toString(), deviceId, null, null, true);

		userController.signUpUser(entrant);
		Toast.makeText(this, "User registration in progress", Toast.LENGTH_SHORT).show();
		navigateToMainMenu(MainMenuActivity.class, entrant);
	}

	/**
	 * Navigates to the main menu activity for the specified user type.
	 *
	 * @param activityClass The activity class to navigate to.
	 * @param user          The user object containing user details.
	 */
	private void navigateToMainMenu(Class<?> activityClass, User user) {
		Intent intent = new Intent(MainActivity.this, activityClass);
		Bundle bundle = new Bundle();
		String tempFileName = "temp_profile_image.jpg";

		if (user.getProfileImage() != null) {
			try {
				FileOutputStream fos = this.openFileOutput(tempFileName, Context.MODE_PRIVATE);
				user.getProfileImage().compress(Bitmap.CompressFormat.JPEG, 90, fos);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		bundle.putParcelable("user", user);
		bundle.putString("profile_image_path", tempFileName);
		intent.putExtras(bundle);

		startActivity(intent);
		finish(); // Close the current activity to prevent going back to it
	}

	/**
	 * Gets the application context.
	 *
	 * @return The application context.
	 */
	public static Context getAppContext() {
		return appContext;
	}
}
