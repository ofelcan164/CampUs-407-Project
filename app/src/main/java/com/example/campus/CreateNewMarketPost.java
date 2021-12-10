package com.example.campus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class CreateNewMarketPost extends AppCompatActivity {

    FirebaseAuth mAuth;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_market_post);

        // Post helper instance
        NewPostHelper postHelper = new NewPostHelper();

        // Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up location services
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };
        startListening();

        Button postBtn = (Button) findViewById(R.id.newPostSaleBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get post info
                EditText salePostTitle = (EditText) findViewById(R.id.newSaleTitle);
                EditText salePostPhone = (EditText) findViewById(R.id.newSalePhone);
                EditText salePostDescription = (EditText) findViewById(R.id.newSaleDescription);
                if (salePostTitle.getText().toString() != null && !salePostTitle.getText().toString().equals("")
                        && salePostPhone.getText().toString() != null && !salePostPhone.getText().toString().equals("")
                        && salePostDescription.getText().toString() != null && !salePostDescription.getText().toString().equals("")) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        MarketPost post = new MarketPost(salePostTitle.getText().toString(),
                                salePostPhone.getText().toString(),
                                salePostDescription.getText().toString(),
                                mAuth.getCurrentUser().getDisplayName(),
                                location.getLatitude(),
                                location.getLongitude(),
                                mAuth.getUid());

                        // Post the post!
                        postHelper.postMarket(post);
                        salePostTitle.setError(null);
                        salePostPhone.setError(null);
                        salePostDescription.setError(null);
                        Intent intent = new Intent(CreateNewMarketPost.this, MainFeedsActivity.class);
                        intent.putExtra("select", "market");
                        startActivity(intent);
                    }
                } else {
                    salePostTitle.setError("Please enter you post's title");
                    salePostPhone.setError("Please enter your phone number");
                    salePostDescription.setError("Please enter the sale's description");
                }
            }
        });
    }

    /**
     * Assign the LocationManager (when permission is granted)
     */
    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }
}