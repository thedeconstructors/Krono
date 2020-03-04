package com.demo.planactivityuserdemo.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.demo.planactivityuserdemo.R;
import com.demo.planactivityuserdemo.UserClient;
import com.demo.planactivityuserdemo.model.User;
import com.demo.planactivityuserdemo.userinterface.MainActivity;
import com.demo.planactivityuserdemo.userinterface.PlansActivity;
import com.demo.planactivityuserdemo.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener
{
    // Log
    private static final String TAG = "SignInActivity";
    private final String devEmail = "suptdeconstructors@gmail.com";
    private final String devPassword = "Destruct3d!";

    // Firebase
    private FirebaseAuth.AuthStateListener FirebaseAuthListener;
    private FirebaseAnalytics mFirebaseAnalytics;
    //private GoogleSignInClient GoogleSignInClient;

    // Views
    private EditText Email;
    private EditText Password;
    private ProgressBar ProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // For Debug Purpose Only
        // Removes the Firebase Auth Session and Signs the User Out
        //FirebaseAuth.getInstance().signOut();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_signin);

        setContentViews();
        setFirebaseAuth();
    }

    /************************************************************************
     * Purpose:         Content Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContentViews()
    {
        //getSupportActionBar().hide();

        Email = findViewById(R.id.signin_emailEditText);
        Password = findViewById(R.id.signin_passwordEditText);
        ProgressBar = findViewById(R.id.signin_progressBar);

        findViewById(R.id.signin_email_button).setOnClickListener(this);
        findViewById(R.id.signin_google_button).setOnClickListener(this);
        findViewById(R.id.signin_anonymous_button).setOnClickListener(this);
        findViewById(R.id.signin_register_button).setOnClickListener(this);

        Email.setText(devEmail);
        Password.setText(devPassword);
    }

    /************************************************************************
     * Purpose:         OnClickListener & Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.signin_email_button:
            {
                onEmailSignInClick();
                break;
            }
            case R.id.signin_google_button:
            {
                onGoogleSignInClick();
                break;
            }
            case R.id.signin_anonymous_button:
            {
                onAnonymousSignInClick();
                break;
            }
            case R.id.signin_register_button:
            {
                onRegisterClick();
                break;
            }
        }
    }

    private void onEmailSignInClick()
    {
        String email = this.Email.getText().toString();
        String password = this.Password.getText().toString();

        //check if the fields are filled out
        if(!Helper.isEmpty(email) && !Helper.isEmpty(password))
        {
            Log.d(TAG, "onEmailSignInClick: attempting to authenticate");
            showProgressBar();
            hideKeyboard();

            FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

    private void onGoogleSignInClick()
    {
        //Intent intent = new Intent(SignInActivity.this, GoogleSignInActivity.class);
        //startActivity(intent);
    }

    private void onAnonymousSignInClick()
    {
        Log.d(TAG, "onAnonymousSignInClick: attempting to authenticate");
        showProgressBar();
        hideKeyboard();

        FirebaseAuth.getInstance()
                    .signInAnonymously()
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

    private void onRegisterClick()
    {
        Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Set Firebase Authentication
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
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                    makeSnackbarMessage("Signed in");

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db
                            .collection(getString(R.string.collection_users))
                            .document(user.getUid());

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                        {
                            if(task.isSuccessful())
                            {
                                Log.d(TAG, "onComplete: successfully set the user client");
                                User user = task.getResult().toObject(User.class);
                                ((UserClient)(getApplicationContext())).setUser(user);
                            }
                        }
                    });

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
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

    /************************************************************************
     * Purpose:         Firebase Authentication Session Management
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
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
     * Purpose:         Utilities
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void makeSnackbarMessage(String string)
    {
        Snackbar.make(findViewById(R.id.signin_layout), string, Snackbar.LENGTH_SHORT).show();
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
