package com.example.campus;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

public class CreateProfile extends AppCompatActivity {

    private Button cancelBtn;
    private Button createBtn;
    private Button createCredsBtn;

    private EditText email;
    private EditText password1;
    private EditText password2;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        cancelBtn = (Button) findViewById(R.id.create_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCancelClicked();
            }
        });

        createCredsBtn = (Button) findViewById(R.id.create_creds_btn);
        createCredsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open dialog
                openCredsDialog();
            }
        });

        email = (EditText) findViewById(R.id.create_email);
        password1 = (EditText) findViewById(R.id.password_enter_edit);
        password2 = (EditText) findViewById(R.id.password_confirm_edit);
        progressDialog = new ProgressDialog(this);
        createBtn = (Button) findViewById(R.id.create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().indexOf("@wisc.edu") == -1) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Must enter a wisc.edu email", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Register();
                }
            }
        });

        // sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

    }

    public void createCancelClicked() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    private void Register() {
        String emailString = email.getText().toString();
        String password1String = password1.getText().toString();
        String password2String = password2.getText().toString();
        if (TextUtils.isEmpty(emailString)) {
            email.setError("Enter your email");
            return;
        } else if (TextUtils.isEmpty(password1String)) {
            password1.setError("Enter your password");
            return;
        } else if (TextUtils.isEmpty(password2String)) {
            password2.setError("Confirm your password");
            return;
        } else if (!password1String.equals(password2String)) {
            password2.setError("Enter different password");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.createUserWithEmailAndPassword(emailString, password1String)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateProfile.this, "Successfully registered", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CreateProfile.this, MainFeedsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CreateProfile.this, "Registration failed", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
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
//    /**
//     * Interface callback to get user credentials from dialog
//     * @param username
//     * @param password
//     */
//    @Override
//    public void saveCreds(String username, String password) {
//        newUsername = username;
//        newPassword = password;
//    }
