package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
	LocationRequest locationRequest;
	private FusedLocationProviderClient fusedLocationClient;
	private static final int REQUEST_CHECK_SETTINGS = 100;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
	Location userLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = getApplicationContext();
		setContentView(R.layout.register_page);

		initializeManagers();
		setupInitialLogin();

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

		if (checkLocationPermission()) {
			getLocation(); // Get last known location
		}

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
				UUID.randomUUID().toString(), deviceId, null, userLocation, true);
//Admin(String email, String name, String role, String phoneNumber, String userId, String deviceId, Bitmap profileImage)
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


	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CHECK_SETTINGS) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Location settings satisfied", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Location settings not satisfied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					LOCATION_PERMISSION_REQUEST_CODE);
			return false;
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getLocation();
			} else {
				Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void getLocation() {
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

		if (checkLocationPermission()) {
			fusedLocationClient.getLastLocation()
					.addOnSuccessListener(this, new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							if (location != null) {
								// Use the location object
								double latitude = location.getLatitude();
								double longitude = location.getLongitude();
								Log.d("Location", "Lat: " + latitude + ", Long: " + longitude);
								Toast.makeText(MainActivity.this,
										"Lat: " + latitude + ", Long: " + longitude,
										Toast.LENGTH_SHORT).show();
								userLocation = location;
							} else {
								Toast.makeText(MainActivity.this,
										"Location unavailable, try again",
										Toast.LENGTH_SHORT).show();
							}
						}
					})
					.addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							startLocationUpdates();
						}
					});
		}
	}

	private void startLocationUpdates() {
		if (checkLocationPermission()) {
			LocationRequest locationRequest = new LocationRequest.Builder(
					LocationRequest.PRIORITY_HIGH_ACCURACY,
					10000) // 10 seconds interval
					.build()
					.setFastestInterval(5000); // 5 seconds fastest interval;

			fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
				@Override
				public void onLocationResult(LocationResult locationResult) {
					if (locationResult != null) {
						for (Location location : locationResult.getLocations()) {
							double latitude = location.getLatitude();
							double longitude = location.getLongitude();
							Log.d("Location Update", "Lat: " + latitude + ", Long: " + longitude);
						}
					}
				}
			}, Looper.getMainLooper());
		}
	}

}
