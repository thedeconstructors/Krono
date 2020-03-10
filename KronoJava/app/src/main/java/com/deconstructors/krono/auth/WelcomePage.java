package com.deconstructors.krono.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.ui.MainActivity;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

public class WelcomePage extends AppCompatActivity
        implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "WelcomePage";

    // Firebase
    private FirebaseAuth Auth;
    private AuthStateListener FirebaseAuthListener;
    //private GoogleSignInClient GoogleSignInClient;

    // Background
    private static final int FADE_DURATION = 4000;
    private CoordinatorLayout BackgroundLayout;

    // Layout Widgets
    private LinearLayout HeaderLayout;
    private LinearLayout SignInLayout;
    private LinearLayout RegisterLayout;
    private Button SignInLayoutOpenButton;
    private Button RegisterLayoutOpenButton;
    private TextView BackButton;
    private ProgressBar ProgressBar;

    // SignIn Widgets
    private final String devEmail = "suptdeconstructors@gmail.com";
    private final String devPassword = "Destruct3d!";
    private Button SignInButton;
    private EditText SignInEmail;
    private EditText SignInPassword;

    // Register Widgets



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_welcome);

        this.setContents();
        this.startBGAnimation();
        this.setFirebaseAuth();
    }

    private void setContents()
    {
        // Firebase
        this.Auth = FirebaseAuth.getInstance();
        //this.Auth.signOut(); // Debug Purpose Only

        // Background & Layout Widgets
        this.BackgroundLayout = findViewById(R.id.auth_welcomeBackground);
        this.HeaderLayout = findViewById(R.id.auth_header);
        this.SignInLayout = findViewById(R.id.auth_signinLayout);
        this.SignInLayoutOpenButton = findViewById(R.id.auth_welcome_signIn);
        this.RegisterLayout = findViewById(R.id.auth_registerLayout);
        this.RegisterLayoutOpenButton = findViewById(R.id.auth_welcome_register);
        this.BackButton = findViewById(R.id.auth_back);
        this.ProgressBar = findViewById(R.id.auth_progressBar);

        // SignIn Widgets
        this.SignInEmail = findViewById(R.id.auth_signIn_emailEditText);
        this.SignInEmail.setText(this.devEmail);
        this.SignInPassword = findViewById(R.id.auth_signIn_passwordEditText);
        this.SignInPassword.setText(this.devPassword);
        this.SignInButton = findViewById(R.id.auth_signIn);
        this.SignInButton.setOnClickListener(this); // Manually Added for Readability

        // Register Widgets
    }

    private void startBGAnimation()
    {
        AnimationDrawable animationDrawable = (AnimationDrawable) this.BackgroundLayout.getBackground();
        animationDrawable.setEnterFadeDuration(FADE_DURATION);
        animationDrawable.setExitFadeDuration(FADE_DURATION);
        animationDrawable.start();
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

        //check if the fields are filled out
        if(!Helper.isEmpty(email) && !Helper.isEmpty(password))
        {
            Log.d(TAG, "onEmailSignInClick: attempting to authenticate");
            showProgressBar();
            hideKeyboard();

            this.Auth.signInWithEmailAndPassword(email, password)
                     .addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (!task.isSuccessful())
                    {
                        Log.d(TAG, "onEmailSignInClick: success");
                        hideProgressBar();
                    }
                    else
                    {
                        Log.d(TAG, "onEmailSignInClick: failed");
                        makeSnackbarMessage("Authentication Failed");
                        hideProgressBar();
                    }
                }
            });
        }
        else
        {
            makeSnackbarMessage("Fill in all the fields");
        }
    }

    private void onAnonymousSignInClick()
    {
        Log.d(TAG, "onAnonymousSignInClick: attempting to authenticate");
        showProgressBar();
        hideKeyboard();

        this.Auth.signInAnonymously()
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (!task.isSuccessful())
                {
                    Log.d(TAG, "onAnonymousSignInClick: success");
                    hideProgressBar();
                }
                else
                {
                    Log.d(TAG, "onAnonymousSignInClick: failed");
                    makeSnackbarMessage("Authentication Failed");
                    hideProgressBar();
                }
            }
        });
    }

    private void onRegisterClick()
    {

    }

    /************************************************************************
     * Purpose:         Firebase Authentication Session Management
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: started.");

        FirebaseAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Intent intent = new Intent(WelcomePage.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Auth.getInstance().addAuthStateListener(FirebaseAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (FirebaseAuthListener != null)
        {
            Auth.getInstance().removeAuthStateListener(FirebaseAuthListener);
        }
    }

    /************************************************************************
     * Purpose:         onClick
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void onClick(View view)
    {
        // Layout Animation
        switch (view.getId())
        {
            case R.id.auth_back:
            {
                this.HeaderLayout.setVisibility(View.VISIBLE);
                this.SignInLayoutOpenButton.setVisibility(View.VISIBLE);
                this.RegisterLayoutOpenButton.setVisibility(View.VISIBLE);
                this.SignInLayout.setVisibility(View.GONE);
                this.RegisterLayout.setVisibility(View.GONE);
                this.BackButton.setVisibility(View.GONE);
                break;
            }
            case R.id.auth_welcome_signIn:
            {
                this.HeaderLayout.setVisibility(View.GONE);
                this.SignInLayoutOpenButton.setVisibility(View.GONE);
                this.RegisterLayoutOpenButton.setVisibility(View.GONE);
                this.SignInLayout.setVisibility(View.VISIBLE);
                this.RegisterLayout.setVisibility(View.GONE);
                this.BackButton.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.auth_welcome_register:
            {
                this.HeaderLayout.setVisibility(View.GONE);
                this.SignInLayoutOpenButton.setVisibility(View.GONE);
                this.RegisterLayoutOpenButton.setVisibility(View.GONE);
                this.SignInLayout.setVisibility(View.GONE);
                this.RegisterLayout.setVisibility(View.VISIBLE);
                this.BackButton.setVisibility(View.VISIBLE);
                break;
            }

            // Sign In & Skip & Register Buttons
            case R.id.auth_signIn:
            {
                this.onSignInClick();
                break;
            }
            case R.id.auth_anonymousSignIn:
            {
                this.onAnonymousSignInClick();
                break;
            }
            case R.id.auth_register:
            {
                this.onRegisterClick();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Utilities
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void makeSnackbarMessage(String string)
    {
        Snackbar.make(findViewById(R.id.auth_welcomeBackground), string, Snackbar.LENGTH_SHORT).show();
    }

    private void showProgressBar()
    {
        this.ProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar()
    {
        if(this.ProgressBar.getVisibility() == View.VISIBLE)
        {
            this.ProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
