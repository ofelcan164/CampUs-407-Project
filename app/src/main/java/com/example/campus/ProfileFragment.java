package com.example.campus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private LocationManager locationManager;
    private LocationListener locationListener;

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
        curLocationCheck.setChecked(sharedPreferences.getBoolean("use_cur_loc", false));
        curLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCurLocationChecked();
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

        // Location services
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
        // Prompt user for location permissions TODO
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            // Request permissions if necessary
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

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
        startActivity(intent);
    }

    /**
     * Update the whether user is using current location
     */
    private void onCurLocationChecked() {
        sharedPreferences.edit().putBoolean("use_cur_loc", curLocationCheck.isChecked()).apply(); // Update the whether user is using current location

        // Update SharedPreferences based on location configuration
        if (sharedPreferences.getBoolean("user_cur_loc", false)) {
            // Update SharedPreferences user_lat and user_lng with current location
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // Put locations
                sharedPreferences.edit().putFloat("user_lat", (float) location.getLatitude()).apply();
                sharedPreferences.edit().putFloat("user_lng", (float) location.getLongitude()).apply();
            }
        }
        else {
            // Update SharedPreferences user_lat and user_lng with saved location
            if (mAuth.getCurrentUser() != null) {
                mRef.child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        User user = task.getResult().getValue(User.class);

                        // Add location to SharedPreferences
                        sharedPreferences.edit().putFloat("user_lat", (float)user.getLat()).apply();
                        sharedPreferences.edit().putFloat("user_lng", (float)user.getLng()).apply();
                    }
                });
            }
        }

        Toast.makeText(getActivity().getApplicationContext(), "Updated location configuration to "
                + ((curLocationCheck.isChecked()) ? "use current location." : "use saved location."), Toast.LENGTH_SHORT);
    }

    /**
     * Assign the LocationManager (when permission is granted)
     */
    public void startListening() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}