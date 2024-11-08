package com.example.fusion1_events;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
/*WARNING: No manifest file found at .\AndroidManifest.xml.
Falling back to the Android OS resources only.
To remove this warning, annotate your test class with*/
@Config(manifest=Config.NONE)
public class DeviceManagerTest {

    @Mock
    Context mockContext;

    @Mock
    ContentResolver mockContentResolver;

    private DeviceManager deviceManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the context and content resolver
        when(mockContext.getContentResolver()).thenReturn(mockContentResolver);

        // Set up the DeviceManager with the mocked context
        deviceManager = new DeviceManager(mockContext);
    }

    @Test
    public void testGetOrCreateDeviceId() {
        String expectedDeviceId = "unique_device_id_12345";

        // Mock the Settings.Secure.getString() method using Robolectric
        Settings.Secure.putString(mockContext.getContentResolver(), Settings.Secure.ANDROID_ID, expectedDeviceId);

        String actualDeviceId = deviceManager.getOrCreateDeviceId();

        // Verify that the returned device ID matches the expected one
        assertEquals(expectedDeviceId, actualDeviceId);
    }
}
