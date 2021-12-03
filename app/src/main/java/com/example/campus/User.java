package com.example.campus;

public class User {
    // Private instance fields
    private String username;
    private String password;
    private String phone;
    private String email;
    private String year;
    private String major;
    private String UID;

    //    private double savedLocationLat;
    //    private double savedLocationLng;
    //    private boolean useCurLocation;

    /**
     * No arg constructor
     */
    public User() {

    }

    /**
     * Constructor
     */
    public User(String username, String password, String email, String phone, String year, String major, String UID) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.year = year;
        this.major = major;
        this.UID = UID;
//        this.savedLocationLat = savedLocationLat;
//        this.savedLocationLng = savedLocationLng;
//        this.useCurLocation = useCurLocation;
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

    public void setUID(String UID) {
        this.UID = UID;
    }
}
