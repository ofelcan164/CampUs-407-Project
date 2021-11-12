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

public class CreateProfile extends AppCompatActivity {

    // Private fields
    private Button cancelBtn;
    private Button createBtn;

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
    }

    public void createCancelClicked() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    public void createClicked() {
        // TODO NOT REALLY WHAT WILL HAPPEN
        EditText email = (EditText) findViewById(R.id.email_edit_create);
        if (email.getText().toString().indexOf("@wisc.edu") == -1) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Must enter a wisc.edu email", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        String username = email.getText().toString().substring(0, email.getText().toString().indexOf("@wisc.edu")); // TODO
        EditText passwordEnter = (EditText) findViewById(R.id.password_create);
        EditText passwordConfirm = (EditText) findViewById(R.id.password_confirm);

        if (!passwordEnter.getText().toString().equals(passwordConfirm.getText().toString())) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            // Check against DB, if valid user then TODO
            Context context = getApplicationContext();
            SQLiteDatabase userDB = context.openOrCreateDatabase("users", Context.MODE_PRIVATE,null);
            UsersDBHelper usersDBHelper = new UsersDBHelper(userDB);

            User newUser = usersDBHelper.getValidUser(username, passwordEnter.getText().toString());
            if (newUser != null) {
                // SEND ERROR THAT USER ALREADY EXISTS TOAST
            }
            else {
                // Actually new user
                newUser = User.initUser(username, passwordEnter.getText().toString());
                // SET ALL PROFILE THINGS TODO
                // Insert into db
                usersDBHelper.insertUser(newUser);

                // Save username and password to SharedPreferences
                sharedPreferences.edit().putString("username", username).apply();
                sharedPreferences.edit().putString("password", passwordEnter.getText().toString()).apply();
                sharedPreferences.edit().putBoolean("useCurLocation", newUser.getUseCurLocation()).apply();
                Intent intent = new Intent(this, MainFeedsActivity.class);
                Toast toast = Toast.makeText(context, String.format("New profile created for %s!", username), Toast.LENGTH_LONG);
                toast.show();
                startActivity(intent);
            }
        }
    }
}