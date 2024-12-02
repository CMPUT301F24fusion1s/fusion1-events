package com.example.fusion1_events;

import static com.example.fusion1_events.UtilityMethods.decodeBase64ToBitmap;
import static com.example.fusion1_events.UtilityMethods.encodeBitmapToBase64;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.WriterException;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import kotlin.NotImplementedError;

/**
 * Event class represents an event in the application, with properties such as name, date, location, description, etc.
 * It implements Parcelable to allow easy transfer of data between components.
 */
public class Event implements Parcelable {

    private final UUID id;
    private String organizerId;
    private String name;
    private Date date;
    private String location;
    private String description;
    private Bitmap poster;
    private Bitmap qrCode;
    private String qrCodeHash;
    private Integer capacity;
    private Integer waitlistLimit;
    private Boolean geolocationRequired;
    private WaitList waitlist;

    /**
     * Constructor for creating a new Event instance.
     *
     * @param id                  The UUID of the event.
     * @param organizerId         The UUID of the organizer.
     * @param name                The name of the event.
     * @param date                The date of the event.
     * @param location            The location of the event.
     * @param description         The description of the event.
     * @param poster              The poster image for the event.
     * @param capacity            The capacity of the event.
     * @param geolocationRequired Whether geolocation is required for the event.
     */
    public Event(UUID id, String organizerId, String name, Date date, String location, String description, Bitmap poster, int capacity, int waitlistLimit, Boolean geolocationRequired) {
        this.id = id != null ? id : UUID.randomUUID();
        this.organizerId = organizerId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.poster = poster;
        this.capacity = capacity;
        this.waitlistLimit = waitlistLimit;
        this.geolocationRequired = geolocationRequired;
        this.waitlist = new WaitList();
    }

    /**
     * Constructor for recreating an Event instance from a Parcel.
     *
     * @param in Parcel containing the serialized event data.
     */
    protected Event(Parcel in) {
        String idString = in.readString();
        id = idString != null ? UUID.fromString(idString) : null;

         organizerId = in.readString();
//        organizerId = (organizerIdString != null) ? organizerId : null;

        name = in.readString();

        long dateLong = in.readLong();
        date = (dateLong != -1) ? new Date(dateLong) : null;

        location = in.readString();
        description = in.readString();
        qrCode = in.readParcelable(Bitmap.class.getClassLoader());
        qrCodeHash = in.readString();

        int capacityValue = in.readInt();
        capacity = (capacityValue != -1) ? capacityValue : null;

        int waitlistLimitValue = in.readInt();
        waitlistLimit = (waitlistLimitValue != -1) ? waitlistLimitValue : null;

        geolocationRequired = in.readByte() != 0;
        waitlist = in.readParcelable(WaitList.class.getClassLoader());
    }



    /**
     * Converts the Event object to a map for Firestore storage.
     *
     * @return A map containing the event data. This map can be used to store the event in Firestore.
     */
    public Map<String, Object> toMap() {
        // Convert the poster and QR code Bitmaps to Base64 strings
        String posterBase64 = encodeBitmapToBase64(this.getPoster());
        String qrCodeBase64 = encodeBitmapToBase64(this.getQrCode());

        // Create a map to store event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("organizerId", this.getOrganizerId().toString());
        eventData.put("name", this.getName());
        eventData.put("date", this.getDate());
        eventData.put("location", this.getLocation());
        eventData.put("description", this.getDescription());
        eventData.put("qrCode", qrCodeBase64);
        eventData.put("qrCodeHash", this.getQrCodeHash());
        eventData.put("poster", posterBase64);
        eventData.put("capacity", this.getCapacity());
        eventData.put("waitlistLimit", this.waitlistLimit);
        eventData.put("geolocationRequired", this.geolocationRequired);
        eventData.put("waitingEntrants", this.waitlist.getWaitingEntrants());
        eventData.put("invitedEntrants", this.waitlist.getInvitedEntrants());
        eventData.put("cancelledEntrants", this.waitlist.getCancelledEntrants());
        eventData.put("enrolledEntrants", this.waitlist.getEnrolledEntrants());

        return eventData;
    }

