package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateProfile extends AppCompatActivity implements CreateUserCredsDialog.CreateUserCredsDialogListener {

    // Private fields
    private Button cancelBtn;
    private Button createBtn;
    private Button createCredsBtn;

    private String newUsername;
    private String newPassword;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        cancelBtn = (Button) findViewById(R.id.create_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCancelClicked();
            }
        });

        createBtn = (Button) findViewById(R.id.create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createClicked();
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
    }

    public void createCancelClicked() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    public void createClicked() {
        EditText email = (EditText) findViewById(R.id.email_edit_create);
        if (email.getText().toString().indexOf("@wisc.edu") == -1) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Must enter a wisc.edu email", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // Check against DB
        Context context = getApplicationContext();
        SQLiteDatabase userDB = context.openOrCreateDatabase("users", Context.MODE_PRIVATE,null);
        UsersDBHelper usersDBHelper = new UsersDBHelper(userDB);

        // If username hasn't been entered properly
        if (usersDBHelper.usernameExists(newUsername)) {
            Toast.makeText(context, "Username already exists or is invalid", Toast.LENGTH_SHORT);
            openCredsDialog();
        }
        // If passwords don't match
        else if (newPassword == null) {
            Toast.makeText(context, "Passwords do not match or were left empty", Toast.LENGTH_SHORT).show();
            openCredsDialog();
        }

        else {
            User newUser = User.initUser(newUsername, newPassword);
            // SET ALL PROFILE THINGS TODO
            // Insert into db
            usersDBHelper.insertUser(newUser);

            // Save username and password to SharedPreferences
            sharedPreferences.edit().putString("username", newUsername).apply();
            sharedPreferences.edit().putString("password", newPassword).apply();
            sharedPreferences.edit().putBoolean("useCurLocation", newUser.getUseCurLocation()).apply();
            Intent intent = new Intent(this, MainFeedsActivity.class);
            Toast.makeText(context, String.format("New profile created for %s!", newUsername), Toast.LENGTH_LONG).show();
            startActivity(intent);
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
     * Interface callback to get user credentials from dialog
     * @param username
     * @param password
     */
    @Override
    public void saveCreds(String username, String password) {
        newUsername = username;
        newPassword = password;
    }
}