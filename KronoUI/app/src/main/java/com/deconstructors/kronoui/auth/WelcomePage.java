package com.deconstructors.kronoui.auth;

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

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.ui.MainActivity;
import com.deconstructors.kronoui.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WelcomePage extends AppCompatActivity
        implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "WelcomePage";

    // Firebase
    private FirebaseAuth FirebaseAuth;
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
    private EditText RegisterName;
    private EditText RegisterEmail;
    private EditText RegisterPassword;
    private EditText RegisterConfirm;
    private Button RegisterButton;

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
        this.FirebaseAuth = FirebaseAuth.getInstance();
        this.FirebaseAuth.signOut(); // Debug Purpose Only

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
        this.RegisterName = findViewById(R.id.auth_register_nameEditText);
        this.RegisterEmail = findViewById(R.id.auth_register_emailEditText);
        this.RegisterPassword = findViewById(R.id.auth_register_passwordEditText);
        this.RegisterConfirm = findViewById(R.id.auth_register_confirmEditText);
        this.RegisterButton = findViewById(R.id.auth_register);
        this.RegisterButton.setOnClickListener(this); // Manually Added for Readability
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

        if(!Helper.isEmpty(email) && !Helper.isEmpty(password))
        {
            Log.d(TAG, "onEmailSignInClick: attempting to authenticate");
            showProgressBar();
            hideKeyboard();

            this.FirebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                    {
                         @Override
                         public void onSuccess(AuthResult authResult)
                         {
                             Log.d(TAG, "onEmailSignInClick: success");
                             hideProgressBar();
                         }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d(TAG, "onEmailSignInClick: failed");
                            makeSnackbarMessage("Authentication Failed");
                            hideProgressBar();
                        }
                    });
        }
        else
        {
            makeSnackbarMessage("Please fill in all the fields");
        }
    }

    private void onAnonymousSignInClick()
    {
        Log.d(TAG, "onAnonymousSignInClick: attempting to authenticate");
        showProgressBar();
        hideKeyboard();

        this.FirebaseAuth.signInAnonymously()
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
        final String name = this.RegisterName.getText().toString();
        final String email = this.RegisterEmail.getText().toString();
        final String password = this.RegisterPassword.getText().toString();
        final String confirm = this.RegisterConfirm.getText().toString();

        if (Helper.isEmpty(name) || Helper.isEmpty(email)
                || Helper.isEmpty(password) || Helper.isEmpty(confirm))
        {
            this.makeSnackbarMessage("Please fill in all the fields");
        }
        else if (!(password.equals(confirm)))
        {
            this.makeSnackbarMessage("Please make sure your passwords match");
        }
        else
        {
            Log.d(TAG, "onRegisterClick: attempting to authenticate");
            this.showProgressBar();
            this.hideKeyboard();

            this.FirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                    {
                        @Override
                        public void onSuccess(AuthResult authResult)
                        {
                            onRegister(authResult, name, email);
                            Log.d(TAG, "onRegisterClick: success");
                            hideProgressBar();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d(TAG, "onRegisterClick: failed");
                            makeSnackbarMessage("Error: Register Failed");
                            hideProgressBar();
                        }
                    });
        }
    }

    private void onRegister(AuthResult authResult, String name, String email)
    {
        DocumentReference ref = FirebaseFirestore
                .getInstance()
                .collection(getString(R.string.collection_users))
                .document(authResult.getUser().getUid());

        Map<String, Object> user = new HashMap<>();

        user.put("displayName", name);
        user.put("email", email);
        user.put("bio", "");
        user.put("friendList", new ArrayList<>());

        ref.set(user)
        .addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                makeSnackbarMessage("Error: Could Not Add New User");
            }
        });
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
        FirebaseAuth.getInstance().addAuthStateListener(FirebaseAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (FirebaseAuthListener != null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(FirebaseAuthListener);
        }
    }

    /************************************************************************
     * Purpose:         Button Click GUI Animation
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

    @Override
    public void onBackPressed()
    {
        if (this.HeaderLayout.getVisibility() == View.VISIBLE)
        {
            super.onBackPressed();
        }
        else
        {
            this.onClick(this.BackButton);
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
