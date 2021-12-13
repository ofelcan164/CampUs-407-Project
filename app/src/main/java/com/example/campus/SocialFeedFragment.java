package com.example.campus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SocialFeedFragment extends Fragment {

    ImageButton imageButton;
    PopupMenu dropDownMenu;
    Menu plusButtonMenu;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SocialPostAdapter adapter;
    ArrayList<SocialPost> posts;

    DatabaseReference mRef;

    private SharedPreferences sharedPreferences;

    public SocialFeedFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_social_feed, container, false);

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

        // Database reference
        mRef = FirebaseDatabase.getInstance().getReference().child("posts").child("social");

        sharedPreferences = getActivity().getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        // Set up recycler view
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) v.findViewById(R.id.socialFeedRecycler);

        linearLayoutManager.setReverseLayout(true); // Newest posts first
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        // Initialize posts
        posts = new ArrayList<>();

        Location userLoc = new Location("");
        userLoc.setLatitude(sharedPreferences.getFloat("user_lat", 0));
        userLoc.setLongitude(sharedPreferences.getFloat("user_lng", 0));

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SocialPost sp = ds.getValue(SocialPost.class);

                    Location loc = new Location("");
                    loc.setLatitude(sp.getLat());
                    loc.setLongitude(sp.getLng());

                    if (loc.distanceTo(userLoc) <= MainFeedsActivity.LOCATION_RADIUS) {
                        posts.add(sp);
                    }
                }
                Log.i("SIZE", "Size " + posts.size());
                adapter = new SocialPostAdapter(posts);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        //Return fragment view
        return v;
    }

    /**
     * Tells the app to start getting
     * data from database on starting of the activity
     */
    @Override public void onStart() {
        super.onStart();
    }

    /**
     * Function to tell the app to stop getting
     * data from database on stopping of the activity
     */
    @Override public void onStop() {
        super.onStop();
    }
}