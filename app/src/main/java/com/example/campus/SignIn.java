package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignIn extends AppCompatActivity {

    // Private fields
    private Button signin;
    private Button createProfile;

    private EditText username;
    private EditText password;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // SharedPreferences Interaction
        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        // Check for saved username and password
        if (!(sharedPreferences.getString("username", "").equals("") || sharedPreferences.getString("password", "").equals(""))) {
            Intent intent = new Intent(this, MainFeedsActivity.class);
            startActivity(intent);
        } else {
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
    }

    public void createProfileClicked() {
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }

    public void signInClicked() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        // Check against DB, if valid user then TODO
        // Save username and password to SharedPreferences
        sharedPreferences.edit().putString("username", username.getText().toString()).apply();
        sharedPreferences.edit().putString("password", password.getText().toString()).apply();

        // Start next MainFeedsAcitivty
        Intent intent = new Intent(this, MainFeedsActivity.class);
        startActivity(intent);
    }

}