package com.example.fusion1_events;

import static com.example.fusion1_events.UtilityMethods.decodeBase64ToBitmap;
import static com.example.fusion1_events.UtilityMethods.encodeBitmapToBase64;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.WriterException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import kotlin.NotImplementedError;

public class Event implements Parcelable {

    private final UUID id;
    private UUID organizerId;
    private String name;
    private Date date;
    private String location;
    private String description;
    private Bitmap poster;
    private Bitmap qrCode;
    private String qrCodeHash;
    private Integer capacity;
    private Boolean geolocationRequired;
    private List<String> waitlist;

    public Event(UUID organizerId, String name, Date date, String location, String description, Bitmap poster, int capacity, Boolean geolocationRequired) {
        this.id = UUID.randomUUID();
        this.organizerId = organizerId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.poster = poster;
        this.capacity = capacity;
        this.geolocationRequired = geolocationRequired;
    }

    protected Event(Parcel in) {
        id = UUID.fromString(in.readString());
        organizerId = UUID.fromString(in.readString());
        name = in.readString();
        date = new Date(in.readLong());
        location = in.readString();
        description = in.readString();
        qrCode = in.readParcelable(Bitmap.class.getClassLoader());
        qrCodeHash = in.readString();
        poster = in.readParcelable(Bitmap.class.getClassLoader());
        capacity = in.readInt();
        geolocationRequired = in.readByte() != 0;
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
        eventData.put("geolocationRequired", this.geolocationRequired);
        eventData.put("waitlist", this.getWaitlist());

        return eventData;
    }

    public static Event fromFirestoreDocument(DocumentSnapshot document) {
        // Extract data from document
        UUID organizerId = UUID.fromString(document.getString("organizerId"));
        String name = document.getString("name");
        Date date = document.getDate("date");
        String location = document.getString("location");
        String description = document.getString("description");
        String posterBase64 = document.getString("poster");
        int capacity = document.getLong("capacity") != null ? Objects.requireNonNull(document.getLong("capacity")).intValue() : 0;
        Boolean geolocationRequired = document.getBoolean("geolocationRequired");
        List<String> waitlistStrings = (List<String>) document.get("waitlist");

        // Convert Base64 string back to Bitmap
        Bitmap poster = null;
        if (posterBase64 != null) {
            poster = decodeBase64ToBitmap(posterBase64);
        }

        // Create Event object
        Event event = new Event(organizerId, name, date, location, description, poster, capacity, geolocationRequired);
        event.setWaitlist(waitlistStrings);

        return event;
    }

    private void generateQRCode() {
        try {
            QRCode.QRCodeResult qrCodeResult = QRCode.generateQRCode(this.getId());
            this.setQrCode(qrCodeResult.getBitmap());
            this.setQrCodeHash(qrCodeResult.getHash());
        } catch (WriterException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating QR code for event.");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(UUID organizerId) {
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

    public Boolean getGeolocationRequired() {
        return geolocationRequired;
    }

    public void setGeolocationRequired(Boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }

    public List<String> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(List<String> waitlist) {
        this.waitlist = waitlist;
    }

    public List<User> runLottery() {
        // TODO: Implement lottery logic
        throw new NotImplementedError("Method not implemented.");
    }

    public void reRunLottery() {
        this.runLottery();
    }

    public void sendNotification(String message) {
        // TODO: Implement notification logic
        throw new NotImplementedError("Method not implemented.");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.id.toString());
        parcel.writeString(this.organizerId.toString());
        parcel.writeString(this.name);
        parcel.writeLong(this.date.getTime());
        parcel.writeString(this.location);
        parcel.writeString(this.description);
        parcel.writeParcelable(this.qrCode, i);
        parcel.writeString(this.qrCodeHash);
        parcel.writeParcelable(this.poster, i);
        parcel.writeInt(this.capacity);
        parcel.writeByte((byte) (this.geolocationRequired ? 1 : 0));
        parcel.writeStringList(this.waitlist);
    }

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
}
