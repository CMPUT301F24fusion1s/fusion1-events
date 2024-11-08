package com.example.fusion1_events;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class DeviceManagerTest {

    @Mock
    Context mockContext;

    @Mock
    ContentResolver mockContentResolver;

    private DeviceManager deviceManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockContext.getContentResolver()).thenReturn(mockContentResolver);
        deviceManager = new DeviceManager(mockContext);
    }

    @Test
    public void testGetOrCreateDeviceId() {
        String expectedDeviceId = "unique_device_id_12345";
        when(Settings.Secure.getString(mockContentResolver, Settings.Secure.ANDROID_ID)).thenReturn(expectedDeviceId);

        String actualDeviceId = deviceManager.getOrCreateDeviceId();

        assertEquals(expectedDeviceId, actualDeviceId);
    }
}
