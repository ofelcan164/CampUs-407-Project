package com.example.campus;

public class MarketPost {

    private String title;
    private String phoneNum;
    private String description;
    private String UID;

    /**
     * Default no-arg constructor for Firebase deserialization
     */
    public MarketPost() {}

    /**
     * Constructor
     */
    public MarketPost(String title, String phoneNum, String description, String UID) {
        this.title = title;
        this.phoneNum = phoneNum;
        this.description = description;
        this.UID = UID;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUID() { return this.UID; }

    // Setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
