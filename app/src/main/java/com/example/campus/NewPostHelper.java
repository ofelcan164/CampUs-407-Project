package com.example.campus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewPostHelper {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    /**
     * Construtor
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
    public void postSocial(SocialPost post) {
        // Acquire user credentials
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();

        // Add post to users posts
        mRef.child("posts").child("social").child(userID).push().setValue(post); // push() creates guaranteed unique ID for post
    }

    /**
     * Save a new market post to Database
     */
    public void postMarket(MarketPost post) {
        // Acquire user credentials
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();

        // Add post to users posts
        mRef.child("posts").child("market").child(userID).push().setValue(post); // push() creates guaranteed unique ID for post
    }

    /**
     * Save a new alert post to Database
     */
    public void postAlert(AlertPost post) {
        // Acquire user credentials
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();

        // Add post to users posts
        mRef.child("posts").child("alerts").child(userID).push().setValue(post); // push() creates guaranteed unique ID for post
    }
}
