package com.example.campus;

public class User {
    // Private instance fields
    private String username;
    private String password;
    private double savedLocationLat;
    private double savedLocationLng;
    private boolean useCurLocation;

    /**
     * Constructor
     */
    public User(String username, String password, double savedLocationLat, double savedLocationLng, boolean useCurLocation) {
        this.username = username;
        this.password = password;
        this.savedLocationLat = savedLocationLat;
        this.savedLocationLng = savedLocationLng;
        this.useCurLocation = useCurLocation;
    }

    /**
     * Initialize a user (after creation) with default values for savedLocationLat, savedLocationLng, useCurLocation
     */
    public static User initUser(String username, String password) {
        return new User(username, password, 100, 200, true); // TODO DEFAULT TO TRUE FOR LOC?
    }

    // Getters
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public double getSavedLocationLat() { return this.savedLocationLat; }
    public double getSaveLocationLng() { return this.savedLocationLng; }
    public boolean getUseCurLocation() { return this.useCurLocation; }

    // Setters
    public void setUsername(String newUsername) { this.username = newUsername; }
    public void setPassword(String newPassword) { this.password = newPassword; }
    public void setSavedLocationLat(double newLat) { this.savedLocationLat = newLat; }
    public void setSaveLocationLng(double newLng) { this.savedLocationLng = newLng; }
    public void setUseCurLocation(boolean newCurLocation) { this.useCurLocation = newCurLocation; }

}
