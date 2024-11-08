package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import android.view.MenuItem;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ScanQRCodeActivity is an activity that handles the QR code scanning process using the ZXing library.
 * It launches a QR code scanner and handles the result by displaying it in an AlertDialog.
 */
public class ScanQRCodeActivity extends BaseActivity {

    private static final String TAG = "ScanQRCodeActivity";

    /**
     * Called when the activity is created.
     * Initiates the QR code scanning process as soon as the activity is started.
     *
     * @param savedInstanceState Bundle containing data from previous instance (if available)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting ScanQRCodeActivity");

        // Initiate the QR code scanning when the activity is created
        scanCode();
    }

    /**
     * Configures the scanning options and launches the QR scanner.
     * Sets various options such as prompt text, beep sound, and screen orientation lock during scanning.
     */
    private void scanCode() {
        Log.d(TAG, "scanCode: Setting up scan options");
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);

        try {
            Log.d(TAG, "scanCode: Launching QR scanner");
            barLauncher.launch(options);
        } catch (Exception e) {
            Log.e(TAG, "scanCode: Failed to launch scanner", e);
            Toast.makeText(this, "Error launching scanner", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ActivityResultLauncher that handles the result of the QR scan.
     * If a QR code is successfully scanned, it displays the event details page.
     * If the scan is canceled or no result is found, it shows a Toast message and finishes the activity.
     */
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            Log.d(TAG, "barLauncher: Scan result received");
            String qrHash = result.getContents();

            // Get event details from Firebase using the scanned qrHash
            FirebaseManager firebaseManager = new FirebaseManager();
            firebaseManager.getEventByQRHash(qrHash, new FirebaseManager.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    // Navigate to event details
                    Intent intent = new Intent(ScanQRCodeActivity.this, EventDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    String tempFileName = "temp_event_poster.jpg";

                    try {
                        if (event.getPoster() != null) {
                            FileOutputStream fos = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                            event.getPoster().compress(Bitmap.CompressFormat.JPEG, 90, fos);
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error saving poster image", e);
                        e.printStackTrace();
                    }

                    bundle.putParcelable("event", event);
                    bundle.putString("poster_image_path", tempFileName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish(); // Close the scanner activity
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Error fetching event details", e);
                    Toast.makeText(ScanQRCodeActivity.this,
                            "Error: Could not find event", Toast.LENGTH_SHORT).show();
                    finish(); // Close the scanner activity
                }
            });
        } else {
            Log.d(TAG, "barLauncher: Scan canceled or no result found");
            // If the scan was canceled, show a toast message
            Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            // TODO: navigate to previous activity, not MainMenuActivity
            navigateToActivity(MainMenuActivity.class);
        }
    });

    @Override
    protected int getLayoutResourceId() {
        return R.layout.scan_qr_code;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.camera;
    }
}
