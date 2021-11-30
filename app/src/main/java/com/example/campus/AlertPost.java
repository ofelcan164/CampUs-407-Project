package com.example.campus;

public class AlertPost {

    private String title;
    private String content;
    private String UID;
    private String urgencyRating;

    /**
     * Default no-arg constructor for Firebase deserialization
     */
    public AlertPost() {
    }

    public AlertPost(String title, String content, String UID, String urgencyRating) {
        this.title = title;
        this.content = content;
        this.urgencyRating = urgencyRating;
        this.UID = UID;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getUID() {
        return UID;
    }

    public String getUrgencyRating() {
        return this.urgencyRating;
    }

    // Setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setUrgencyRating(String urgencyRating) {
        this.urgencyRating = urgencyRating;
    }
}