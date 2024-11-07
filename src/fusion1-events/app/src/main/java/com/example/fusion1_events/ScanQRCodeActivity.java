package com.example.fusion1_events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScanQRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiate the QR code scanning when the activity is created
        scanCode();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barLauncher.launch(options);
    }

    // Handles the result of the QR scan
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