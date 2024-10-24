package com.example.fusion1_events;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.zxing.WriterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class QRCodeInstrumentedTest {

    private Event mockEvent;
    private UUID mockUUID;

    @Before
    public void setUp() {
        // Create a mock Event object
        mockEvent = Mockito.mock(Event.class);

        // Create a mock UUID
        mockUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Configure the mock to return the mock UUID when getId() is called
        when(mockEvent.getId()).thenReturn(mockUUID);
    }

    @Test
    public void testGenerateQRCode() {
        try {
            // Act: Generate the QR code
            QRCode.QRCodeResult result = QRCode.generateQRCode(mockEvent.getId());

            // Assert: Verify the bitmap and hash
            assertNotNull("QR Code bitmap should not be null", result.getBitmap());
            assertEquals("Bitmap width should be 200", 200, result.getBitmap().getWidth());
            assertEquals("Bitmap height should be 200", 200, result.getBitmap().getHeight());
            assertEquals("QR Code hash should match the event UUID", mockUUID.toString(), result.getHash());
        } catch (WriterException e) {
            fail("WriterException should not be thrown");
        }
    }

    @Test
    public void testGenerateQRCodeHash() {
        // Act: Generate the QR code hash
        String hash = QRCode.generateQRCodeHash(mockUUID.toString());

        // Assert: Verify the hash
        assertEquals("QR Code hash should match the event UUID", mockUUID.toString(), hash);
    }
}
