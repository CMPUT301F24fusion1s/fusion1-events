package com.example.fusion1_events;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import kotlin.NotImplementedError;

public class Event {

    private final UUID id;
    private String name;
    private Date date;
    private String location;
    private String description;
    private Bitmap qrCode;
    private String qrCodeHash;
    private Bitmap poster;
    private List<UUID> waitlist;
    private int capacity;

    public Event(String name, Date date, String location, String description, Bitmap poster, int capacity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.poster = poster;
        this.capacity = capacity;
    }

    public UUID getId() {
        return id;
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
