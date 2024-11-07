package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Map;

public class Entrant extends User implements Parcelable {
    protected Location location;
    private ArrayList<Event> eventList;
    // By default user's notification is on.
    protected boolean notificationEnabled = true;

    public Entrant(String email, String name, String role, String phoneNumber, String userId, String deviceId, Bitmap profileImage, Location location, boolean notificationEnabled) {
        super(email, name, role, phoneNumber, userId, deviceId, profileImage);
        this.location = location;
        this.notificationEnabled = notificationEnabled;
    }

    // no argument constractor
    public Entrant()
    {
        super();
    }


    protected Entrant(Parcel in) {
        super(in);
        location = in.readParcelable(Location.class.getClassLoader());
        notificationEnabled = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Entrant> CREATOR = new Creator<Entrant>() {
        @Override
        public Entrant createFromParcel(Parcel in) {
            return new Entrant(in);
        }

        @Override
        public Entrant[] newArray(int size) {
            return new Entrant[size];
        }
    };

    public void joinWaitlist(Event event){
       // event.getWaitlist().joinEntrent(this);
    }
    public void quitWaitlist(Event event){
        //event.getWaitlist().ommitEntrent(this);
    }
    public void acceptOrganizerInvitation(Event event){
       // event.joinAttendeeList(this);
    }
    public void rejectOrganizerInvitation(Event event){
        //event.addToRejectionList(this);
    }

    public void turnNotificationOn(){
        this.notificationEnabled = true;
    }
    public void turnNotificationOff(){
        this.notificationEnabled = false;
    }

    public static Entrant extractUser(Map<String, Object> userDocument){
        Bitmap convertedImage = null;
        String phone  = (String) userDocument.get("phoneNumber");
        String name = (String) userDocument.get("name");
        String email = (String) userDocument.get("email");
        String role = (String) userDocument.get("role");
        String userID = (String) userDocument.get("userId");
        String deviceID = (String) userDocument.get("deviceId");
        String image = (String) userDocument.get("profileImage");

        if (image == null) {
            // set to default
            convertedImage = BitmapFactory.decodeResource(MainActivity.getAppContext().getResources(), R.drawable.ic_user);
        }
        else
            convertedImage = UtilityMethods.decodeBase64ToBitmap(image);
        Entrant user = new Entrant(email, name, role, phone, userID, deviceID, convertedImage, null, true);

        return user;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getEmail());
        dest.writeString(getName());
        dest.writeString(getRole());
        dest.writeString(getPhoneNumber());
        dest.writeString(getUserId());
        dest.writeString(getDeviceId());
        dest.writeParcelable(location, flags);
        dest.writeByte((byte) (notificationEnabled ? 1 : 0));
    }
}
