package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.os.Parcel;

import com.google.zxing.WriterException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
/*WARNING: No manifest file found at .\AndroidManifest.xml.
Falling back to the Android OS resources only.
To remove this warning, annotate your test class with*/
@Config(manifest=Config.NONE)

public class QRCodeTest {

    private UUID mockEventId;
    private Bitmap mockBitmap;

    @Before
    public void setUp() {
        // Generate a mock event ID
        mockEventId = UUID.randomUUID();
        // Use Robolectric to create a Bitmap
        mockBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
    }

    @Test
    public void testGenerateQRCode() throws WriterException {
        // Generate a QR code for the mock event ID
        QRCode.QRCodeResult result = QRCode.generateQRCode(mockEventId);

        // Verify that the QR code result is not null
        assertNotNull("QRCodeResult should not be null", result);

        // Verify that the QR code bitmap is not null
        assertNotNull("Bitmap should not be null", result.getBitmap());

        // Verify that the QR code hash matches the event ID string
        assertEquals("Hash should match event ID", mockEventId.toString(), result.getHash());
    }

    @Test
    public void testQRCodeResultParcelable() {
        QRCode.QRCodeResult originalResult = new QRCode.QRCodeResult(mockBitmap, mockEventId.toString());

        // Write the original QRCodeResult to a Parcel
        Parcel parcel = Parcel.obtain();
        originalResult.writeToParcel(parcel, 0);

        // Reset the Parcel to read from the beginning
        parcel.setDataPosition(0);

        // Create a new QRCodeResult from the Parcel
        QRCode.QRCodeResult recreatedResult = QRCode.QRCodeResult.CREATOR.createFromParcel(parcel);

        // Verify that the recreated QRCodeResult is not null
        assertNotNull("Recreated QRCodeResult should not be null", recreatedResult);

        // Verify that the bitmap is correctly recreated
        assertNotNull("Bitmap in recreated QRCodeResult should not be null", recreatedResult.getBitmap());

        // Verify that the hash matches the original
        assertEquals("Hash should match original", originalResult.getHash(), recreatedResult.getHash());
    }

    @Test
    public void testGenerateQRCodeHash() {
        // Generate a hash for the mock event ID
        String hash = QRCode.generateQRCodeHash(mockEventId.toString());

        // Verify that the generated hash matches the event ID string
        assertEquals("Generated hash should match event ID", mockEventId.toString(), hash);
    }
}
