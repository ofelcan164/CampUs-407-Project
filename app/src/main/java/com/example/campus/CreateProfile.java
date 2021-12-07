package com.example.campus;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateProfile extends AppCompatActivity implements CreateUserCredsDialog.CreateUserCredsDialogListener {

    private Button cancelBtn;
    private Button createBtn;
    private Button createCredsBtn;

    private String passwordFromDialog1;
    private String passwordFromDialog2;
    private String emailFromDialog;
    private EditText username;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Set up cancel button
        cancelBtn = (Button) findViewById(R.id.create_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCancelClicked();
            }
        });

        // Firebase Auth object
        mAuth = FirebaseAuth.getInstance();

        // Set up button for creating user credentials
        createCredsBtn = (Button) findViewById(R.id.create_creds_btn);
        createCredsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open dialog
                openCredsDialog();
            }
        });

        username = (EditText) findViewById(R.id.username_edit_create);
        createBtn = (Button) findViewById(R.id.create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username == null || username.getText().toString().equals("")) {
                    username.setError("Please enter a valid username");
                } else {
                    Register();
                }
            }
        });

        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        // Set up location services
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
    }

    /**
     * Cancel button clicked
     */
    public void createCancelClicked() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    /**
     * User information is valid, register user with Firebase Authentication
     */
    private void Register() {
        // Prompt user for location permissions
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            // Request permissions if necessary
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // Validate user-inputted credentials
                if (emailFromDialog == null) {
                    Toast.makeText(getApplicationContext(), "Must enter a wisc.edu email", Toast.LENGTH_SHORT).show();
                    openCredsDialog();
                    return;
                } else if (passwordFromDialog1 == null || passwordFromDialog2 == null) {
                    Toast.makeText(getApplicationContext(), "Must enter and confirm a valid password [6 characters or more]", Toast.LENGTH_SHORT).show();
                    openCredsDialog();
                    return;
                }

                // Get user info
                String username = ((EditText) findViewById(R.id.username_edit_create)).getText().toString();
                String year = ((EditText) findViewById(R.id.year_edit_create)).getText().toString();
                String major = ((EditText) findViewById(R.id.major_edit_create)).getText().toString();
                String phone = ((EditText) findViewById(R.id.phone_edit_create)).getText().toString();

                NewUserHelper userHelper = new NewUserHelper();

                // Valid user, register with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(emailFromDialog, passwordFromDialog1)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateProfile.this, "Successfully registered your profile", Toast.LENGTH_LONG).show();

                                    sharedPreferences.edit().putString("email", emailFromDialog).apply();
                                    sharedPreferences.edit().putString("password", passwordFromDialog1).apply();
                                    sharedPreferences.edit().putFloat("user_lat", (float)location.getLatitude()).apply();
                                    sharedPreferences.edit().putFloat("user_lng", (float)location.getLongitude()).apply();
                                    sharedPreferences.edit().putBoolean("use_cur_loc", false).apply();

                                    // Save username as DisplayName
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build();
                                    FirebaseUser curUser = mAuth.getCurrentUser();
                                    curUser.updateProfile(profileUpdates);

                                    // Save all user info
                                    userHelper.saveUser(new User(username,
                                            passwordFromDialog1,
                                            emailFromDialog,
                                            phone,
                                            year,
                                            major,
                                            location.getLatitude(),
                                            location.getLongitude(),
                                            curUser.getUid()));

                                    startActivity(new Intent(CreateProfile.this, MainFeedsActivity.class));
                                } else {
                                    Toast.makeText(CreateProfile.this, "Registration failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
    }

    /**
     * Opens dialog for entering user credentials
     */
    public void openCredsDialog() {
        CreateUserCredsDialog dialog = new CreateUserCredsDialog();
        dialog.show(getSupportFragmentManager(), "create_creds");
    }

    /**
     * Assign the LocationManager (when permission is granted)
     */
    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /**
     * When permission for location services is requested
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
        else {
            Toast.makeText(getApplicationContext(), "The CampUs app requires location permissions.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Interface callback to get user credentials from dialog
     * @param email
     * @param password1
     * @param password2
     */
    @Override
    public void saveCreds(String email, String password1, String password2) {
        emailFromDialog = email;
        passwordFromDialog1 = password1;
        passwordFromDialog2 = password2;
    }
}
