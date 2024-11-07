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

public class User implements Parcelable {
    private String email;
    private String name;
    private String role;
    private String phoneNumber;
    private String userId;
    private String deviceId;
    protected Bitmap profileImage;

    public User(String email, @NonNull String name, String role, String phoneNumber, String userId, String deviceId, Bitmap profileImage) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.deviceId = deviceId;
        this.profileImage = profileImage;
    }

    // no argument constructor for serialization
    public User()
    {
    }

    public User(Parcel in) {
        email = in.readString();
        name = in.readString();
        role = in.readString();
        phoneNumber = in.readString();
        userId = in.readString();
        deviceId = in.readString();
    }


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

    public void setName(String name) {
        if(name != null && !name.isEmpty())
            this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

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

    public void updateInfo(String name, String email, String phoneNumber){
        setName(name);
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

    /**
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @param parcel
     * @param i
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

    public void removeProfileImage(){
        this.profileImage = null;
    }
}