    /**
     * Creates an Event object from a Firestore document.
     *
     * @param document The Firestore document containing event data.
     * @return The Event object created from the document.
     */
    public static Event fromFirestoreDocument(DocumentSnapshot document) {
        // Extract data from document
        UUID id = UUID.fromString(document.getString("qrCodeHash"));
        String organizerId = document.getString("organizerId");
        String name = document.getString("name");
        Date date = document.getDate("date");
        String location = document.getString("location");
        String description = document.getString("description");
        String posterBase64 = document.getString("poster");
        int capacity = document.getLong("capacity") != null ? Objects.requireNonNull(document.getLong("capacity")).intValue() : 0;
        int waitlistLimit = document.getLong("waitlistLimit") != null ? Objects.requireNonNull(document.getLong("waitlistLimit")).intValue() : 0;
        Boolean geolocationRequired = document.getBoolean("geolocationRequired");

        // Extract waitlist data
        List<String> waitingEntrants = document.get("waitingEntrants") != null ? (List<String>) document.get("waitingEntrants") : new ArrayList<>();
        List<String> invitedEntrants = document.get("invitedEntrants") != null ? (List<String>) document.get("invitedEntrants") : new ArrayList<>();
        List<String> cancelledEntrants = document.get("cancelledEntrants") != null ? (List<String>) document.get("cancelledEntrants") : new ArrayList<>();
        List<String> enrolledEntrants = document.get("enrolledEntrants") != null ? (List<String>) document.get("enrolledEntrants") : new ArrayList<>();

        // Convert Base64 string back to Bitmap
        Bitmap poster = null;
        if (posterBase64 != null) {
            poster = decodeBase64ToBitmap(posterBase64);
        }

        // Create Event object
        Event event = new Event(id, organizerId, name, date, location, description, poster, capacity, waitlistLimit, geolocationRequired);
        event.intializeWaitlist(waitingEntrants, invitedEntrants, cancelledEntrants, enrolledEntrants);

        return event;
    }

    /**
     * Generates a QR code for the event.
     */
    protected void generateQRCode() {
        try {
            QRCode.QRCodeResult qrCodeResult = QRCode.generateQRCode(this.getId());
            this.setQrCode(qrCodeResult.getBitmap());
            this.setQrCodeHash(qrCodeResult.getHash());
        } catch (WriterException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating QR code for event.");
        }
    }

