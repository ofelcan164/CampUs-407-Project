package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class CreateNewSocialPost extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_social_post);

        // Post helper instance
        NewPostHelper postHelper = new NewPostHelper();

        // Auth
        mAuth = FirebaseAuth.getInstance();

        Button postBtn = (Button) findViewById(R.id.newSocialPostBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get post info
                EditText socialPostContent = (EditText) findViewById(R.id.newSocialPostContent);
                if (socialPostContent.getText().toString() != null && !socialPostContent.getText().toString().equals("")) {
                    SocialPost post = new SocialPost(socialPostContent.getText().toString(), mAuth.getCurrentUser().getDisplayName());

                    // Post the post!
                    postHelper.postSocial(post);
                    socialPostContent.setError(null);
                    Intent intent = new Intent(CreateNewSocialPost.this, MainFeedsActivity.class);
                    intent.putExtra("select", "social");
                    startActivity(intent);
                }
                else {
                    socialPostContent.setError("Please enter you post's content");
                }
            }
        });
    }
}