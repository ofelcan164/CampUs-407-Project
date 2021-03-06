package com.example.campus;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewPostHelper {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    /**
     * Constructor
     * Initializes private fields
     */
    public NewPostHelper() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
    }

    /**
     * Save a new social post to Database
     */
    public String postSocial(SocialPost post) {
        DatabaseReference newRef = mRef.child("posts").child("social").push();// push() creates guaranteed unique ID for post
        String key = newRef.getKey();
        post.setPostID(key);
        newRef.setValue(post);
        return key;
    }

    /**
     * Save a new market post to Database
     */
    public String postMarket(MarketPost post) {
        DatabaseReference newRef = mRef.child("posts").child("market").push();
        String key = newRef.getKey();
        post.setPostID(key);
        newRef.setValue(post); // push() creates guaranteed unique ID for post
        return key;
    }

    /**
     * Save a new alert post to Database
     */
    public void postAlert(AlertPost post) {
        // Add post to users posts
        mRef.child("posts").child("alerts").push().setValue(post); // push() creates guaranteed unique ID for post
    }
}
