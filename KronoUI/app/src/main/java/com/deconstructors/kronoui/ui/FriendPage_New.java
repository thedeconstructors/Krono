package com.deconstructors.kronoui.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FriendPage_New implements View.OnClickListener
{
    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private LinearLayout BottomSheet;
    private BottomSheetBehavior SheetBehavior;
    private FloatingActionButton FAB;
    private EditText SearchText;
    private ImageView SearchButton;

    // Database
    private FirebaseFirestore DBInstance;

    public FriendPage_New(Activity instance)
    {
        this.ActivityInstance = instance;
        setContents();
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
            this.makeBottomSheetSnackbarMessage("Please enter an email");
            return;
        }

        this.DBInstance
                .collection(this.ActivityInstance.getString(R.string.collection_users))
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        if (queryDocumentSnapshots.isEmpty())
                        {
                            FriendPage_New.this.makeBottomSheetSnackbarMessage("Friend Not Found");
                        }
                        else
                        {
                            FriendPage_New.this.addFriend(email);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        FriendPage_New.this.makeBottomSheetSnackbarMessage("Error Occurred!");
                    }
                });
    }

    private void addFriend(String email)
    {
        this.setSheetState(BottomSheetBehavior.STATE_HIDDEN);
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
    private void makeBottomSheetSnackbarMessage(String text)
    {
        Snackbar snackbar = Snackbar.make(this.ActivityInstance.findViewById(R.id.FriendPage_Background),
                                          text,
                                          Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(this.BottomSheet);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.show();
    }
}
