package com.example.campus;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class User {
    // Private instance fields
    private String username;
    private String password;
    private String phone;
    private String email;
    private String year;
    private String major;
    private String UID;
    private double lat;
    private double lng;

    /**
     * No arg constructor
     */
    public User() {
    }

    /**
     * Constructor
     */
    public User(String username, String password, String email, String phone, String year, String major, double lat, double lng, String UID) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.year = year;
        this.major = major;
        this.lat = lat;
        this.lng = lng;
        this.UID = UID;
    }

    // Getters
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getYear() {
        return year;
    }

    public String getMajor() {
        return major;
    }

    public String getUID() {
        return UID;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    // Setters
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
