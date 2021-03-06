package com.example.campus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity implements EditUserCredsDialog.EditUserCredsDialogListener {

    private Button cancel;
    private Button save;
    private Button editCreds;
    private Button saveLocBtn;
    private Button editPhotoBtn;
    private CheckBox curLocationCheck;
  
    private static final int RESULT_LOAD_IMAGE = 1;
  
    ImageView editProfilePictureImageView;
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

        editProfilePictureImageView = findViewById(R.id.editProfilePictureImageView);
        editPhotoBtn = (Button) findViewById(R.id.edit_profile_picture);
        editPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

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

        downloadAndSet(mAuth.getUid());

        // Location services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
        // Prompt user for location permissions
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            // Request permissions if necessary
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            try {
                editProfilePictureImageView.setDrawingCacheEnabled(true);
                editProfilePictureImageView.buildDrawingCache();
                Bitmap bitmap = editProfilePictureImageView.getDrawingCache();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] socialPhotoByteStream = baos.toByteArray();

                String baseFolder = "profilePictures/";
                String imageFilePath = baseFolder.concat(mAuth.getUid());

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
            startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            editProfilePictureImageView.setImageURI(selectedImage);
        }
    }

    /**
     * Update picture in the edit screen
     * @param userID
     */
    public void downloadAndSet(String userID) {
        String profilePicRoot = "profilePictures/";
        String profilePicPath = profilePicRoot.concat(userID);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageReference.child(profilePicPath);

        editProfilePictureImageView = findViewById(R.id.editProfilePictureImageView);
        final long ONE_MEGABYTE = 1024 * 1024;

        profilePicRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                editProfilePictureImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("Error", "Image Download failed");
            }
        });
    }

    /**
     * Update the whether user is using current location
     */
    private void onCurLocationChecked() {
        sharedPreferences.edit().putBoolean("use_cur_loc", curLocationCheck.isChecked()).apply(); // Update the whether user is using current location

        // Update SharedPreferences based on location configuration
        if (sharedPreferences.getBoolean("use_cur_loc", false)) {
            // Update SharedPreferences user_lat and user_lng with current location
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // Put locations
                sharedPreferences.edit().putFloat("user_lat", (float) location.getLatitude()).apply();
                sharedPreferences.edit().putFloat("user_lng", (float) location.getLongitude()).apply();
            }
        }
        else {
            // Update SharedPreferences user_lat and user_lng with saved location
            if (mAuth.getCurrentUser() != null) {
                mRef.child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot ds = task.getResult();
                        if (ds != null) {
                            User user = ds.getValue(User.class);

                            // Add location to SharedPreferences
                            sharedPreferences.edit().putFloat("user_lat", (float) user.getLat()).apply();
                            sharedPreferences.edit().putFloat("user_lng", (float) user.getLng()).apply();
                        }
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
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Save location to the user
            mRef.child(mAuth.getUid()).child("lat").setValue(location.getLatitude());
            mRef.child(mAuth.getUid()).child("lng").setValue(location.getLongitude());

            Toast.makeText(this, "Current location saved.", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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