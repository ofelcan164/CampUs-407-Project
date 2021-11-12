package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity implements EditUserCredsDialog.EditUserCredsDialogListener {

    private Button cancel;
    private Button save;
    private Button editCreds;

    private CheckBox curLocationCheck;

    private SharedPreferences sharedPreferences;

    private String newPassword;

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

        sharedPreferences = getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        curLocationCheck = (CheckBox) findViewById(R.id.location_check_edit_profile);
        curLocationCheck.setChecked(sharedPreferences.getBoolean("useCurLocation", true));
        curLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCurLocationChecked(view);
            }
        });
    }

    private void onCancelClicked() {
        startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
    }

    private void onSaveClicked() {
        // TODO SAVE TO DB AND GET FOR VALID INPUT
        if (newPassword == null) {
            Toast.makeText(getApplicationContext(), "Passwords do not match or were left empty", Toast.LENGTH_SHORT).show();
            openEditCredsDialog();
        }
        else {
            Context context = getApplicationContext();
            SQLiteDatabase userDB = context.openOrCreateDatabase("users", Context.MODE_PRIVATE,null);
            UsersDBHelper usersDBHelper = new UsersDBHelper(userDB);
            User oldUser = usersDBHelper.getValidUser(sharedPreferences.getString("username", ""), sharedPreferences.getString("password", ""));

            User newUser = usersDBHelper.updateUserPassword(oldUser, newPassword);
            if (newUser != null) {
                // Save username and password to SharedPreferences
                sharedPreferences.edit().putString("username", newUser.getUsername()).apply();
                sharedPreferences.edit().putString("password", newUser.getPassword()).apply();
                sharedPreferences.edit().putBoolean("useCurLocation", newUser.getUseCurLocation()).apply();

                // Return to profile
                Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
            }
        }
    }

    private void onCurLocationChecked(View view) {
        sharedPreferences.edit().putBoolean("useCurLocation", ((CheckBox) view).isChecked()).apply(); // Update the whethere user is using current location
    }

    public void openEditCredsDialog() {
        EditUserCredsDialog dialog = new EditUserCredsDialog();
        dialog.show(getSupportFragmentManager(), "edit_creds");
    }

    @Override
    public void saveEditCreds(String password) {
        newPassword = password;
    }
}