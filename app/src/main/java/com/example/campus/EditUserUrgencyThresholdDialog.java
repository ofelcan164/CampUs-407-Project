package com.example.campus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EditUserUrgencyThresholdDialog extends AppCompatDialogFragment {

    private EditUserUrgencyThresholdDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change your Urgency Threshold");
        builder.setMessage("New Alerts with a urgency rating equal or greater than your set threshold will send a push notification");

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

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.saveUrgencyThreshold(urgencyThreshold);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.saveUrgencyThreshold("");
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EditUserUrgencyThresholdDialog.EditUserUrgencyThresholdDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement EditUserUrgencyThresholdDialogListener");
        }
    }

    public interface EditUserUrgencyThresholdDialogListener {
        void saveUrgencyThreshold(String urgency);
    }
}
