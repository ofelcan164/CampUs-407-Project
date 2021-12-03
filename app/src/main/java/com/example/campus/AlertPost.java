package com.example.campus;

public class AlertPost {

    private String title;
    private String content;
    private String UID;
    private String urgencyRating;
    private String username;

    /**
     * Default no-arg constructor for Firebase deserialization
     */
    public AlertPost() {
    }

    public AlertPost(String title, String urgencyRating, String content, String username, String UID) {
        this.title = title;
        this.content = content;
        this.urgencyRating = urgencyRating;
        this.UID = UID;
        this.username = username;
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

    public String getUsername() {
        return username;
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

    public void setUsername(String username) {
        this.username = username;
    }
}