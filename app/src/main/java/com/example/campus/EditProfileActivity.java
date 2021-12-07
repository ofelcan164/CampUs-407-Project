package com.example.campus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
    private Button editPhotoBtn;
    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView editProfilePictureImageView;
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

        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);
        newPassword = sharedPreferences.getString("Password", "");

        curLocationCheck = (CheckBox) findViewById(R.id.location_check_edit_profile);
        curLocationCheck.setChecked(sharedPreferences.getBoolean("useCurLocation", true));
        curLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCurLocationChecked(view);
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
    }

    private void onCancelClicked() {
        startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
    }

    private void onSaveClicked() {
        if (newPassword == null) {
            Toast.makeText(getApplicationContext(), "Passwords much both be matching and valid [6 characters or more]", Toast.LENGTH_SHORT).show();
            openEditCredsDialog();
            return;
        }
        else {
            if (!newPassword.equals("")) {
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
                    year, major,
                    mAuth.getCurrentUser().getUid()));

            startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
        }
        try{
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
     * Update the whether user is using current location
     */
    private void onCurLocationChecked(View view) {
        sharedPreferences.edit().putBoolean("useCurLocation", ((CheckBox) view).isChecked()).apply();
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