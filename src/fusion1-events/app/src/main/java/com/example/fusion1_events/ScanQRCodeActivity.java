package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

/**
 * ScanQRCodeActivity is an activity that handles the QR code scanning process using the ZXing library.
 * It launches a QR code scanner and handles the result by displaying it in an AlertDialog.
 */
public class ScanQRCodeActivity extends AppCompatActivity {

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

        // Set the content view (assuming the layout file has been defined correctly)
        setContentView(R.layout.scan_qr_code);
        Log.d(TAG, "onCreate: Content view set");

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) {
            Log.e(TAG, "onCreate: BottomNavigationView not found in layout. Check layout XML.");
        } else {
            // Set up the listener for navigation
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            Log.d(TAG, "onNavigationItemSelected: Home selected. Navigating to MainMenuActivity.");
                            startActivity(new Intent(ScanQRCodeActivity.this, MainMenuActivity.class));
                            return true;
                        case R.id.camera:
                            Log.d(TAG, "onNavigationItemSelected: Camera selected. Staying on ScanQRCodeActivity.");
                            return true;
                    }
                    return false;
                }
            });
        }

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
     * If a QR code is successfully scanned, it displays the result in an AlertDialog.
     * If the scan is canceled or no result is found, it shows a Toast message and finishes the activity.
     */
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            Log.d(TAG, "barLauncher: Scan result received");
            // Display the scanned result in an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCodeActivity.this);
            builder.setTitle("Scan Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", (DialogInterface dialog, int which) -> dialog.dismiss());
            builder.show();
        } else {
            Log.d(TAG, "barLauncher: Scan canceled or no result found");
            // If the scan was canceled, show a toast message
            Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no result
        }
    });
}
