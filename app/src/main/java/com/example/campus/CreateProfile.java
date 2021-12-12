package com.example.campus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CreateProfile extends AppCompatActivity implements CreateUserCredsDialog.CreateUserCredsDialogListener {

    private Button cancelBtn;
    private Button createBtn;
    private Button createCredsBtn;
    private Button addPhotoBtn;
    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView addProfilePictureImageView;

    private String passwordFromDialog1;
    private String passwordFromDialog2;
    private String emailFromDialog;
    private EditText username;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        addProfilePictureImageView = findViewById(R.id.addProfilePictureImageView);
        addPhotoBtn = (Button) findViewById(R.id.add_profile_picture);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
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

        try {
            addProfilePictureImageView.setDrawingCacheEnabled(true);
            addProfilePictureImageView.buildDrawingCache();
            Bitmap bitmap = addProfilePictureImageView.getDrawingCache();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] socialPhotoByteStream = baos.toByteArray();

            String baseFolder = "profilePictures/";
            String imageFilePath = baseFolder + mAuth.getUid();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child(imageFilePath);

            UploadTask uploadTask = imageRef.putBytes(socialPhotoByteStream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("ImageUpload", "Image successfully uploaded to Firebase.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error", "Image upload failed. Error:" + e);
        }
      
        // Set up location services
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
    }

    /**
     * Adds profile picture to image view
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            addProfilePictureImageView.setImageURI(selectedImage);
        }
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
                    Toast.makeText(getApplicationContext(), "Must enter and confirm a valid password [6 characters or more]", Toast.LENGTH_LONG).show();
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
                                    sharedPreferences.edit().putString("email", emailFromDialog).apply();
                                    sharedPreferences.edit().putString("password", passwordFromDialog1).apply();
                                    sharedPreferences.edit().putFloat("user_lat", (float)location.getLatitude()).apply();
                                    sharedPreferences.edit().putFloat("user_lng", (float)location.getLongitude()).apply();
                                    sharedPreferences.edit().putBoolean("use_cur_loc", false).apply();
                                    sharedPreferences.edit().putInt("urgency_thres", 1).apply();

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

                                    // Get user signed in properly and then start the main feed
                                    mAuth.signOut();
                                    mAuth.signInWithEmailAndPassword(emailFromDialog, passwordFromDialog1);
                                    Toast.makeText(CreateProfile.this, "Successfully registered your profile", Toast.LENGTH_LONG).show();
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
     */
    @Override
    public void saveCreds(String email, String password1, String password2) {
        emailFromDialog = email;
        passwordFromDialog1 = password1;
        passwordFromDialog2 = password2;
    }
}
