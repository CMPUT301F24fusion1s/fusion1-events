package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.os.Parcel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class UserTest {

    private User user;
    private Bitmap mockBitmap;

    @Before
    public void setUp() {
        // Use Mockito to create a mock bitmap for testing
        mockBitmap = Mockito.mock(Bitmap.class);
        user = new User("test@example.com", "Test User", "Admin", "1234567890", "userId123", "deviceId123", mockBitmap);
    }

    @Test
    public void testUserConstructor() {
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
        assertEquals("Admin", user.getRole());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("userId123", user.getUserId());
        assertEquals("deviceId123", user.getDeviceId());
        assertNotNull(user.getProfileImage());
    }

    @Test
    public void testSetName() {
        user.setName("New Name");
        assertEquals("New Name", user.getName());

        user.setName(null);
        assertNotEquals(null, user.getName());
    }

    @Test
    public void testSetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    public void testSetPhoneNumber() {
        user.setPhoneNumber(" 0987654321 ");
        assertEquals("0987654321", user.getPhoneNumber());

        user.setPhoneNumber("");
        assertNotEquals("", user.getPhoneNumber());
    }

    @Test
    public void testSetUserId() {
        user.setUserId("newUserId");
        assertEquals("newUserId", user.getUserId());
    }

    @Test
    public void testSetDeviceId() {
        user.setDeviceId("newDeviceId");
        assertEquals("newDeviceId", user.getDeviceId());
    }

    @Test
    public void testSetProfileImage() {
        Bitmap newBitmap = Mockito.mock(Bitmap.class);
        user.setProfileImage(newBitmap);
        assertEquals(newBitmap, user.getProfileImage());
    }

    @Test
    public void testRemoveProfileImage() {
        user.removeProfileImage();
        assertNull(user.getProfileImage());
    }

    @Test
    public void testUpdateInfo() {
        user.updateInfo("Updated Name", "updated@example.com", "1122334455");
        assertEquals("Updated Name", user.getName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("1122334455", user.getPhoneNumber());
    }

    @Test
    public void testParcelable() {
        Parcel mockParcel = Mockito.mock(Parcel.class);

        user.writeToParcel(mockParcel, 0);

        Mockito.verify(mockParcel).writeString(user.getEmail());
        Mockito.verify(mockParcel).writeString(user.getName());
        Mockito.verify(mockParcel).writeString(user.getRole());
        Mockito.verify(mockParcel).writeString(user.getPhoneNumber());
        Mockito.verify(mockParcel).writeString(user.getUserId());
        Mockito.verify(mockParcel).writeString(user.getDeviceId());
    }
}
