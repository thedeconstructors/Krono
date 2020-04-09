package com.deconstructors.krono.auth;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.ui.MainPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

public class WelcomePage extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "WelcomePage";

    // Firebase
    private FirebaseAuth DBInstance;
    private AuthStateListener FirebaseAuthListener;
    //private GoogleSignInClient GoogleSignInClient;

    // Background
    private static final int FADE_DURATION = 4000;
    private CoordinatorLayout BackgroundLayout;

    // Layout Widgets
    private LinearLayout WelcomeLayout;
    private LinearLayout LoginLayout;
    private LinearLayout RegisterLayout;
    private TextView BackButton;

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
        this.DBInstance = FirebaseAuth.getInstance();
        //this.DBInstance.signOut(); // Debug Purpose Only

        // Background & Layout Widgets
        this.BackgroundLayout = findViewById(R.id.auth_welcomeBackground);
        this.WelcomeLayout = findViewById(R.id.auth_welcomeViews);
        this.LoginLayout = findViewById(R.id.auth_signinLayout);
        this.RegisterLayout = findViewById(R.id.auth_registerLayout);
        this.BackButton = findViewById(R.id.auth_back);

        // SignIn & Register Pages
        EmailLoginPage EmailLoginPage = new EmailLoginPage(this);
        RegisterPage RegisterPage = new RegisterPage(this);
        AnonymousLoginPage AnonymousLoginPage = new AnonymousLoginPage(this);
    }

    private void startBGAnimation()
    {
        AnimationDrawable animationDrawable = (AnimationDrawable) this.BackgroundLayout.getBackground();
        animationDrawable.setEnterFadeDuration(FADE_DURATION);
        animationDrawable.setExitFadeDuration(FADE_DURATION);
        animationDrawable.start();
    }

    /************************************************************************
     * Purpose:         Firebase Authentication Session Management
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: started.");

        this.FirebaseAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Intent intent = new Intent(WelcomePage.this, MainPage.class);
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
        this.DBInstance.addAuthStateListener(FirebaseAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (FirebaseAuthListener != null)
        {
            this.DBInstance.removeAuthStateListener(FirebaseAuthListener);
        }
    }

    /************************************************************************
     * Purpose:         Button Click GUI Animation
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.auth_back:
            {
                this.WelcomeLayout.setVisibility(View.VISIBLE);
                this.LoginLayout.setVisibility(View.GONE);
                this.RegisterLayout.setVisibility(View.GONE);
                this.BackButton.setVisibility(View.GONE);
                break;
            }
            case R.id.auth_welcome_signIn:
            {
                this.WelcomeLayout.setVisibility(View.GONE);
                this.LoginLayout.setVisibility(View.VISIBLE);
                this.RegisterLayout.setVisibility(View.GONE);
                this.BackButton.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.auth_welcome_register:
            {
                this.WelcomeLayout.setVisibility(View.GONE);
                this.LoginLayout.setVisibility(View.GONE);
                this.RegisterLayout.setVisibility(View.VISIBLE);
                this.BackButton.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (this.WelcomeLayout.getVisibility() == View.VISIBLE)
        {
            super.onBackPressed();
        }
        else
        {
            this.onClick(this.BackButton);
        }
    }
}
