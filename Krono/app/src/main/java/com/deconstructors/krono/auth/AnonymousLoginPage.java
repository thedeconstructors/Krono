package com.deconstructors.krono.auth;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.deconstructors.krono.R;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AnonymousLoginPage implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "LoginPage";

    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private CoordinatorLayout BackgroundLayout;
    private ProgressBar ProgressBar;
    private TextView SkipButton;

    // Database
    private FirebaseAuth DBInstance;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public AnonymousLoginPage(Activity instance)
    {
        this.ActivityInstance = instance;
        setContents();
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

        // XML Widgets
        this.BackgroundLayout = this.ActivityInstance.findViewById(R.id.auth_welcomeBackground);
        this.ProgressBar = this.ActivityInstance.findViewById(R.id.auth_progressBar);
        this.SkipButton = this.ActivityInstance.findViewById(R.id.auth_anonymousSignIn);
        this.SkipButton.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void onAnonymousSignInClick()
    {
        Log.d(TAG, "onAnonymousSignInClick: attempting to authenticate");
        Helper.showProgressBar(this.ActivityInstance, this.ProgressBar);
        Helper.hideKeyboard(this.ActivityInstance);

        this.DBInstance.signInAnonymously()
                       .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                       {
                           @Override
                           public void onSuccess(AuthResult authResult)
                           {
                               AnonymousLoginPage.this.onRegister(authResult);
                               Log.d(TAG, "onAnonymousSignInClick: success");
                               Helper.hideProgressBar(AnonymousLoginPage.this.ActivityInstance,
                                                      AnonymousLoginPage.this.ProgressBar);
                           }
                       })
                       .addOnFailureListener(new OnFailureListener()
                       {
                           @Override
                           public void onFailure(@NonNull Exception e)
                           {
                               Log.d(TAG, "onAnonymousSignInClick: failed");
                               Helper.makeSnackbarMessage(AnonymousLoginPage.this.BackgroundLayout,
                                                          AnonymousLoginPage.this.ActivityInstance.getString(R.string.error_auth_failed));
                               Helper.hideProgressBar(AnonymousLoginPage.this.ActivityInstance,
                                                      AnonymousLoginPage.this.ProgressBar);
                           }
                       });
    }

    private void onRegister(AuthResult authResult)
    {
        DocumentReference ref = FirebaseFirestore
                .getInstance()
                .collection(this.ActivityInstance.getString(R.string.collection_users))
                .document(authResult.getUser().getUid());

        Map<String, Object> user = new HashMap<>();

        user.put(this.ActivityInstance.getString(R.string.users_displayname), this.ActivityInstance.getString(R.string.profile_no_displayName));
        user.put(this.ActivityInstance.getString(R.string.users_email), this.ActivityInstance.getString(R.string.profile_no_email));
        user.put(this.ActivityInstance.getString(R.string.users_bio), "");
        user.put(this.ActivityInstance.getString(R.string.friends_title), new HashMap<>());

        ref.set(user)
           .addOnFailureListener(new OnFailureListener()
           {
               @Override
               public void onFailure(@NonNull Exception e)
               {
                   Helper.makeSnackbarMessage(AnonymousLoginPage.this.BackgroundLayout,
                                              AnonymousLoginPage.this.ActivityInstance.getString(R.string.error_register_failed));
               }
           });
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
            case R.id.auth_anonymousSignIn:
            {
                this.onAnonymousSignInClick();
                break;
            }
        }
    }
}
