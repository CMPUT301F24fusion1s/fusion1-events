package com.example.fusion1_events;

import android.graphics.Bitmap;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class UtilityMethodsInstrumentedTest {

    @Test
    public void testEncodeBitmapToBase64() {
        // Create a sample Bitmap
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        // Encode the Bitmap to Base64
        String base64String = UtilityMethods.encodeBitmapToBase64(bitmap);

        // Decode back to Bitmap
        Bitmap decodedBitmap = UtilityMethods.decodeBase64ToBitmap(base64String);

        // Check if the decoded Bitmap is not null
        assertNotNull(decodedBitmap);
        // Check if the decoded Bitmap has the same dimensions
        assertEquals(bitmap.getWidth(), decodedBitmap.getWidth());
        assertEquals(bitmap.getHeight(), decodedBitmap.getHeight());
    }

    @Test
    public void testDecodeBase64ToBitmap() {
        // Create a sample Bitmap
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        // Encode the Bitmap to Base64
        String base64String = UtilityMethods.encodeBitmapToBase64(bitmap);

        // Decode back to Bitmap
        Bitmap decodedBitmap = UtilityMethods.decodeBase64ToBitmap(base64String);

        // Check if the decoded Bitmap is not null
        assertNotNull(decodedBitmap);
        // Check if the decoded Bitmap has the same dimensions
        assertEquals(bitmap.getWidth(), decodedBitmap.getWidth());
        assertEquals(bitmap.getHeight(), decodedBitmap.getHeight());
    }

    @Test
    public void testConvertUuidListToStringList() {
        // Create a list of UUIDs
        List<UUID> uuidList = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Convert to list of strings
        List<String> stringList = UtilityMethods.convertUuidListToStringList(uuidList);

        // Check if the list sizes match
        assertEquals(uuidList.size(), stringList.size());

        // Check if each string is a valid UUID
        for (String uuidString : stringList) {
            assertNotNull(UUID.fromString(uuidString));
        }
    }

    @Test
    public void testConvertStringListToUuidList() {
        // Create a list of UUIDs and convert to strings
        List<UUID> uuidList = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        List<String> stringList = UtilityMethods.convertUuidListToStringList(uuidList);

        // Convert back to UUIDs
        List<UUID> convertedUuidList = UtilityMethods.convertStringListToUuidList(stringList);

        // Check if the list sizes match
        assertEquals(uuidList.size(), convertedUuidList.size());

        // Check if each UUID matches the original
        for (int i = 0; i < uuidList.size(); i++) {
            assertEquals(uuidList.get(i), convertedUuidList.get(i));
        }
    }
}
