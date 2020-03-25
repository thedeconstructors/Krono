package com.deconstructors.kronoui.auth;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailLoginPage implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "LoginPage";

    // Debug
    private final String devEmail = "suptdeconstructors@gmail.com";
    private final String devPassword = "Destruct3d!";

    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private CoordinatorLayout BackgroundLayout;
    private ProgressBar ProgressBar;

    private Button SignInButton;
    private EditText SignInEmail;
    private EditText SignInPassword;

    // Database
    private FirebaseAuth DBInstance;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public EmailLoginPage(Activity instance)
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

        // Login Widgets
        this.SignInEmail = this.ActivityInstance.findViewById(R.id.auth_login_emailEditText);
        this.SignInEmail.setText(this.devEmail);
        this.SignInPassword = this.ActivityInstance.findViewById(R.id.auth_login_passwordEditText);
        this.SignInPassword.setText(this.devPassword);
        this.SignInButton = this.ActivityInstance.findViewById(R.id.auth_loginButton);
        this.SignInButton.setOnClickListener(this); // Manually Added for Readability
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void onSignInClick()
    {
        String email = this.SignInEmail.getText().toString();
        String password = this.SignInPassword.getText().toString();

        if(!Helper.isEmpty(email) && !Helper.isEmpty(password))
        {
            Log.d(TAG, "onEmailSignInClick: attempting to authenticate");
            Helper.showProgressBar(this.ProgressBar);
            Helper.hideKeyboard(this.ActivityInstance);

            this.DBInstance
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                    {
                        @Override
                        public void onSuccess(AuthResult authResult)
                        {
                            Log.d(TAG, "onEmailSignInClick: success");
                            Helper.hideProgressBar(EmailLoginPage.this.ProgressBar);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d(TAG, "onEmailSignInClick: failed");
                            Helper.makeSnackbarMessage(EmailLoginPage.this.BackgroundLayout,
                                                       "Authentication Failed");
                            Helper.hideProgressBar(EmailLoginPage.this.ProgressBar);
                        }
                    });
        }
        else
        {
            Helper.makeSnackbarMessage(EmailLoginPage.this.BackgroundLayout,
                                       "Please fill in all the fields");
        }
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
            case R.id.auth_loginButton:
            {
                this.onSignInClick();
                break;
            }
        }
    }
}
