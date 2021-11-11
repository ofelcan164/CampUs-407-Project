package com.example.campus;

public class SocialFeedDB {

    private String username;
    private String postContent;
    private String dateTime;
    // Need a location posted from? TODO

    public SocialFeedDB(String username, String postContent, String dateTime) {
        this.username = username;
        this.postContent = postContent;
        this.dateTime = dateTime;
    }

    public String getUsername() {return username;}
    public String getPostContent() {return postContent;}
    public String getDateTime() {return dateTime;}
}
