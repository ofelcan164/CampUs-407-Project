package com.example.campus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateProfile extends AppCompatActivity {

    Button cancel_btn;
    Button create_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        cancel_btn = (Button) findViewById(R.id.create_cancel);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCancelClicked();
            }
        });

        create_btn = (Button) findViewById(R.id.create);
        create_btn.setOnClickListener(new View.OnClickListener() {
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
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}