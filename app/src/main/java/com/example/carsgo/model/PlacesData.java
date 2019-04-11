package com.example.carsgo.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlacesData {

    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private Uri  websiteUri;
    private LatLng latLng;
    private float rating;

    public PlacesData(String name, String address, String phoneNumber, String id, Uri websiteUri, LatLng latLng, float rating) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.rating = rating;
    }

    public PlacesData(){}

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public float getRating() {
        return rating;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
