package com.example.campus;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

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
        progressDialog = new ProgressDialog(this);
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


        try{
            addProfilePictureImageView.setDrawingCacheEnabled(true);
            addProfilePictureImageView.buildDrawingCache();
            Bitmap bitmap = addProfilePictureImageView.getDrawingCache();

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
//        progressDialog.setMessage("Please wait...");
//        progressDialog.show();
//        progressDialog.setCanceledOnTouchOutside(false);

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

                            // Save username as DisplayName
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            FirebaseUser curUser = mAuth.getCurrentUser();
                            curUser.updateProfile(profileUpdates);

                            // Save all user info
                            userHelper.saveUser(new User(username, passwordFromDialog1, emailFromDialog, phone, year, major, curUser.getUid()));

                            startActivity(new Intent(CreateProfile.this, MainFeedsActivity.class));
                        } else {
                            Toast.makeText(CreateProfile.this, "Registration failed", Toast.LENGTH_LONG).show();
                        }
//                        progressDialog.dismiss();
                    }
                });
    }

    /**
     * Opens dialog for entering user credentials
     */
    public void openCredsDialog() {
        CreateUserCredsDialog dialog = new CreateUserCredsDialog();
        dialog.show(getSupportFragmentManager(), "create_creds");
    }

//    public void createClicked() {
//        EditText email = (EditText) findViewById(R.id.email_edit_create);
//        if (email.getText().toString().indexOf("@wisc.edu") == -1) {
//            Context context = getApplicationContext();
//            Toast toast = Toast.makeText(context, "Must enter a wisc.edu email", Toast.LENGTH_SHORT);
//            toast.show();
//            return;
//        }
//
//        // Check against DB
//        Context context = getApplicationContext();
//        SQLiteDatabase userDB = context.openOrCreateDatabase("users", Context.MODE_PRIVATE,null);
//        UsersDBHelper usersDBHelper = new UsersDBHelper(userDB);
//
//        // If username hasn't been entered properly
//        if (usersDBHelper.usernameExists(newUsername)) {
//            Toast.makeText(context, "Username already exists or is invalid", Toast.LENGTH_SHORT);
//            openCredsDialog();
//        }
//        // If passwords don't match
//        else if (newPassword == null) {
//            Toast.makeText(context, "Passwords do not match or were left empty", Toast.LENGTH_SHORT).show();
//            openCredsDialog();
//        }
//
//        else {
//            User newUser = User.initUser(newUsername, newPassword);
//            // SET ALL PROFILE THINGS TODO
//            // Insert into db
//            usersDBHelper.insertUser(newUser);
//
//            // Save username and password to SharedPreferences
//            sharedPreferences.edit().putString("username", newUsername).apply();
//            sharedPreferences.edit().putString("password", newPassword).apply();
//            sharedPreferences.edit().putBoolean("useCurLocation", newUser.getUseCurLocation()).apply();
//            Intent intent = new Intent(this, MainFeedsActivity.class);
//            Toast.makeText(context, String.format("New profile created for %s!", newUsername), Toast.LENGTH_LONG).show();
//            startActivity(intent);
//        }
//    }
//
//
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
