package com.example.campus;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserHelper {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    /**
     * Constructor
     * Initializes private fields
     */
    public NewUserHelper() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
    }

    /**
     * Save a new User to Database
     */
    public void saveUser(User user) {
        // Add post to users posts
        mRef.child("users").child(user.getUID()).setValue(user);
    }

}
