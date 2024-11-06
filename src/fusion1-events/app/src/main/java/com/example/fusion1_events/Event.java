package com.example.fusion1_events;

import static com.example.fusion1_events.UtilityMethods.convertUuidListToStringList;
import static com.example.fusion1_events.UtilityMethods.encodeBitmapToBase64;

import android.graphics.Bitmap;

import com.google.zxing.WriterException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kotlin.NotImplementedError;

public class Event {

    private final UUID id;
    private UUID organizerId;
    private String name;
    private Date date;
    private String location;
    private String description;
    private Bitmap qrCode;
    private String qrCodeHash;
    private Bitmap poster;
    private List<UUID> waitlist;
    private int capacity;

    public Event(UUID organizerId, String name, Date date, String location, String description, Bitmap poster, int capacity) {
        this.id = UUID.randomUUID();
        this.organizerId = organizerId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.poster = poster;
        this.capacity = capacity;
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

        // Convert the waitlist to a list of strings
        List<String> waitlistStrings = convertUuidListToStringList(this.getWaitlist());

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
        eventData.put("waitlist", waitlistStrings);

        return eventData;
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

    public List<UUID> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(List<UUID> waitlist) {
        this.waitlist = waitlist;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
}
