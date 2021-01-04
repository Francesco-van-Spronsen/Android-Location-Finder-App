package com.android2.ui.people;

import android.net.Uri;

public class Person {
    private String name;
    private String picture;
    private double latitude;
    private double longitude;
    private boolean isLoggedIn;

    public Person() {

    }

    public Person(String name, String picture, double latitude, double longitude, boolean isLoggedIn){
        this.name = name;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isLoggedIn = isLoggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
