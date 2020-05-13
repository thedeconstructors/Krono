package com.deconstructors.krono.auth;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.ui.MainPage;
import com.deconstructors.krono.utility.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

public class WelcomePage extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "WelcomePage";
    private static int LOGIN_ACTIVITY = 7001;

    // Firebase
    private FirebaseAuth AuthInstance;
    private AuthStateListener FirebaseAuthListener;
    //GoogleLoginPage GoogleLoginPage;

    // Background
    private static final int FADE_DURATION = 4000;
    private CoordinatorLayout BackgroundLayout;

    // Layout Widgets
    private LinearLayout WelcomeLayout;
    private LinearLayout LoginLayout;
    private LinearLayout RegisterLayout;
    private TextView BackButton;
    private TextView SkipButton;
    private Button GoogleLogIn;

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
        this.AuthInstance = FirebaseAuth.getInstance();
        this.AuthInstance.signOut(); // Debug Purpose Only

        // Background & Layout Widgets
        this.BackgroundLayout = findViewById(R.id.auth_welcomeBackground);
        this.WelcomeLayout = findViewById(R.id.auth_welcomeViews);
        this.LoginLayout = findViewById(R.id.auth_signinLayout);
        this.RegisterLayout = findViewById(R.id.auth_registerLayout);
        this.BackButton = findViewById(R.id.auth_back);
        this.SkipButton = findViewById(R.id.auth_anonymousSignIn);

        // SignIn & Register Pages
        EmailLoginPage EmailLoginPage = new EmailLoginPage(this);
        RegisterPage RegisterPage = new RegisterPage(this);
        AnonymousLoginPage AnonymousLoginPage = new AnonymousLoginPage(this);

        //
        this.GoogleLogIn = findViewById(R.id.auth_googleLogIn);
        this.GoogleLogIn.setOnClickListener(this);
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
        this.AuthInstance.addAuthStateListener(FirebaseAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (FirebaseAuthListener != null)
        {
            this.AuthInstance.removeAuthStateListener(FirebaseAuthListener);
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
                //this.SkipButton.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.auth_welcome_signIn:
            {
                this.WelcomeLayout.setVisibility(View.GONE);
                this.LoginLayout.setVisibility(View.VISIBLE);
                this.RegisterLayout.setVisibility(View.GONE);
                this.BackButton.setVisibility(View.VISIBLE);
                //this.SkipButton.setVisibility(View.GONE);
                break;
            }
            case R.id.auth_welcome_register:
            {
                this.WelcomeLayout.setVisibility(View.GONE);
                this.LoginLayout.setVisibility(View.GONE);
                this.RegisterLayout.setVisibility(View.VISIBLE);
                this.BackButton.setVisibility(View.VISIBLE);
                //this.SkipButton.setVisibility(View.GONE);
                break;
            }
            case R.id.auth_googleLogIn:
            {
                Intent intent = new Intent(WelcomePage.this, GoogleLoginPage.class);
                startActivityForResult(intent, LOGIN_ACTIVITY);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_ACTIVITY)
        {
            if(resultCode == RESULT_OK)
            {
                // => Firebase Auth
            }
            if (resultCode == RESULT_CANCELED)
            {
                Helper.makeSnackbarMessage(this.BackgroundLayout,
                                           "Authentication Failed");
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
