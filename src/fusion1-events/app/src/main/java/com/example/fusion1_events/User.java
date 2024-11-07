package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

/**
 *  Each user can user one device because we are
 */
// TODO maybe change the email type so user does not input any value
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;

/**
 * Represents a User in the application, with details such as email, name, role, phone number, and profile image.
 * Implements Parcelable for easy data transfer between Android components.
 */
public class User implements Parcelable {
    private String email;
    private String name;
    private String role;
    private String phoneNumber;
    private String userId;
    private String deviceId;
    protected Bitmap profileImage;

    /**
     * Parameterized constructor to initialize a User object with specified details.
     *
     * @param email         User's email address.
     * @param name          User's name (non-null).
     * @param role          User's role (e.g., "Admin", "User").
     * @param phoneNumber   User's phone number.
     * @param userId        Unique identifier for the user.
     * @param deviceId      Unique device identifier associated with the user.
     * @param profileImage  Profile image of the user as a Bitmap.
     */
    public User(String email, @NonNull String name, String role, String phoneNumber, String userId, String deviceId, Bitmap profileImage) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.deviceId = deviceId;
        this.profileImage = profileImage;
    }

    /**
     * No-argument constructor required for serialization purposes.
     */
    public User()
    {
    }

    /**
     * Constructor to initialize a User object from a Parcel.
     *
     * @param in Parcel containing the User data.
     */
    public User(Parcel in) {
        email = in.readString();
        name = in.readString();
        role = in.readString();
        phoneNumber = in.readString();
        userId = in.readString();
        deviceId = in.readString();
    }


    /**
     * Parcelable Creator to create User instances from a Parcel.
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getter and setter methods for user properties
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and setters for userId and deviceId
    public String getUserId() {
        return userId; // Convert String back to UUID
    }

    public void setUserId(String userId) {
        this.userId = userId; // Convert UUID to String for storage
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getName() {
        return name;
    }

    /**
     * Sets the user's name if it is non-null and non-empty.
     *
     * @param name User's name.
     */
    public void setName(String name) {
        if(name != null && !name.isEmpty())
            this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number after trimming leading and trailing whitespace.
     *
     * @param phoneNumber User's phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber != null && !phoneNumber.isEmpty()) {
            phoneNumber = phoneNumber.trim();
            this.phoneNumber = phoneNumber;
        }
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    /**
     * Updates the user's information with new name, email, and phone number.
     *
     * @param name        Updated name.
     * @param email       Updated email.
     * @param phoneNumber Updated phone number.
     */
    public void updateInfo(String name, String email, String phoneNumber){
        setName(name);
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

    /**
     * Describes the contents of the User object, used for Parcelable interface.
     *
     * @return Integer contents description.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes User properties to a Parcel for Parcelable interface.
     *
     * @param parcel Parcel to write data to.
     * @param i      Flags for writing.
     */
    @Override
    public void writeToParcel(@androidx.annotation.NonNull Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(role);
        parcel.writeString(phoneNumber);
        parcel.writeString(userId);
        parcel.writeString(deviceId);
    }

    /**
     * Removes the profile image by setting it to null.
     */
    public void removeProfileImage(){
        this.profileImage = null;
    }
}
