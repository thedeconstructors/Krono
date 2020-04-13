package com.deconstructors.krono.auth;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterPage implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "LoginPage";

    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private CoordinatorLayout BackgroundLayout;
    private ProgressBar ProgressBar;

    private EditText RegisterName;
    private EditText RegisterEmail;
    private EditText RegisterPassword;
    private EditText RegisterConfirm;
    private Button RegisterButton;

    // Database
    private FirebaseAuth DBInstance;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public RegisterPage(Activity instance)
    {
        this.ActivityInstance = instance;
        this.setContents();
    }

    /************************************************************************
     * Purpose:         Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Database
        this.DBInstance = FirebaseAuth.getInstance();

        // Base Widgets
        this.BackgroundLayout = this.ActivityInstance.findViewById(R.id.auth_welcomeBackground);
        this.ProgressBar = this.ActivityInstance.findViewById(R.id.auth_progressBar);

        // Register Widgets
        this.RegisterName = this.ActivityInstance.findViewById(R.id.auth_register_nameEditText);
        this.RegisterEmail = this.ActivityInstance.findViewById(R.id.auth_register_emailEditText);
        this.RegisterPassword = this.ActivityInstance.findViewById(R.id.auth_register_passwordEditText);
        this.RegisterConfirm = this.ActivityInstance.findViewById(R.id.auth_register_confirmEditText);
        this.RegisterButton = this.ActivityInstance.findViewById(R.id.auth_registerButton);
        this.RegisterButton.setOnClickListener(this); // Manually Added for Readability
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void onRegisterClick()
    {
        final String name = this.RegisterName.getText().toString();
        final String email = this.RegisterEmail.getText().toString();
        final String password = this.RegisterPassword.getText().toString();
        final String confirm = this.RegisterConfirm.getText().toString();

        if (Helper.isEmpty(name) || Helper.isEmpty(email)
                || Helper.isEmpty(password) || Helper.isEmpty(confirm))
        {
            Helper.makeSnackbarMessage(this.BackgroundLayout, "Please fill in all the fields");
        }
        else if (!(password.equals(confirm)))
        {
            Helper.makeSnackbarMessage(this.BackgroundLayout,"Please make sure your passwords match");
        }
        else
        {
            Log.d(TAG, "onRegisterClick: attempting to register");
            Helper.showProgressBar(this.ProgressBar);
            Helper.hideKeyboard(this.ActivityInstance);

            this.DBInstance.createUserWithEmailAndPassword(email, password)
                             .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                             {
                                 @Override
                                 public void onSuccess(AuthResult authResult)
                                 {
                                     RegisterPage.this.onRegister(authResult, name, email);
                                     Log.d(TAG, "onRegisterClick: success");
                                     Helper.hideProgressBar(RegisterPage.this.ProgressBar);
                                 }
                             })
                             .addOnFailureListener(new OnFailureListener()
                             {
                                 @Override
                                 public void onFailure(@NonNull Exception e)
                                 {
                                     Log.d(TAG, "onRegisterClick: failed");
                                     Helper.makeSnackbarMessage(RegisterPage.this.BackgroundLayout,
                                                                "Error: Register Failed");
                                     Helper.hideProgressBar(RegisterPage.this.ProgressBar);
                                 }
                             });
        }
    }

    private void onRegister(AuthResult authResult, String name, String email)
    {
        Log.d(TAG, "onRegister: creating a new user document");

        DocumentReference ref = FirebaseFirestore
                .getInstance()
                .collection(this.ActivityInstance.getString(R.string.collection_users))
                .document(authResult.getUser().getUid());

        Map<String, Object> user = new HashMap<>();

        user.put("displayName", name);
        user.put("email", email);
        user.put("bio", "");
        user.put("friendList", new ArrayList<>());

        ref.set(user).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Helper.makeSnackbarMessage(RegisterPage.this.BackgroundLayout,
                                          "Error: Could Not Add New User");
            }
        });
    }

    /************************************************************************
     * Purpose:         Button Click Listener
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.auth_registerButton:
            {
                this.onRegisterClick();
                break;
            }
        }
    }
}