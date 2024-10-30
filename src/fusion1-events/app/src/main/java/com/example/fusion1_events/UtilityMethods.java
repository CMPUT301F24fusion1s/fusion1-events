package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UtilityMethods {
    /**
     * Encodes a Bitmap image to a Base64 string. This allows the image to be stored as a string in Firestore.
     *
     * @param bitmap The Bitmap image to encode.
     * @return The Base64 encoded string.
     */
    public static String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Decodes a Base64 string to a Bitmap image.
     *
     * @param base64String The Base64 encoded string.
     * @return The decoded Bitmap image.
     */
    public static Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Converts a list of UUIDs to a list of strings.
     *
     * @param uuidList The list of UUIDs to convert.
     * @return A list of strings representing the UUIDs.
     */
    public static List<String> convertUuidListToStringList(List<UUID> uuidList) {
        return uuidList.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of strings to a list of UUIDs.
     *
     * @param stringList The list of strings to convert.
     * @return A list of UUIDs parsed from the strings.
     */
    public static List<UUID> convertStringListToUuidList(List<String> stringList) {
        return stringList.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
}
