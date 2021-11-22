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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity implements EditUserCredsDialog.EditUserCredsDialogListener {

    private Button cancel;
    private Button save;
    private Button editCreds;

    private CheckBox curLocationCheck;

    private EditText emailEditText;
    private EditText majorEditText;
    private EditText phoneEditText;
    private EditText yearEditText;

    private SharedPreferences sharedPreferences;

    private String newPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

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

        // TODO TODO
//        emailEditText = (EditText) findViewById(R.id.email_edit_edit_profile);
//        emailEditText.setText(mAuth.getCurrentUser().getEmail());
//
//        yearEditText = (EditText) findViewById(R.id.year_edit_edit_profile);
//        yearEditText.setText(getIntent().getStringExtra("year"));
//
//        majorEditText = (EditText) findViewById(R.id.major_edit_edit_profile);
//        majorEditText.setText(getIntent().getStringExtra("major"));
//
//        phoneEditText = (EditText) findViewById(R.id.phone_edit_edit_profile);
//        phoneEditText.setText(mAuth.getCurrentUser().getPhoneNumber());
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
            // TODO EMAIL VERIFICATION??
            if (!newPassword.equals("")) {
                FirebaseUser curUser = mAuth.getCurrentUser();
                curUser.updatePassword(newPassword);

                // Save new password to SharedPreferences
                sharedPreferences.edit().putString("password", newPassword).apply();

                // Return to profile
                Toast.makeText(getApplicationContext(), "Password updated!", Toast.LENGTH_SHORT).show();
            }

            // Other none credential based user info


            startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
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