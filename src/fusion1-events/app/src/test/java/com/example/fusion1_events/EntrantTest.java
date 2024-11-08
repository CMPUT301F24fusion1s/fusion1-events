package com.example.fusion1_events;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Entrant class.
 * This class tests the functionality of the Entrant class, including constructors and notification methods.
 */
public class EntrantTest {

    private Entrant entrant;
    private Bitmap mockBitmap;
    private Location mockLocation;
    private Context mockContext;

    /**
     * Sets up the test environment by creating mock objects and initializing an Entrant instance.
     */
    @Before
    public void setUp() {
        // Create mock objects for Bitmap, Location, and Context
        mockBitmap = mock(Bitmap.class);
        mockLocation = mock(Location.class);
        mockContext = mock(Context.class);
        android.content.res.Resources mockResources = mock(android.content.res.Resources.class);

        // Mock getting resources from context
        when(mockContext.getResources()).thenReturn(mockResources);
        // Initialize an Entrant object with test data
        entrant = new Entrant("test@example.com", "Test User", "entrant", "1234567890", "user123", "device123", mockBitmap, mockLocation, true);
    }

    /**
     * Tests the constructor of the Entrant class to ensure all fields are initialized correctly.
     */
    @Test
    public void testConstructor() {
        // Test that the constructor initializes all fields correctly
        assertEquals("test@example.com", entrant.getEmail());
        assertEquals("Test User", entrant.getName());
        assertEquals("entrant", entrant.getRole());
        assertEquals("1234567890", entrant.getPhoneNumber());
        assertEquals("user123", entrant.getUserId());
        assertEquals("device123", entrant.getDeviceId());
        assertEquals(mockBitmap, entrant.getProfileImage());
        assertEquals(mockLocation, entrant.location);
        assertTrue(entrant.getNotificationEnabled());
    }

    /**
     * Tests the notification methods of the Entrant class, including turning notifications on and off.
     */
    @Test
    public void testNotificationMethods() {
        // Test turning notifications off
        entrant.turnNotificationOff();
        assertFalse(entrant.getNotificationEnabled());

        // Test turning notifications back on
        entrant.turnNotificationOn();
        assertTrue(entrant.getNotificationEnabled());
    }
}
