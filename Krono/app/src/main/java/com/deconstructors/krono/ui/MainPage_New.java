package com.deconstructors.krono.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Location;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainPage_New implements View.OnClickListener,
                                     DatePickerDialog.OnDateSetListener
{
    // Error Log
    private static final String TAG = "MainPage_New";

    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private LinearLayout BottomSheet;
    private BottomSheetBehavior SheetBehavior;
    private FloatingActionButton FAB;
    private FloatingActionButton NotificationsFAB;

    // Vars
    private EditText TitleText;
    private EditText DescriptionText;
    private Button DateButton;
    private Calendar CalendarInstance;

    // Database
    private FirebaseFirestore DBInstance;

    /************************************************************************
     * Purpose:         Fragment Create View
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public MainPage_New(Activity instance)
    {
        this.ActivityInstance = instance;
        setContents();
    }

    private void setContents()
    {
        // Database
        this.DBInstance = FirebaseFirestore.getInstance();

        // Other Widgets
        this.FAB = this.ActivityInstance.findViewById(R.id.ui_main_fab);
        this.FAB.setOnClickListener(this);
        this.NotificationsFAB = this.ActivityInstance.findViewById(R.id.ui_main_fab_notifications);
        this.TitleText = this.ActivityInstance.findViewById(R.id.MainPageNew_TitleText);
        this.DescriptionText = this.ActivityInstance.findViewById(R.id.MainPageNew_Description);
        this.DateButton = this.ActivityInstance.findViewById(R.id.MainPageNew_DateButton);
        this.DateButton.setOnClickListener(this);
        this.CalendarInstance = Calendar.getInstance();

        ImageView completeButton = this.ActivityInstance.findViewById(R.id.MainPageNew_DoneButton);
        completeButton.setOnClickListener(this);

        // Bottom Sheet Interaction
        this.BottomSheet = this.ActivityInstance.findViewById(R.id.MainPageNew_BottomSheet);
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
                        MainPage_New.this.FAB.setVisibility(View.GONE);
                        //MainPage_New.this.NotificationsFAB.setVisibility(View.GONE);
                        MainPage_New.this.TitleText.requestFocus();
                        Helper.showKeyboard(MainPage_New.this.ActivityInstance);
                        break;
                    }

                    case BottomSheetBehavior.STATE_HIDDEN:
                    {
                        MainPage_New.this.FAB.setVisibility(View.VISIBLE);
                        //MainPage_New.this.NotificationsFAB.setVisibility(View.VISIBLE);
                        Helper.hideKeyboard(MainPage_New.this.ActivityInstance);
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
    private void createNewPlan()
    {
        if (!Helper.isEmpty(this.TitleText))
        {
            // Set ref first to get the destination document id
            DocumentReference ref = FirebaseFirestore
                    .getInstance()
                    .collection(this.ActivityInstance.getString(R.string.collection_plans))
                    .document();

            Map<String, Object> activity = new HashMap<>();

            activity.put("ownerID", FirebaseAuth.getInstance().getUid());
            activity.put("planID", ref.getId());
            activity.put("title", this.TitleText.getText().toString());
            if (!Helper.isEmpty(this.DescriptionText))
            {
                activity.put("description", this.DescriptionText.getText().toString());
            }
            else
            {
                activity.put("description", "");
            }
            if (!this.DateButton.getText().toString().equals(this.ActivityInstance.getString(R.string.newactivity_timestamp)))
            {
                activity.put(this.ActivityInstance.getString(R.string.activities_timestamp), this.DateButton.getText().toString());
            }
            else
            {
                activity.put(this.ActivityInstance.getString(R.string.activities_timestamp), this.ActivityInstance.getString(R.string.newactivity_timestamp));
            }

            ref.set(activity).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    MainPage_New.this.setSheetState(BottomSheetBehavior.STATE_HIDDEN);
                    MainPage_New.this.TitleText.setText("");
                    MainPage_New.this.DescriptionText.setText("");
                    MainPage_New.this.DateButton.setText(MainPage_New.this.ActivityInstance.getString(R.string.newactivity_timestamp));
                }
            })
               .addOnFailureListener(new OnFailureListener()
               {
                   @Override
                   public void onFailure(@NonNull Exception e)
                   {
                       makeSnackbarMessage("Error: Could Not Add Activity");
                   }
               });
        }
        else
        {
            makeSnackbarMessage("Must Enter All Text Fields");
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ui_main_fab:
            {
                this.FAB.setVisibility(View.GONE);
                this.NotificationsFAB.setVisibility(View.GONE);
                this.setSheetState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.MainPageNew_DoneButton:
            {
                this.createNewPlan();
                break;
            }
            case R.id.MainPageNew_DateButton:
            {
                this.showDatePicker();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Date Picker
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void showDatePicker()
    {
        DatePickerDialog dpd = new DatePickerDialog(
                this.ActivityInstance,
                this,
                this.CalendarInstance.get(Calendar.YEAR),
                this.CalendarInstance.get(Calendar.MONTH),
                this.CalendarInstance.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Helper.displayDateFormat, Locale.getDefault());
        this.CalendarInstance.set(year, month, dayOfMonth);
        String dateString = sdf.format(this.CalendarInstance.getTime());
        this.DateButton.setText(dateString);
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
     * Purpose:         Utilities
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void makeSnackbarMessage(String text)
    {
        Snackbar snackbar = Snackbar.make(this.ActivityInstance.findViewById(R.id.MainPageNew_BottomSheet),
                                          text,
                                          Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(this.BottomSheet);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.show();
    }
}
