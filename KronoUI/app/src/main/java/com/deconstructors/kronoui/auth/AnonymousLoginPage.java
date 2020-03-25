package com.deconstructors.kronoui.auth;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
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
        Helper.showProgressBar(this.ProgressBar);
        Helper.hideKeyboard(this.ActivityInstance);

        this.DBInstance.signInAnonymously()
                       .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                       {
                           @Override
                           public void onSuccess(AuthResult authResult)
                           {
                               AnonymousLoginPage.this.onRegister(authResult);
                               Log.d(TAG, "onAnonymousSignInClick: success");
                               Helper.hideProgressBar(AnonymousLoginPage.this.ProgressBar);
                           }
                       })
                       .addOnFailureListener(new OnFailureListener()
                       {
                           @Override
                           public void onFailure(@NonNull Exception e)
                           {
                               Log.d(TAG, "onAnonymousSignInClick: failed");
                               Helper.makeSnackbarMessage(AnonymousLoginPage.this.BackgroundLayout,
                                                          "Authentication Failed");
                               Helper.hideProgressBar(AnonymousLoginPage.this.ProgressBar);
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

        user.put("displayName", "Unknown");
        user.put("email", "Unknown");
        user.put("bio", "");
        user.put("friendList", new ArrayList<>());

        ref.set(user)
           .addOnFailureListener(new OnFailureListener()
           {
               @Override
               public void onFailure(@NonNull Exception e)
               {
                   Helper.makeSnackbarMessage(AnonymousLoginPage.this.BackgroundLayout,
                                              "Error: Could Not Add New User");
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
