package com.example.fusion1_events;

import android.provider.ContactsContract;

public class User {
    private String name;
    private ContactsContract.CommonDataKinds.Email email;
    private String phoneNumber;

    public User(String name, ContactsContract.CommonDataKinds.Email email, String phoneNumber){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null && !name.isEmpty())
            this.name = name;
    }

    public ContactsContract.CommonDataKinds.Email getEmail() {
        return email;
    }

    public void setEmail(ContactsContract.CommonDataKinds.Email email) {
        if(email != null)
            this.email = email;
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

    public void optOutOfNotification(Event event){
        event.turnNotificationOff(this);
    }

    public void updateInfo(String name, ContactsContract.CommonDataKinds.Email email, String phoneNumber){
        setName(name);
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

}
