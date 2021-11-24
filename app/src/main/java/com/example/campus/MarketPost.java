package com.example.campus;

public class MarketPost {

    private String title;
    private String phoneNum;
    private String description;

    /**
     * Constructor
     */
    public MarketPost(String title, String phoneNum, String description) {
        this.title = title;
        this.phoneNum = phoneNum;
        this.description = description;
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
}
