package com.example.campus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OtherProfileActivity extends AppCompatActivity {

    private TextView username;
    private TextView email;
    private TextView year;
    private TextView major;
    private TextView phone;
    private ImageView other_profile_pic;
    private String userID;

    private Button back;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private NewUserHelper userHelper;

    public OtherProfileActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        Log.i("OTHER ACT", getIntent().getStringExtra("uid"));

        // Set up textview information
        username = (TextView) findViewById(R.id.username_other_profile);
        email = (TextView) findViewById(R.id.email_other_profile);
        year = (TextView) findViewById(R.id.year_other_profile);
        major = (TextView) findViewById(R.id.major_other_profile);
        phone = (TextView) findViewById(R.id.phone_other_profile);

        // Database interaction
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        // Set the values
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user.getUID().equals(getIntent().getStringExtra("uid"))) {
                        username.setText(user.getUsername());
                        email.setText(user.getEmail());
                        year.setText(user.getYear());
                        major.setText(user.getMajor());
                        phone.setText(user.getPhone());
                        userID = user.getUID();
                        downloadAndSet(userID);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        back = (Button) findViewById(R.id.back_btn_other_profile);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherProfileActivity.this, MainFeedsActivity.class);
                intent.putExtra("select", getIntent().getStringExtra("back"));
                startActivity(intent);
            }
        });
    }

    public void downloadAndSet(String userID) {
        String profilePicRoot = "profilePictures/";
        String profilePicPath = profilePicRoot.concat(userID);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageReference.child(profilePicPath);

        other_profile_pic = findViewById(R.id.profile_pic);
        final long ONE_MEGABYTE = 1024 * 1024;

        profilePicRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                other_profile_pic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("Error", "Image Download failed");
            }
        });
    }
}