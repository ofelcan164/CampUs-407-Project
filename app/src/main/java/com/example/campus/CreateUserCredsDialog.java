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

public class CreateUserCredsDialog extends AppCompatDialogFragment {

    private EditText usernameEditText;
    private EditText passwordEnter;
    private EditText passwordConfirm;

    private CreateUserCredsDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter your username and password");

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_creds, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String username = usernameEditText.getText().toString();
                        if (passwordEnter.getText().toString().equals(passwordConfirm.getText().toString())) {
                            String password = passwordConfirm.getText().toString();
                            listener.saveCreds(username, password);
                        }
                        else {
                            // Passwords must match
                            listener.saveCreds(username, null);
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.saveCreds(null, null);
                    }
                });

        // Get creds
        // TODO SET TEXT SOMEHOW?
        usernameEditText = (EditText) view.findViewById(R.id.create_username);
        passwordEnter = (EditText) view.findViewById(R.id.password_enter_edit);
        passwordConfirm = (EditText) view.findViewById(R.id.password_confirm_edit);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CreateUserCredsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CreateUserCredsDialogListener");
        }
    }

    public interface CreateUserCredsDialogListener {
        void saveCreds(String username, String password);
    }
}
