package com.noproblem.smarthospitaladmin.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Victor Artemyev on 25/11/2016.
 */

@IgnoreExtraProperties
public class Location {

    private String address;
    private double longitude;
    private double latitude;

    public Location() {
    }

    public Location(String address, double longitude, double latitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override public String toString() {
        return "Location{" +
                "address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
