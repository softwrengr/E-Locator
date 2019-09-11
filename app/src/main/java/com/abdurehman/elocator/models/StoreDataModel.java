package com.abdurehman.elocator.models;

/**
 * Created by eapple on 18/12/2018.
 */

public class StoreDataModel {

    public String address;
    public String contact;
    public String image;
    public String latitude;
    public String longitude;
    public String title;

//    public StoreDataModel(String address, String contact, String image, String latitude, String longitude, String title) {
//        this.address = address;
//        this.contact = contact;
//        this.image = image;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.title = title;
//    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
