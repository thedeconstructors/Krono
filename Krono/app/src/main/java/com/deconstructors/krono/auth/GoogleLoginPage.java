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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleLoginPage extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "GoogleLoginPage";
    private static final int RC_SIGN_IN = 9001;

    // XML Widgets
    private Button GoogleLogIn;
    private ProgressBar ProgressBar;

    // Database
    private FirebaseAuth AuthInstance;
    private GoogleSignInClient GoogleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_welcome);

        this.setContents();
        this.GoogleLogIn();
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.GoogleClient = GoogleSignIn.getClient(this, gso);

        // Base Widgets
        this.GoogleLogIn = findViewById(R.id.auth_googleLogIn);
        this.GoogleLogIn.setOnClickListener(this);
        this.ProgressBar = findViewById(R.id.auth_progressBar);
    }

    /************************************************************************
     * Purpose:         Initiate Google Log In Intent
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void GoogleLogIn()
    {
        Helper.showProgressBar(this, this.ProgressBar);
        Intent GoogleLogInIntent = this.GoogleClient.getSignInIntent();
        startActivityForResult(GoogleLogInIntent, RC_SIGN_IN);
        // -> onActivityResult
    }

    /************************************************************************
     * Purpose:         On Google Log In Result
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e)
            {
                Log.w(TAG, "Google sign in failed: ", e);
                Helper.hideProgressBar(this, this.ProgressBar);

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        }
    }

    /************************************************************************
     * Purpose:         Google Log In -> Firebase Auth
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void firebaseAuthWithGoogle(String idToken)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        AuthInstance.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                     {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task)
                         {
                             if (task.isSuccessful())
                             {
                                 Helper.hideProgressBar(GoogleLoginPage.this, ProgressBar);
                                 Intent returnIntent = new Intent();

                                 // Return result success
                                 if(task.getResult().getAdditionalUserInfo().isNewUser())
                                 {
                                     setResult(Activity.RESULT_FIRST_USER, returnIntent);
                                 }
                                 else
                                 {
                                     setResult(Activity.RESULT_OK, returnIntent);
                                 }

                                 finish();
                             }
                             else
                             {
                                 // Return result failure
                                 Log.w(TAG, "firebaseAuthWithGoogle: ", task.getException());
                                 Helper.hideProgressBar(GoogleLoginPage.this, ProgressBar);

                                 Intent returnIntent = new Intent();
                                 setResult(Activity.RESULT_CANCELED, returnIntent);
                                 finish();
                             }
                         }
                     });
    }

    /************************************************************************
     * Purpose:         Button Click Listener
     * Precondition:    This is necessary for some reason
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.auth_googleLogIn:
            {

            }
        }
    }
}
