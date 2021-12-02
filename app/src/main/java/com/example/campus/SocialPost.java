package com.example.campus;

public class SocialPost {

    private String content;
    private String username;
    private String UID;

    /**
     * Default no arg constructor for Firebase deserialization
     */
    public SocialPost() {}

    public SocialPost(String content, String username, String UID) {
        this.content = content;
        this.username = username;
        this.UID = UID;
    }

    // Getters
    public String getContent() {
        return this.content;
    }
    public String getUID() {
        return this.UID;
    }
    public String getUsername() {
        return username;
    }

    // Setters
    public void setContent(String content) {
        this.content = content;
    }
    public void setUID(String UID) {
        this.UID = UID;
    }
    public void setUsername(String username) {
        this.username = username;
    }


}