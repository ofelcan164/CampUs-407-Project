package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignIn extends AppCompatActivity {

    Button signin;
    Button createProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Configure on screen buttons
        signin = (Button) findViewById(R.id.signin_btn);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInClicked();
            }
        });

        createProfile = (Button) findViewById(R.id.create_profile_btn);
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProfileClicked();
            }
        });
    }

    public void createProfileClicked() {
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }

    public void signInClicked() {
        // TODO NOT REALLY WHAT WILL HAPPEN
        Intent intent = new Intent(this, MainFeedsActivity.class);
        startActivity(intent);
    }
}