package com.example.campus;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

public class ProfileFragment extends Fragment {

    private ImageView edit_profile_icon;
    private CheckBox curLocationCheck;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        edit_profile_icon = (ImageView) v.findViewById(R.id.edit_profile_icon);
        edit_profile_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfileIconClicked();
            }
        });

        curLocationCheck = (CheckBox) v.findViewById(R.id.location_check_profile);
        curLocationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCurLocationChecked(view);
            }
        });

        // Return fragment view
        return v;
    }

    private void editProfileIconClicked() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void onCurLocationChecked(View view) {
        MainFeedsActivity.useCurLocation = ((CheckBox) view).isChecked();
        Log.i("Check", "Switched to " + MainFeedsActivity.useCurLocation);
    }
}