    // Getters and setters for event properties
    public UUID getId() {
        return id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getQrCode() {
        if (this.qrCode == null)
            generateQRCode();

        return qrCode;
    }

    public void setQrCode(Bitmap qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCodeHash() {
        return qrCodeHash;
    }

    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Integer getWaitlistLimit() {
        return waitlistLimit;
    }

    public void setWaitlistLimit(Integer waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
    }

    public Boolean getGeolocationRequired() {
        return geolocationRequired;
    }

    public void setGeolocationRequired(Boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }

    public WaitList getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(WaitList waitlist) {
        this.waitlist = waitlist;
    }

    /**
     * Runs a lottery for the event waitlist.
     */
    public void runLottery() {
        int availableCapacity = this.capacity - this.waitlist.getEnrolledEntrants().size();
        List<String> waitingEntrants = new ArrayList<>(this.waitlist.getWaitingEntrants());

        // Shuffle the waiting entrants list
        Collections.shuffle(waitingEntrants);

        if (availableCapacity > waitingEntrants.size()) {
            waitlist.inviteWaitingEntrants(waitingEntrants);
            return;
        }

        List<String> selectedEntrants = waitingEntrants.subList(0, availableCapacity);
        waitlist.inviteWaitingEntrants(selectedEntrants);
    }

    /**
     * Re-runs the lottery for the event.
     */
    public void reRunLottery() {
        this.runLottery();
    }

    /**
     * Sends a notification with the specified message.
     *
     * @param message The message to be sent.
     * @throws NotImplementedError as the logic is not yet implemented.
     */
    public void sendNotification(String message) {
        // TODO: Implement notification logic
        throw new NotImplementedError("Method not implemented.");
    }

    /**
     * Describe the contents for Parcelable interface.
     *
     * @return An integer value representing the contents.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.id != null ? this.id.toString() : null);
        parcel.writeString(this.organizerId != null ? this.organizerId : null);
        parcel.writeString(this.name);
        parcel.writeLong(this.date != null ? this.date.getTime() : -1);
        parcel.writeString(this.location);
        parcel.writeString(this.description);
        parcel.writeParcelable(this.qrCode, i);
        parcel.writeString(this.qrCodeHash);
        parcel.writeInt(this.capacity != null ? this.capacity : -1);
        parcel.writeInt(this.waitlistLimit != null ? this.waitlistLimit : -1);
        parcel.writeByte((byte) (this.geolocationRequired != null && this.geolocationRequired ? 1 : 0));
        parcel.writeParcelable(this.waitlist != null ? this.waitlist : new WaitList(),i);
    }


    /**
     * Writes the Event data to a Parcel.
     *
     * @param parcel The Parcel in which the data should be written.
     * @param i      Flags to modify how the object should be written.
     */
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    private void intializeWaitlist(List<String> waitingEntrants, List<String> invitedEntrants, List<String> cancelledEntrants, List<String> enrolledEntrants) {
        this.waitlist = new WaitList(waitingEntrants, invitedEntrants, cancelledEntrants, enrolledEntrants);
    }

    protected static class WaitList implements Parcelable {
        private final List<String> waitingEntrants;
        private final List<String> invitedEntrants;
        private final List<String> cancelledEntrants;
        private final List<String> enrolledEntrants;

        public WaitList() {
            this.waitingEntrants = new ArrayList<>();
            this.invitedEntrants = new ArrayList<>();
            this.cancelledEntrants = new ArrayList<>();
            this.enrolledEntrants = new ArrayList<>();
        }

        public WaitList(List<String> waitingEntrants, List<String> invitedEntrants, List<String> cancelledEntrants, List<String> enrolledEntrants) {
            this.waitingEntrants = waitingEntrants;
            this.invitedEntrants = invitedEntrants;
            this.cancelledEntrants = cancelledEntrants;
            this.enrolledEntrants = enrolledEntrants;
        }

        protected WaitList(Parcel in) {
            waitingEntrants = in.createStringArrayList();
            invitedEntrants = in.createStringArrayList();
            cancelledEntrants = in.createStringArrayList();
            enrolledEntrants = in.createStringArrayList();
        }

        public static final Creator<WaitList> CREATOR = new Creator<WaitList>() {
            @Override
            public WaitList createFromParcel(Parcel in) {
                return new WaitList(in);
            }

            @Override
            public WaitList[] newArray(int size) {
                return new WaitList[size];
            }
        };

        public List<String> getWaitingEntrants() {
            return waitingEntrants;
        }

        public List<String> getInvitedEntrants() {
            return invitedEntrants;
        }

        public List<String> getCancelledEntrants() {
            return cancelledEntrants;
        }

        public List<String> getEnrolledEntrants() {
            return enrolledEntrants;
        }

        public void addWaitingEntrant(String userId) {
            if (userId == null) return;
            if (!getAllEntrants().contains(userId)) {
                waitingEntrants.add(userId);
            }
        }

        public void removeWaitingEntrant(String userId) {
            if (userId == null) return;
            waitingEntrants.remove(userId);
        }

        public void inviteWaitingEntrant(String userId) {
            if (userId == null) return;
            if (waitingEntrants.contains(userId)) {
                waitingEntrants.remove(userId);
                invitedEntrants.add(userId);
            }
        }

        public void inviteWaitingEntrants(List<String> userIds) {
            if (userIds == null) return;
            for (String userId : userIds) {
                inviteWaitingEntrant(userId);
            }
        }

        public void cancelInvitedEntrant(String userId) {
            if (userId == null) return;
            if (invitedEntrants.contains(userId)) {
                invitedEntrants.remove(userId);
                cancelledEntrants.add(userId);
            }
        }

        public void enrollInvitedEntrant(String userId) {
            if (userId == null) return;
            if (invitedEntrants.contains(userId)) {
                invitedEntrants.remove(userId);
                enrolledEntrants.add(userId);
            }
        }

        public void cancelEnrolledEntrant(String userId) {
            if (userId == null) return;
            if (enrolledEntrants.contains(userId)) {
                enrolledEntrants.remove(userId);
                cancelledEntrants.add(userId);
            }
        }

        public List<String> getAllEntrants() {
            List<String> allEntrants = new ArrayList<>();
            allEntrants.addAll(waitingEntrants);
            allEntrants.addAll(invitedEntrants);
            allEntrants.addAll(cancelledEntrants);
            allEntrants.addAll(enrolledEntrants);
            return allEntrants;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeStringList(waitingEntrants);
            parcel.writeStringList(invitedEntrants);
            parcel.writeStringList(cancelledEntrants);
            parcel.writeStringList(enrolledEntrants);
        }
    }
}


/**
 * Summary:
 * - The Event class represents event data including name, date, location, description, and more.
 * - It implements Parcelable to facilitate passing Event objects between Android components.
 * - It provides methods to convert to and from Firestore documents and generate QR codes.
 * - Not fully implemented features include lottery logic and sending notifications.
 */
