package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class EditProfileActivity extends AppCompatActivity {

    private Button cancel;
    private Button save;

    private CheckBox curLocationCheck;

    private SharedPreferences sharedPreferences;

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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClicked();
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
        startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
    }

    private void onCurLocationChecked(View view) {
        sharedPreferences.edit().putBoolean("useCurLocation", ((CheckBox) view).isChecked()).apply(); // Update the whethere user is using current location
    }
}