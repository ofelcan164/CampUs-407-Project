package com.example.campus;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlertFragment extends Fragment {

    ImageButton imageButton;
    PopupMenu dropDownMenu;
    Menu plusButtonMenu;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    AlertPostAdapter adapter;
    ArrayList<AlertPost> posts;

    DatabaseReference mRef;

    private SharedPreferences sharedPreferences;

    private static final String CHANNEL_1_ID = "channel1";
    private static final String CHANNEL_2_ID = "channel2";
    private static final String CHANNEL_3_ID = "channel3";
    private static final String CHANNEL_4_ID = "channel4";
    private static final String CHANNEL_5_ID = "channel5";

    private NotificationManagerCompat notificationManager;
    private String postTitle;
    private String postContent;
    private String postUrgency;
    private String postUsername;
    private AlertPost latestPost = null;
    private AlertPost newPost;

    public AlertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alert, container, false);

        // Set up notifications
        notificationManager = NotificationManagerCompat.from(getActivity());

        sharedPreferences = getContext().getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        imageButton = (ImageButton) v.findViewById(R.id.floatingPlusButton);
        dropDownMenu = new PopupMenu(getContext(), imageButton);
        plusButtonMenu = dropDownMenu.getMenu();

        // Inflate menu from from XML id
        dropDownMenu.getMenuInflater().inflate(R.menu.plus_button_menu, plusButtonMenu);

        // Set action when plus button is clicked
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.newSocialPostText:
                        Intent newSocialPostIntent = new Intent(getActivity(), CreateNewSocialPost.class);
                        startActivity(newSocialPostIntent);
                        return true;
                    case R.id.newMarketSale:
                        Intent newMarketPostIntent = new Intent(getActivity(), CreateNewMarketPost.class);
                        startActivity(newMarketPostIntent);
                        return true;
                    case R.id.newAlert:
                        Intent newAlertPostIntent = new Intent(getActivity(), CreateNewAlertPost.class);
                        startActivity(newAlertPostIntent);
                        return true;
                }

                return false;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropDownMenu.show();
            }
        });

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
//                if (posts.size() != 0) {
//
//                    newPost = posts.get(posts.size() - 1);
//
//                    if (latestPost == null) {
//                        latestPost = newPost;
//                    }
//
//                    else {
//
//                        if (!newPost.equals(latestPost))  {
//
//                            postTitle = newPost.getTitle();
//                            postContent = newPost.getContent();
//                            postUrgency = newPost.getUrgencyRating();
//                            postUsername = newPost.getUsername();
//
//                            latestPost = newPost;
//
//                            switch(postUrgency) {
//                                case "!":
//                                    sendOnChannel1(v);
//                                case "!!":
//                                    sendOnChannel2(v);
//                                case "!!!":
//                                    sendOnChannel3(v);
//                                case "!!!!":
//                                    sendOnChannel4(v);
//                                case "!!!!!":
//                                    sendOnChannel5(v);
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        };

        mRef.addValueEventListener(alertPostListener);


        // Set up recycler view
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) v.findViewById(R.id.alertFeedRecycler);

        linearLayoutManager.setReverseLayout(true); // Newest posts first
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        // Query of the database with FirebaseRecyclerOptions
        FirebaseRecyclerOptions<AlertPost> options = new FirebaseRecyclerOptions.Builder<AlertPost>()
                .setQuery(mRef, AlertPost.class).build();

        // Connect the adapter
        adapter = new AlertPostAdapter(options);
        recyclerView.setAdapter(adapter);

        //Return fragment view
        return v;
    }

    private void sendOnChannel1(View view) {

        Intent activityIntent = new Intent(getActivity(), MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
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

    private void sendOnChannel2(View view) {

        Intent activityIntent = new Intent(getActivity(), MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_2_ID)
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

    private void sendOnChannel3(View view) {

        Intent activityIntent = new Intent(getActivity(), MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_3_ID)
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

    private void sendOnChannel4(View view) {

        Intent activityIntent = new Intent(getActivity(), MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_4_ID)
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

    private void sendOnChannel5(View view) {

        Intent activityIntent = new Intent(getActivity(), MainFeedsActivity.class);
        activityIntent.putExtra("select", "alert");
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_5_ID)
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

    /**
     * Tells the app to start getting
     * data from database on starting of the activity
     */
    @Override public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /**
     * Function to tell the app to stop getting
     * data from database on stopping of the activity
     */
    @Override public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}