package com.example.campus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    // Private fields
    private Button signin;
    private Button createProfile;

    private EditText emailEditText;
    private EditText passwordEditText;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // SharedPreferences Interaction
        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Firebase DataBase for user information
        mRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Check for saved email and password
        if (!(sharedPreferences.getString("email", "").equals("") || sharedPreferences.getString("password", "").equals(""))) {
            mAuth.signInWithEmailAndPassword(sharedPreferences.getString("email", ""), sharedPreferences.getString("password", ""));
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
        emailEditText = (EditText) findViewById(R.id.email_signin);
        passwordEditText = (EditText) findViewById(R.id.password_signin);
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
                        // Update SharedPreferences
                        sharedPreferences.edit().putString("email", email).apply();
                        sharedPreferences.edit().putString("password", password).apply();
                        if (mAuth.getCurrentUser() != null) {
                             mRef.child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                 @Override
                                 public void onComplete(@NonNull Task<DataSnapshot> task) {
                                     DataSnapshot ds = task.getResult();
                                     User user = new User();
                                     if (ds != null) {
                                         user = ds.getValue(User.class);
                                         // Add location to SharedPreferences
                                         sharedPreferences.edit().putFloat("user_lat", (float)user.getLat()).apply();
                                         sharedPreferences.edit().putFloat("user_lng", (float)user.getLng()).apply();
                                         sharedPreferences.edit().putBoolean("use_cur_loc", false).apply();
                                     } else {
                                         Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
                                     }

                                 }
                            });
                        }

                        // Start main activity
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