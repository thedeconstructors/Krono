package com.deconstructors.krono.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.deconstructors.krono.R;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class FriendPage_New implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "FriendPage_New";

    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private LinearLayout BottomSheet;
    private BottomSheetBehavior SheetBehavior;
    private FloatingActionButton FAB;
    private EditText SearchText;
    private ImageView SearchButton;
    private ProgressBar ProgressBar;

    // Database
    private FirebaseFirestore DBInstance;
    private FirebaseAuth AuthInstance;
    private FirebaseFunctions DBFunctions;

    public FriendPage_New(Activity instance)
    {
        this.ActivityInstance = instance;
        this.setContents();
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void setContents()
    {
        // Database
        this.DBInstance = FirebaseFirestore.getInstance();
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBFunctions = FirebaseFunctions.getInstance();

        // Bottom Sheet Interaction
        this.FAB = this.ActivityInstance.findViewById(R.id.FriendPage_FAB);
        this.FAB.setOnClickListener(this);

        this.SearchText = this.ActivityInstance.findViewById(R.id.FriendPageNew_SearchText);
        this.SearchButton = this.ActivityInstance.findViewById(R.id.FriendPageNew_SearchButton);
        this.SearchButton.setOnClickListener(this);

        this.BottomSheet = this.ActivityInstance.findViewById(R.id.FriendPageNew_BottomSheet);
        this.SheetBehavior = BottomSheetBehavior.from(this.BottomSheet);
        this.SheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                    case BottomSheetBehavior.STATE_EXPANDED:
                    {
                        FriendPage_New.this.FAB.setVisibility(View.GONE);
                        FriendPage_New.this.SearchText.requestFocus();
                        Helper.showKeyboard(FriendPage_New.this.ActivityInstance);
                        break;
                    }

                    case BottomSheetBehavior.STATE_HIDDEN:
                    {
                        FriendPage_New.this.FAB.setVisibility(View.VISIBLE);
                        Helper.hideKeyboard(FriendPage_New.this.ActivityInstance);
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        // Other XML
        this.ProgressBar = this.ActivityInstance.findViewById(R.id.friend_progressBar);
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void searchFriend()
    {
        final String email = this.SearchText.getText().toString();

        if (email.equals(""))
        {
            this.makeBottomSheetMessage(this.ActivityInstance.getString(R.string.error_friend_enteremail));
            return;
        }

        this.DBInstance
                .collection(this.ActivityInstance.getString(R.string.collection_users))
                .whereEqualTo(this.ActivityInstance.getString(R.string.users_email), email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        if (queryDocumentSnapshots.isEmpty())
                        {
                            FriendPage_New.this.makeBottomSheetMessage(FriendPage_New.this.ActivityInstance.getString(R.string.error_friend_notfound));
                        }
                        else
                        {
                            Helper.showProgressBar(ActivityInstance, ProgressBar);
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                            {
                                FriendPage_New.this.addFriend(doc.getId());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        FriendPage_New.this.makeBottomSheetMessage(FriendPage_New.this.ActivityInstance.getString(R.string.error_friend_search));
                    }
                });
    }

    private void addFriend(String friendID)
    {
        Map<String, Object> friends = new HashMap<>();
        Map<String, Object> users = new HashMap<>();
        friends.put(friendID, false);
        users.put(this.ActivityInstance.getString(R.string.collection_friends) , friends);

        // Add Friend to the Owner's Document
        this.DBInstance
            .collection(this.ActivityInstance.getString(R.string.collection_users))
            .document(this.AuthInstance.getCurrentUser().getUid())
            .set(users, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    FriendPage_New.this.setSheetState(BottomSheetBehavior.STATE_HIDDEN);
                    FriendPage_New.this.SearchText.setText("");
                }
            })
            .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    makeBottomSheetMessage(FriendPage_New.this.ActivityInstance.getString(R.string.error_friend_add) + e.getMessage());
                }
            });

        // Add Friend to the Friend's Document using Firebase Functions
        this.getAddFriendFunctions(friendID)
            .addOnSuccessListener(new OnSuccessListener<String>()
            {
                @Override
                public void onSuccess(String s)
                {
                    Helper.hideProgressBar(ActivityInstance, ProgressBar);
                    /*FriendPage_New.this.setSheetState(BottomSheetBehavior.STATE_HIDDEN);*/
                }
            })
            .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Helper.hideProgressBar(ActivityInstance, ProgressBar);
                    Log.d(TAG, "getAddFriendFunctions: " + e.getMessage());
                    /*makeBottomSheetMessage("Add Friend Error: " + e.getMessage());*/
                }
            });
    }

    private Task<String> getAddFriendFunctions(String friendID)
    {
        // Create the arguments to the callable function.
        Map<String, Object> snap = new HashMap<>();
        snap.put(this.ActivityInstance.getString(R.string.friend_friendID), friendID);
        snap.put(this.ActivityInstance.getString(R.string.functions_push), true);

        return this.DBFunctions
                .getHttpsCallable(this.ActivityInstance.getString(R.string.functions_addfriend))
                .call(snap)
                .continueWith(new Continuation<HttpsCallableResult, String>()
                {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception
                    {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    /************************************************************************
     * Purpose:         Click Listener
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.FriendPage_FAB:
            {
                this.FAB.setVisibility(View.GONE);
                this.setSheetState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.FriendPageNew_SearchButton:
            {
                this.searchFriend();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Setter & Getter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void setSheetState(int sheetState)
    {
        this.SheetBehavior.setState(sheetState);
    }

    public int getSheetState()
    {
        return this.SheetBehavior.getState();
    }

    /************************************************************************
     * Purpose:         Utility
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void makeBottomSheetMessage(String text)
    {
        Snackbar snackbar = Snackbar.make(this.ActivityInstance.findViewById(R.id.FriendPage_Background),
                                          text,
                                          Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(this.BottomSheet);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.show();
    }
}
