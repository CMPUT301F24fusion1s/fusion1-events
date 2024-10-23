package com.example.fusion1_events;

import android.content.Context;
import android.provider.Settings;

public class DeviceManager {
    // Class Context is used to access system resources
    private Context context;

    public DeviceManager(Context context) {
        this.context = context;
    }

    /**
     * Retrieves the device ID (or generates one if necessary).
     */
    public String getOrCreateDeviceId() {
        // Retrieve the unique Android device ID
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}