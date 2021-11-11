package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EditProfileActivity extends AppCompatActivity {

    private Button cancel;
    private Button save;

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
    }

    private void onCancelClicked() {
        startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
    }

    private void onSaveClicked() {
        // TODO SAVE TO DB AND GET FOR VALID INPUT
        startActivity(new Intent(this, MainFeedsActivity.class).putExtra("select", "profile"));
    }
}