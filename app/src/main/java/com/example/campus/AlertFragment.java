package com.example.campus;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class AlertFragment extends Fragment {

    ImageButton imageButton;
    PopupMenu dropDownMenu;
    Menu plusButtonMenu;

    public AlertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alert, container, false);

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
                    case R.id.newSocialPost:
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



        //Return fragment view
        return v;
    }
}