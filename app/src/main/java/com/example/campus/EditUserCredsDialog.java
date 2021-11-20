package com.example.campus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EditUserCredsDialog extends AppCompatDialogFragment {

    private EditText passwordEnter;
    private EditText passwordConfirm;

    private EditUserCredsDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change your password");

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_creds, null);

        // Get creds
        passwordEnter = (EditText) view.findViewById(R.id.password_enter_edit_edit);
        passwordConfirm = (EditText) view.findViewById(R.id.password_confirm_edit_edit);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (passwordEnter.getText().toString().equals(passwordConfirm.getText().toString())
                                && passwordEnter.getText().toString().length() >= 6) {
                            String password = passwordConfirm.getText().toString();
                            listener.saveEditCreds(password);
                        }
                        else {
                            // Passwords must match
                            passwordEnter.setError("Passwords do no match");
                            passwordConfirm.setError("Passwords do no match");
                            listener.saveEditCreds(null);
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.saveEditCreds("");
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EditUserCredsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement EditUserCredsDialogListener");
        }
    }

    public interface EditUserCredsDialogListener {
        void saveEditCreds(String password);
    }
}
