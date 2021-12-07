package com.example.campus;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.campus.databinding.ActivityMainFeedsBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainFeedsActivity extends AppCompatActivity {

    // Private class fields
    private BottomNavigationView bottomNavigationView;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Fragment curFrag;

    /**
     * When permission for location services is requested
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    /**
     * Assign the LocationManager (when permission is granted)
     */
    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feeds);

        // Set up location services
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

        // Start default fragment - Social Feed Fragment
        curFrag = new SocialFeedFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFrag).commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);
        if (getIntent().getStringExtra("select") != null) {
            String select = getIntent().getStringExtra("select");
            if (select.equals("social")) {
                bottomNavigationView.setSelectedItemId(R.id.social_option);
            }
            else if (select.equals("market")) {
                bottomNavigationView.setSelectedItemId(R.id.market_option);
            }
            else if (select.equals("alert")) {
                bottomNavigationView.setSelectedItemId(R.id.alert_option);
            }
            else if (select.equals("profile")) {
                bottomNavigationView.setSelectedItemId(R.id.profile_option);
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.social_option);
        }
    }

    // TODO SOME SORT OF ON STOP TO SAVE DB STUFF ESPECIALLY THE USER LOCATION -  DONT WANT THAT UPDATING CONSTANTLY

    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            switch(item.getItemId()) {
                case R.id.social_option:
                    if (!(curFrag instanceof SocialFeedFragment)) {
                        curFrag = new SocialFeedFragment();
                    }
                    break;
                case R.id.market_option:
                    if (!(curFrag instanceof MarketFragment)) {
                        curFrag = new MarketFragment();
                    }
                    break;
                case R.id.alert_option:
                     if (!(curFrag instanceof AlertFragment)) {
                         curFrag = new AlertFragment();
                     }
                    break;
                case R.id.profile_option:
                     if (!(curFrag instanceof ProfileFragment)) {
                        curFrag = new ProfileFragment();
                     }
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container, curFrag).commit();
            return true;
        }
    };

}