package com.deconstructors.krono.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity
{
    // Error Handler Log Search
    private static final String _Tag = "Krono_Login_Log";

    // Firebase Authentication Listener
    private FirebaseAuth.AuthStateListener _AuthStateListener;

    // XML Widgets
    private EditText _email;
    private EditText _password;
    private Button _emailLogIn;
    private Button _devLogin; // <-- Remove
    private Button _guestLogIn;
    private ProgressBar _progressBar;
    private RelativeLayout _relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        _email = findViewById(R.id.edittext_email);
        _password = findViewById(R.id.edittext_password);
        _emailLogIn = findViewById(R.id.button_emailLogin);
        _devLogin = findViewById(R.id.button_devLogin); // <-- Remove
        _guestLogIn = findViewById(R.id.button_guestLogin);
        _progressBar = findViewById(R.id.login_progressBar);
        _relativeLayout = findViewById(R.id.login_relativeLayout);

        //setupFirebaseAuth();
    }

    /**************************** Database *********************************/

    /***********************************************************************
     * Purpose:         Setup Firebase Authentication
     * Precondition:    Authentication State Changed
     * Postcondition:   Change Intent when signed in
     ************************************************************************/
    /*private void setupFirebaseAuth()
    {
        Log.d(_Tag, "setUpFirebaseAuth started");

        _AuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Log.d(_Tag, "setupFirebaseAuth - signed in as:" + user.getUid());
                    Toast.makeText(LoginPage.this, "Signed in", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d(_Tag, "setupFirebaseAuth - signed out");
                }
            }
        };
    }*/

    /***********************************************************************
     * Purpose:         onStart
     * Precondition:    Comes right after onCreate
     * Postcondition:   Setup Firebase Authentication Listener On Start
     ************************************************************************/
    @Override
    public void onStart()
    {
        super.onStart();

        //Sign user in if they are already signed in
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            //get user id from db and log them in
            final String loginid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //get user in db and set session id to its id
            FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            startSnackbarMessage("Signed in");
                            setProgressbar(false);
                            Intent intent = new Intent(LoginPage.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    startSnackbarMessage("Failed to retreive user:\n" +
                            e.toString());
                    setProgressbar(false);
                }
            });
        }
        //FirebaseAuth.getInstance().addAuthStateListener(_AuthStateListener);
    }

    /***********************************************************************
     * Purpose:         onStop
     * Precondition:    Comes right after onPause and before onDestroy
     * Postcondition:   Remove Firebase Authentication Listener On Stop
     ************************************************************************/
    @Override
    public void onStop()
    {
        super.onStop();/*
        if (_AuthStateListener != null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(_AuthStateListener);
        }*/
    }

    /****************************** Login **********************************/

    /***********************************************************************
     * Purpose:         Log in Button Clicks
     * Precondition:    .
     * Postcondition:   Please check the link below for more information.
     *                  firebase.google.com/docs/auth/android/password-auth
     ************************************************************************/
    public void onEmailLoginButtonClick(View view)
    {
        if (!isEmpty(_email) && !isEmpty(_password))
        {
            Log.d(_Tag, "onEmailLoginButtonClick - Valid email and password.");
            setProgressbar(true);

            //sign in
            FirebaseAuth.getInstance().signInWithEmailAndPassword(getText(_email), getText(_password))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            //final String loginid = task.getResult().getUser().getUid();
                            //get user in db and set session id to its id
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(FirebaseAuth.getInstance().getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            startSnackbarMessage("Signed in");
                                            setProgressbar(false);
                                            Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    startSnackbarMessage("Failed to retreive user:\n" +
                                            e.toString());
                                    setProgressbar(false);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            startSnackbarMessage("Invalid email or password");
                            setProgressbar(false);
                        }
                    });
        }
        else
        {
            startSnackbarMessage("Please enter all the fields");
        }
    }

    public void onDevLoginButtonClick(View view)
    {
        String dev_email = "suptdeconstructors@gmail.com";
        String dev_password = "Destruct3d!";

        _email.setText(dev_email);
        _password.setText(dev_password);

        onEmailLoginButtonClick(findViewById(R.id.button_emailLogin));
    }

    public void onGuestLoginButtonClick(View view)
    {
        closeKeyboard();
        setProgressbar(true);

        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            startSnackbarMessage("Signed in");
                            setProgressbar(false);
                        }
                        else
                        {
                            //Log.d(_Tag, task.getException().toString());
                            startSnackbarMessage("Failed to sign in anonymously");
                            setProgressbar(false);
                        }
                    }
                });
    }

    public void onRegisterButtonClick(View view)
    {
        if (!isEmpty(_email) && !isEmpty(_password))
        {
            Intent intent = new Intent(this, RegisterPage.class);
            intent.putExtra("email", getText(_email));
            intent.putExtra("password", getText(_password));
            startActivity(intent);
        }
        else
        {
            startSnackbarMessage("Please enter all the fields");
        }
    }

    /****************************** Online *********************************/

    /***********************************************************************
     * Purpose:         getGoogleServiceStatus
     * Precondition:    .
     * Postcondition:   Return Google Service/ Map API Availability
     ************************************************************************/
    public boolean getGoogleServiceStatus()
    {
        boolean temp_result = true;
        return temp_result;
    }

    /******************************* Others *********************************/

    /***********************************************************************
     * Purpose:         startSnackbarMessage
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void startSnackbarMessage(String string)
    {
        Snackbar.make(_relativeLayout, string, Snackbar.LENGTH_SHORT).show();
    }

    /***********************************************************************
     * Purpose:         closeKeyboard
     * Precondition:    .
     * Postcondition:   Close the keyboard when open
     *                  Do nothing when the keyboard is already hidden.
     ************************************************************************/
    private void closeKeyboard()
    {
        View temp_view = this.getCurrentFocus();
        if (temp_view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(temp_view.getWindowToken(), 0);
        }
    }

    /***********************************************************************
     * Purpose:         isEmpty
     * Precondition:    .
     * Postcondition:   Checks if EditText Field is Empty
     ************************************************************************/
    private boolean isEmpty(EditText editText)
    {
        return editText.getText().toString().equals("");
    }

    /***********************************************************************
     * Purpose:         getText
     * Precondition:    .
     * Postcondition:   Return String from EditText Field
     ************************************************************************/
    private String getText(EditText editText)
    {
        return editText.getText().toString();
    }

    /***********************************************************************
     * Purpose:         setProgressbar
     * Precondition:    .
     * Postcondition:   Enable or Disable Progressbar
     ************************************************************************/
    private void setProgressbar(boolean bool)
    {
        if (_progressBar.getVisibility() == View.INVISIBLE && bool)
        {
            _progressBar.setVisibility(View.VISIBLE);
        }
        else if (_progressBar.getVisibility() == View.VISIBLE && !bool)
        {
            _progressBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d(_Tag, "setProgressbar - bugged");
        }
    }
}
