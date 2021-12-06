package com.example.campus;


public class SocialPost {

    private String content;
    private String username;
    private String postID;

    /**
     * Default no arg constructor for Firebase deserialization
     */
    public SocialPost() {}

    public SocialPost(String content, String username, String postID) {
        this.content = content;
        this.username = username;
        this.postID = postID;
    }

    // Getters
    public String getContent() {
        return this.content;
    }
    public String getUsername() {
        return username;
    }
    public String getPostID() {return this.postID;}

    // Setters
    public void setContent(String content) {
        this.content = content;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPostID(String postID) {this.postID = postID;}


}