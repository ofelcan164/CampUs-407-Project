package com.example.campus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Trace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private ImageView edit_profile_icon;
    private ImageView profile_pic;
    private CheckBox curLocationCheck;
    private Button logoutBtn;

    private TextView username;
    private TextView email;
    private TextView year;
    private TextView major;
    private TextView phone;
    private String userID;


    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private NewUserHelper userHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutBtn = v.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().clear().apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
            }
        });

        edit_profile_icon = (ImageView) v.findViewById(R.id.edit_profile_icon);
        edit_profile_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfileIconClicked();
            }
        });

        // Set sharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        curLocationCheck = (CheckBox) v.findViewById(R.id.location_check_profile);
        curLocationCheck.setChecked(sharedPreferences.getBoolean("useCurLocation", true));
        curLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCurLocationChecked(view);
            }
        });

        // Set up textview information
        username = (TextView) v.findViewById(R.id.profileUsername);
        email = (TextView) v.findViewById(R.id.email_edit_profile);
        year = (TextView) v.findViewById(R.id.year_edit_profile);
        major = (TextView) v.findViewById(R.id.major_edit_profile);
        phone = (TextView) v.findViewById(R.id.phone_edit_profile);

        // Database interaction
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        // Set the values
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user.getUID().equals(mAuth.getUid())) {
                        username.setText(user.getUsername());
                        email.setText(user.getEmail());
                        year.setText(user.getYear());
                        major.setText(user.getMajor());
                        phone.setText(user.getPhone());
                        userID = user.getUID();
                        downloadAndSet(v, userID);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Return fragment view
        return v;
    }

    public void downloadAndSet(View v, String userID) {
        String profilePicRoot = "profilePictures/";
        String profilePicPath = profilePicRoot.concat(userID);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageReference.child(profilePicPath);

        profile_pic = v.findViewById(R.id.profile_pic);
        final long ONE_MEGABYTE = 1024 * 1024;

        profilePicRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profile_pic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("Error", "Image Download failed");
            }
        });
    }

    private void editProfileIconClicked() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
//        intent.putExtra("year", ); TODO
//        intent.putExtra("major", );
        startActivity(intent);
    }

    private void onCurLocationChecked(View view) {
        sharedPreferences.edit().putBoolean("useCurLocation", ((CheckBox) view).isChecked()).apply(); // Update the whethere user is using current location
    }
}