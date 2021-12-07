package com.example.campus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity implements EditUserCredsDialog.EditUserCredsDialogListener {

    private Button cancel;
    private Button save;
    private Button editCreds;
    private Button saveLocBtn;

    private CheckBox curLocationCheck;

    private TextView usernameEditText;
    private TextView emailEditText;
    private EditText majorEditText;
    private EditText phoneEditText;
    private EditText yearEditText;

    private SharedPreferences sharedPreferences;

    private String newPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private NewUserHelper userHelper;

    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        cancel = (Button) findViewById(R.id.edit_profile_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClicked();
            }
        });

        save = (Button) findViewById(R.id.edit_save_profile);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClicked();
            }
        });

        editCreds = (Button) findViewById(R.id.edit_creds_btn);
        editCreds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditCredsDialog();
            }
        });

        saveLocBtn = (Button) findViewById(R.id.edit_save_cur_location);
        saveLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurLocation();
            }
        });

        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);
        newPassword = sharedPreferences.getString("password", "");

        curLocationCheck = (CheckBox) findViewById(R.id.location_check_edit_profile);
        curLocationCheck.setChecked(sharedPreferences.getBoolean("use_cur_loc", false));
        curLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCurLocationChecked();
            }
        });

        // Set up textview information
        usernameEditText = (TextView) findViewById(R.id.editProfileUsername);
        emailEditText = (TextView) findViewById(R.id.email_edit_edit_profile);
        yearEditText = (EditText) findViewById(R.id.year_edit_edit_profile);
        majorEditText = (EditText) findViewById(R.id.major_edit_edit_profile);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_edit_profile);

        // Database interaction
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        userHelper = new NewUserHelper();

        // Set the values
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user.getUID().equals(mAuth.getUid())) {
                        usernameEditText.setText(user.getUsername());
                        emailEditText.setText(user.getEmail());
                        yearEditText.setText(user.getYear());
                        majorEditText.setText(user.getMajor());
                        phoneEditText.setText(user.getPhone());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Location services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
        // Prompt user for location permissions TODO
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            // Request permissions if necessary
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void onCancelClicked() {
        startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
    }

    private void onSaveClicked() {
        if (newPassword == null) {
            Toast.makeText(getApplicationContext(), "Passwords much both be matching and valid [6 characters or more]", Toast.LENGTH_SHORT).show();
            openEditCredsDialog();
            return;
        } else {
            if (!newPassword.equals("") && newPassword != null
            && !newPassword.equals(sharedPreferences.getString("password", ""))) {
                FirebaseUser curUser = mAuth.getCurrentUser();
                curUser.updatePassword(newPassword);

                // Save new password to SharedPreferences
                sharedPreferences.edit().putString("password", newPassword).apply();

                // Return to profile
                Toast.makeText(getApplicationContext(), "Password updated!", Toast.LENGTH_SHORT).show();
            }

            // Save info to database
            String phone = phoneEditText.getText().toString();
            String year = yearEditText.getText().toString();
            String major = majorEditText.getText().toString();
            if (phone == null || phone.equals("")) {
                phoneEditText.setError("Please enter your phone number");
                return;
            }
            if (year == null || year.equals("")) {
                yearEditText.setError("Please enter your year in school");
                return;
            }
            if (major == null || major.equals("")) {
                majorEditText.setError("Please enter your major");
                return;
            }

            userHelper.saveUser(new User(usernameEditText.getText().toString(),
                    sharedPreferences.getString("password", ""),
                    emailEditText.getText().toString(),
                    phone,
                    year,
                    major,
                    sharedPreferences.getFloat("user_lat", 0),
                    sharedPreferences.getFloat("user_lng", 0),
                    mAuth.getCurrentUser().getUid()));

            startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
        }
    }

    /**
     * Update the whether user is using current location
     */
    private void onCurLocationChecked() {
        sharedPreferences.edit().putBoolean("use_cur_loc", curLocationCheck.isChecked()).apply(); // Update the whether user is using current location

        // Update SharedPreferences based on location configuration
        if (sharedPreferences.getBoolean("use_cur_loc", false)) {
            // Update SharedPreferences user_lat and user_lng with current location
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // Put locations
                sharedPreferences.edit().putFloat("user_lat", (float)location.getLatitude()).apply();
                sharedPreferences.edit().putFloat("user_lng", (float)location.getLongitude()).apply();
            }
            // TODO
            else {
                Toast.makeText(this, "LOCATION PERMISSIONS NOT GRANTED????", Toast.LENGTH_LONG).show(); // TODO
            }
        }
        else {
            // Update SharedPreferences user_lat and user_lng with saved location
            if (mAuth.getCurrentUser() != null) {
                mRef.child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        User user = task.getResult().getValue(User.class);

                        // Add location to SharedPreferences
                        sharedPreferences.edit().putFloat("user_lat", (float)user.getLat()).apply();
                        sharedPreferences.edit().putFloat("user_lng", (float)user.getLng()).apply();
                    }
                });
            }
        }

        Toast.makeText(this, "Updated location configuration to "
                + ((curLocationCheck.isChecked()) ? "use current location." : "use saved location."), Toast.LENGTH_SHORT).show();
    }

    /**
     * Save current location to database for user
     */
    private void saveCurLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Save location to the user
            mRef.child(mAuth.getUid()).child("lat").setValue(location.getLatitude());
            mRef.child(mAuth.getUid()).child("lng").setValue(location.getLongitude());
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
     * Open the edit user credentials dialog
     */
    public void openEditCredsDialog() {
        EditUserCredsDialog dialog = new EditUserCredsDialog();
        dialog.show(getSupportFragmentManager(), "edit_creds");
    }

    @Override
    public void saveEditCreds(String password) {
        newPassword = password;
    }
}