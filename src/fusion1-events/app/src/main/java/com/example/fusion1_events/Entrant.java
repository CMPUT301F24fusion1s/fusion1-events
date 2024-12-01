package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Map;

/**
 * The Entrant class represents a user with the role of an event participant.
 * It extends the User class and implements Parcelable to allow easy transfer of objects between Android components.
 * This class provides additional attributes and methods for managing an entrant's participation in events.
 */
public class Entrant extends User implements Parcelable {
    protected Location location;
    private ArrayList<Event> eventList;
    // By default user's notification is on.
    protected Boolean notificationEnabled = true;

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


    /**
     * Constructor to create an Entrant object from a Parcel.
     *
     * @param in The Parcel containing the serialized Entrant data.
     */
    protected Entrant(Parcel in) {
        super(in);
        location = in.readParcelable(Location.class.getClassLoader());
        notificationEnabled = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Creator field to generate instances of Entrant from a Parcel.
     */
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

    /**
     * Method to join the waitlist of an event.
     *
     * @param event The event the entrant wishes to join.
     */
    public void joinWaitlist(Event event){
       // event.getWaitlist().joinEntrent(this);
    }
    /**
     * Method to quit the waitlist of an event.
     *
     * @param event The event the entrant wishes to quit.
     */
    public void quitWaitlist(Event event){
        //event.getWaitlist().ommitEntrent(this);
    }

    /**
     * Method to accept an invitation from an event organizer.
     *
     * @param event The event the entrant wishes to join as an attendee.
     */
    public void acceptOrganizerInvitation(Event event){
       // event.joinAttendeeList(this);
    }

    /**
     * Method to reject an invitation from an event organizer.
     *
     * @param event The event the entrant wishes to reject.
     */
    public void rejectOrganizerInvitation(Event event){
        //event.addToRejectionList(this);
    }

    /**
     * Gets the notification status for the entrant.
     *
     * @return True if notifications are enabled, false otherwise.
     */
    public Boolean getNotificationEnabled() {
        return notificationEnabled;
    }


    /**
     * Turns on notifications for the entrant.
     */
    public void turnNotificationOn(){
        this.notificationEnabled = true;
    }

    /**
     * Turns off notifications for the entrant.
     */
    public void turnNotificationOff(){
        this.notificationEnabled = false;
    }

    /**
     * Extracts an Entrant object from a Firestore document map.
     *
     * @param userDocument The map containing user details from Firestore.
     * @return An Entrant object created from the provided document data.
     */
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

    /**
     * Writes the Entrant object's data to a Parcel for serialization.
     *
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
