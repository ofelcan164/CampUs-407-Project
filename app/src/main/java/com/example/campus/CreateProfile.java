package com.example.campus;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class CreateProfile extends AppCompatActivity implements CreateUserCredsDialog.CreateUserCredsDialogListener {

    private Button cancelBtn;
    private Button createBtn;
    private Button createCredsBtn;

    private String passwordFromDialog1;
    private String passwordFromDialog2;
    private String emailFromDialog;
    private EditText username;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

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

        // Valid user, register with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(emailFromDialog, passwordFromDialog1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateProfile.this, "Successfully registered your profile", Toast.LENGTH_LONG).show();

                            sharedPreferences.edit().putString("email", emailFromDialog).apply();
                            sharedPreferences.edit().putString("password", passwordFromDialog1).apply();

                            startActivity(new Intent(CreateProfile.this, MainFeedsActivity.class));
                        } else {
                            Toast.makeText(CreateProfile.this, "Registration failed", Toast.LENGTH_LONG).show();
                        }
//                        progressDialog.dismiss();
                    }
                });

//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName("Jane Q. User")
//                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
//                .build();
//
//        user.updateProfile(profileUpdates)

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
