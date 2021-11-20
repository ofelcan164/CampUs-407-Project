package com.example.campus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    // Private fields
    private Button signin;
    private Button createProfile;

    private EditText emailEditText;
    private EditText passwordEditText;

    private FirebaseAuth mAuth;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // SharedPreferences Interaction
        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        // Fireabase auth
        mAuth = FirebaseAuth.getInstance();

        // Check for saved email and password
        if (!(sharedPreferences.getString("email", "").equals("") || sharedPreferences.getString("password", "").equals(""))) {
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
        emailEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email == null || email.equals("")) {
            emailEditText.setError("Please enter an email");
        }
        else if (password == null || password.equals("")) {
            passwordEditText.setError("Please enter a password");
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sharedPreferences.edit().putString("email", email).apply();
                        sharedPreferences.edit().putString("password", password).apply();
                        startActivity(new Intent(SignIn.this, MainFeedsActivity.class));
                    } else {
                        emailEditText.setError("Incorrect email or password");
                        passwordEditText.setError("Incorrect email or password");
                    }
                }
            });
        }
    }

}