package com.deconstructors.krono.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.utility.Helper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class FacebookLoginPage extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "FacebookLoginPage";

    // XML Widgets
    private Button FacebookLogIn;
    private ProgressBar ProgressBar;

    // Database
    private FirebaseAuth AuthInstance;
    private CallbackManager FBCBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // These functions are in the right order
        // I hate facebook
        this.FacebookLogIn();
        setContentView(R.layout.auth_welcome);
        this.setContents();
        this.LogIn();
    }

    /************************************************************************
     * Purpose:         Initiate Google Log In Intent
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void FacebookLogIn()
    {
        // Initialize Facebook Login button
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        this.FBCBManager = CallbackManager.Factory.create();

        //this.FacebookLogIn.setPermissions(Arrays.asList(getString(R.string.users_email), getString(R.string.facebook_profile_perm)));
        LoginManager.getInstance().registerCallback(this.FBCBManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Log.d(TAG, "facebook: onSuccess: " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {
                Log.d(TAG, "facebook: onCancel");
                Helper.hideProgressBar(FacebookLoginPage.this, FacebookLoginPage.this.ProgressBar);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }

            @Override
            public void onError(FacebookException error)
            {
                Log.d(TAG, "facebook: onError: ", error);
                LoginManager.getInstance().logOut();
                Helper.hideProgressBar(FacebookLoginPage.this, FacebookLoginPage.this.ProgressBar);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    /************************************************************************
     * Purpose:         Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Database
        this.AuthInstance = FirebaseAuth.getInstance();

        // Base Widgets
        this.FacebookLogIn = findViewById(R.id.auth_facebookLogIn);
        this.FacebookLogIn.setOnClickListener(this);
        this.ProgressBar = findViewById(R.id.auth_progressBar);
        Helper.showProgressBar(this, this.ProgressBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        this.FBCBManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        this.AuthInstance.signInWithCredential(credential)
                         .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                         {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task)
                             {
                                 if (task.isSuccessful())
                                 {
                                     Log.d(TAG, "signInWithCredential: " + task.getResult().toString());
                                     Log.d(TAG, "signInWithCredential: success");

                                     // Sign in success, update UI with the signed-in user's information
                                     Intent returnIntent = new Intent();

                                     if(task.getResult().getAdditionalUserInfo().isNewUser())
                                     {
                                         setResult(Activity.RESULT_FIRST_USER, returnIntent);
                                     }
                                     else
                                     {
                                         setResult(Activity.RESULT_OK, returnIntent);
                                     }

                                     Helper.hideProgressBar(FacebookLoginPage.this,
                                                            FacebookLoginPage.this.ProgressBar);
                                     finish();
                                 }
                                 else
                                 {
                                     // If sign in fails, display a message to the user.
                                     Log.w(TAG, "signInWithFacebookCredential: ", task.getException());
                                     Helper.hideProgressBar(FacebookLoginPage.this, FacebookLoginPage.this.ProgressBar);

                                     Intent returnIntent = new Intent();
                                     setResult(Activity.RESULT_CANCELED, returnIntent);
                                     finish();
                                 }
                             }
                         });
    }

    /************************************************************************
     * Purpose:         Initiate Google Log In Intent
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void LogIn()
    {
        LoginManager.getInstance().logInWithReadPermissions(FacebookLoginPage.this,
                                                            Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.auth_facebookLogIn:
            {

            }
        }
    }
}
