package com.deconstructors.krono.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;

import com.deconstructors.krono.R;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class EmailLoginPage implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "LoginPage";

    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private CoordinatorLayout BackgroundLayout;
    private ProgressBar ProgressBar;

    private Button SignInButton;
    private EditText SignInEmail;
    private EditText SignInPassword;
    private TextView ForgotPassword;

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
        this.loadEmail();
    }

    /************************************************************************
     * Purpose:         Load Remembered Email from CACHE
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void loadEmail()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.ActivityInstance);
        String langPref = this.ActivityInstance.getString(R.string.pref_rememberme_key);
        SharedPreferences prefs = this.ActivityInstance.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String email = prefs.getString(langPref, "");

        this.SignInEmail.setText(email);
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
        this.SignInPassword = this.ActivityInstance.findViewById(R.id.auth_login_passwordEditText);
        this.SignInButton = this.ActivityInstance.findViewById(R.id.auth_loginButton);
        this.SignInButton.setOnClickListener(this); // Manually Added for Readability
        this.ForgotPassword = this.ActivityInstance.findViewById(R.id.auth_login_forgotPassword);
        this.ForgotPassword.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void onSignInClick()
    {
        final String email = this.SignInEmail.getText().toString();
        final String password = this.SignInPassword.getText().toString();

        if(!Helper.isEmpty(email) && !Helper.isEmpty(password))
        {
            Log.d(TAG, "onEmailSignInClick: attempting to authenticate");
            Helper.showProgressBar(this.ActivityInstance, this.ProgressBar);
            Helper.hideKeyboard(this.ActivityInstance);

            this.DBInstance
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                    {
                        @Override
                        public void onSuccess(AuthResult authResult)
                        {
                            Log.d(TAG, "onEmailSignInClick: success");
                            EmailLoginPage.this.saveEmail(email);
                            Helper.hideProgressBar(EmailLoginPage.this.ActivityInstance,
                                                   EmailLoginPage.this.ProgressBar);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d(TAG, "onEmailSignInClick: failed");
                            EmailLoginPage.this.saveEmail("");
                            Helper.makeSnackbarMessage(EmailLoginPage.this.BackgroundLayout,
                                                       EmailLoginPage.this.ActivityInstance.getString(R.string.error_auth_failed));
                            Helper.hideProgressBar(EmailLoginPage.this.ActivityInstance,
                                                   EmailLoginPage.this.ProgressBar);
                        }
                    });
        }
        else
        {
            Helper.makeSnackbarMessage(EmailLoginPage.this.BackgroundLayout,
                                       EmailLoginPage.this.ActivityInstance.getString(R.string.error_register_allfields));
        }
    }

    private void saveEmail(String email)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.ActivityInstance);
        String emailPref = this.ActivityInstance.getString(R.string.pref_rememberme_key);
        SharedPreferences prefs = this.ActivityInstance.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(emailPref, email);
        editor.apply();
    }

    private void onForgotPassword()
    {
        final String email = this.SignInEmail.getText().toString();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == DialogInterface.BUTTON_POSITIVE)
                {
                    sendResetEmail(email);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.ActivityInstance);
        builder.setMessage(this.ActivityInstance.getString(R.string.auth_reset_alert) + email)
               .setPositiveButton(this.ActivityInstance.getString(R.string.error_okay), dialogClickListener)
               .setNegativeButton(this.ActivityInstance.getString(R.string.activitydetail_cancel), dialogClickListener)
               .show();
    }

    private void sendResetEmail(String email)
    {
        if(!Helper.isEmpty(email))
        {
            this.DBInstance
                    .sendPasswordResetEmail(email)
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Helper.makeSnackbarMessage(EmailLoginPage.this.BackgroundLayout,
                                                       EmailLoginPage.this.ActivityInstance.getString(R.string.auth_reset_email));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Helper.makeSnackbarMessage(EmailLoginPage.this.BackgroundLayout,
                                                       EmailLoginPage.this.ActivityInstance.getString(R.string.error_friend_notfound));
                        }
                    });
        }
        else
        {
            Helper.makeSnackbarMessage(EmailLoginPage.this.BackgroundLayout,
                                       EmailLoginPage.this.ActivityInstance.getString(R.string.error_friend_enteremail));
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
            case R.id.auth_login_forgotPassword:
            {
                this.onForgotPassword();
                break;
            }
        }
    }
}
