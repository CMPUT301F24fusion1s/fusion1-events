package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * ScanQRCodeActivity is an activity that handles the QR code scanning process using the ZXing library.
 * It launches a QR code scanner and handles the result by displaying it in an AlertDialog.
 */
public class ScanQRCodeActivity extends AppCompatActivity {

    /**
     * Called when the activity is created.
     * Initiates the QR code scanning process as soon as the activity is started.
     *
     * @param savedInstanceState Bundle containing data from previous instance (if available)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiate the QR code scanning when the activity is created
        scanCode();
    }

    /**
     * Configures the scanning options and launches the QR scanner.
     * Sets various options such as prompt text, beep sound, and screen orientation lock during scanning.
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barLauncher.launch(options);
    }

    /**
     * ActivityResultLauncher that handles the result of the QR scan.
     * If a QR code is successfully scanned, it displays the result in an AlertDialog.
     * If the scan is canceled or no result is found, it shows a Toast message and finishes the activity.
     */
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            // Display the scanned result in an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCodeActivity.this);
            builder.setTitle("Scan Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", (DialogInterface dialog, int which) -> dialog.dismiss());
            builder.show();
        } else {
            // If the scan was canceled, show a toast message
            Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no result
        }
    });
}