package com.example.campus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

public class UrgencyDialogFragment extends DialogFragment {

    Context context;
    private SharedPreferences sharedPreferences;

    public UrgencyDialogFragment() {
        context = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences("com.example.campus", Context.MODE_PRIVATE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());

        alertDialogBuilder.setTitle("Change your Urgency Threshold");
        alertDialogBuilder.setMessage("New Alerts with a urgency rating equal or greater than your " +
                "set threshold will send a push notification to your device");

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_urgency_threshold, null);

        // Sets the dropdown selection for the alert urgency
        Spinner urgencyDropdown = view.findViewById(R.id.urgencyThresholdSpinner);
        String[] spinnerItems = new String[] {"!","!!","!!!","!!!!","!!!!!"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        urgencyDropdown.setAdapter(adapter);

        Spinner spinner = (Spinner) view.findViewById(R.id.urgencyThresholdSpinner);
        String urgencyThreshold = spinner.getSelectedItem().toString();

        alertDialogBuilder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        int urgencyInt = 1;
                        switch(urgencyThreshold) {
                            case "!":
                                urgencyInt = 1;
                            case "!!":
                                urgencyInt = 2;
                            case "!!!":
                                urgencyInt = 3;
                            case "!!!!":
                                urgencyInt = 4;
                            case "!!!!!":
                                urgencyInt = 5;
                        }

                        sharedPreferences.edit().putInt("urgency_thres", urgencyInt).apply();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return alertDialogBuilder.create();
    }
}

