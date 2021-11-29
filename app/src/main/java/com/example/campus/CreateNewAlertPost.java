package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class CreateNewAlertPost extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_alert_post);

        // Post helper instance
        NewPostHelper postHelper = new NewPostHelper();

        // Auth
        mAuth = FirebaseAuth.getInstance();

        Button postBtn = (Button) findViewById(R.id.newAlertPostBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get post info
                EditText alertPostTitle = (EditText) findViewById(R.id.newAlertTitle);
                EditText alertPostContent = (EditText) findViewById(R.id.newAlertContent);
                if (alertPostTitle.getText().toString() != null && !alertPostTitle.getText().toString().equals("")
                        && alertPostContent.getText().toString() != null && !alertPostContent.getText().toString().equals("")) {
                    AlertPost post = new AlertPost(alertPostTitle.getText().toString(), alertPostContent.getText().toString(), mAuth.getUid());

                    // Post the post!
                    postHelper.postAlert(post);
                    alertPostTitle.setError(null);
                    alertPostContent.setError(null);
                    Intent intent = new Intent(CreateNewAlertPost.this, MainFeedsActivity.class);
                    intent.putExtra("select", "alert");
                    startActivity(intent);
                } else {
                    alertPostTitle.setError("Please enter you post's title");
                    alertPostContent.setError("Please enter you post's content");
                }
            }
        });
    }
}