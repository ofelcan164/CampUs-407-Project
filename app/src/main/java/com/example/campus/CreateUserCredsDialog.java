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

    private EditText emailEditText;
    private EditText passwordEnter;
    private EditText passwordConfirm;

    private CreateUserCredsDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter your email and password");

        // Inflate custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_creds, null);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Validate user input
                        if (emailEditText.getText().toString() == null || emailEditText.getText().toString().equals("")) {
                            emailEditText.setError("Enter your email");
                            listener.saveCreds(null, null, null);
                        } else if (passwordEnter.getText().toString() == null || passwordEnter.getText().toString().equals("")) {
                            passwordEnter.setError("Enter your password");
                            listener.saveCreds(emailEditText.getText().toString(), null, null);
                        } else if (passwordConfirm.getText().toString() == null || passwordConfirm.getText().toString().equals("")) {
                            passwordConfirm.setError("Confirm your password");
                            listener.saveCreds(emailEditText.getText().toString(), null, null);
                        } else if (!passwordEnter.getText().toString().equals(passwordConfirm.getText().toString())) {
                            passwordConfirm.setError("Passwords must match");
                            listener.saveCreds(emailEditText.getText().toString(), null, null);
                        } else if (passwordEnter.getText().toString().length() < 6 || passwordConfirm.getText().toString().length() < 6) {
                            listener.saveCreds(emailEditText.getText().toString(), null, null);
                        } else {
                            // Valid user credentials
                            listener.saveCreds(emailEditText.getText().toString(), passwordEnter.getText().toString(), passwordConfirm.getText().toString());
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.saveCreds(null, null, null);
                    }
                });

        emailEditText = (EditText) view.findViewById(R.id.create_email);
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
        void saveCreds(String email, String password1, String password2);
    }
}
