package com.example.campus;

public class AlertPost {

    private String title;
    private String content;
    private String UID;

    /**
     * Default no-arg constructor for Firebase deserialization
     */
    public AlertPost() {}

    public AlertPost(String title, String content, String UID) {
        this.title = title;
        this.content = content;
        this.UID = UID;
    }

    // Getters
    public String getTitle() { return this.title; }
    public String getContent() { return this.content; }
    public String getUID() { return UID; }

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
}
