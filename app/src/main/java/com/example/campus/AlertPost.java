package com.example.campus;

public class AlertPost {

    private String title;
    private String content;

    public AlertPost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters
    public String getTitle() { return this.title; }
    public String getContent() { return this.content; }

}
