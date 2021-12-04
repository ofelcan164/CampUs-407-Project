package com.example.campus;

import android.widget.ImageView;

public class SocialPost {

    private String content;
    private String username;
    private String UID;
    private ImageView photo;

    /**
     * Default no arg constructor for Firebase deserialization
     */
    public SocialPost() {}

    public SocialPost(String content, String username, String UID, ImageView photo) {
        this.content = content;
        this.username = username;
        this.UID = UID;
        this.photo = photo;
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
    public ImageView getPhoto() {return this.photo;}

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