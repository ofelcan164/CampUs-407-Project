package com.example.campus;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainFeedsActivity extends AppCompatActivity {

    // Private class fields
    private BottomNavigationView bottomNavigationView;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Fragment curFrag;

    // Alert Notification fields
    private NotificationManagerCompat notificationManager;
    private String postTitle;
    private String postContent;
    private String postUrgency;
    private String postUsername;
    private AlertPost latestPost = null;
    private AlertPost newPost;
    ArrayList<AlertPost> posts;
    DatabaseReference mRef;
    private static final String CHANNEL_1_ID = "channel1";
    private static final String CHANNEL_2_ID = "channel2";
    private static final String CHANNEL_3_ID = "channel3";
    private static final String CHANNEL_4_ID = "channel4";
    private static final String CHANNEL_5_ID = "channel5";

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
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feeds);

        // Set up notifications
        notificationManager = NotificationManagerCompat.from(this);

        // Set up location services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();

        } else {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        // Receive intent from notification click
        if (getIntent().getStringExtra("select") != null) {
            if (getIntent().getStringExtra("select").equals("alert")) {
                bottomNavigationView.setSelectedItemId(R.id.alert_option);
            }
        }

        /**
         * Listen for new alerts:
         */

        // Initialize posts
        posts = new ArrayList<>();

        // Database reference
        mRef = FirebaseDatabase.getInstance().getReference().child("posts").child("alerts");

        ValueEventListener alertPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    AlertPost ap = ds.getValue(AlertPost.class);
                    posts.add(ap);
                }

                // Hacky logic so notification is only sent when a new alert is created
                if (posts.size() != 0) {

                    newPost = posts.get(posts.size() - 1);

                    if (latestPost == null) {
                        latestPost = newPost;
                    }

                    else {

                        if (!newPost.equals(latestPost))  {

                            postTitle = newPost.getTitle();
                            postContent = newPost.getContent();
                            postUrgency = newPost.getUrgencyRating();
                            postUsername = newPost.getUsername();

                            latestPost = newPost;

                            switch(postUrgency) {
                                case "!":
                                    sendOnChannel1();
                                    break;
                                case "!!":
                                    sendOnChannel2();
                                    break;
                                case "!!!":
                                    sendOnChannel3();
                                    break;
                                case "!!!!":
                                    sendOnChannel4();
                                    break;
                                case "!!!!!":
                                    sendOnChannel5();
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show(); // TODO
            }
        };

        mRef.addValueEventListener(alertPostListener);
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

    private void sendOnChannel1() {

        Intent activityIntent = new Intent(this, MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setContentTitle(postUrgency + " - " + postTitle)
                .setContentText(postUsername + ": " + postContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1, notification);
    }

    private void sendOnChannel2() {

        Intent activityIntent = new Intent(this, MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setContentTitle(postUrgency + " - " + postTitle)
                .setContentText(postUsername + ": " + postContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(2, notification);
    }

    private void sendOnChannel3() {

        Intent activityIntent = new Intent(this, MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setContentTitle(postUrgency + " - " + postTitle)
                .setContentText(postUsername + ": " + postContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(3, notification);
    }

    private void sendOnChannel4() {

        Intent activityIntent = new Intent(this, MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_4_ID)
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setContentTitle(postUrgency + " - " + postTitle)
                .setContentText(postUsername + ": " + postContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(4, notification);
    }

    private void sendOnChannel5() {

        Intent activityIntent = new Intent(this, MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_5_ID)
                .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                .setContentTitle(postUrgency + " - " + postTitle)
                .setContentText(postUsername + ": " + postContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(5, notification);
    }

}