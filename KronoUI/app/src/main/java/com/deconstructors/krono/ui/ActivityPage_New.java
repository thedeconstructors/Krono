package com.deconstructors.krono.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Location;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.utility.Helper;
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

public class ActivityPage_New implements View.OnClickListener,
                                         DatePickerDialog.OnDateSetListener
{
    // Error Log
    private static final String TAG = "NewActivityPage";
    private static final int MAPACTIVITY_REQUESTCODE = 5002;

    // XML Widgets
    private Activity ActivityInstance; // This is not the activity module
    private LinearLayout BottomSheet;
    private BottomSheetBehavior SheetBehavior;
    private FloatingActionButton FAB;

    private EditText TitleText;
    private EditText DescText;
    private Calendar CalendarInstance;
    private Button DateButton;
    private Button LocationButton;
    private ImageView AddButton;

    // Database
    private Plan Plan;
    private FirebaseFirestore DBInstance;

    public ActivityPage_New(Activity instance, Plan plan)
    {
        this.ActivityInstance = instance;
        this.Plan = plan;
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

        // XML Contents
        this.FAB = this.ActivityInstance.findViewById(R.id.ActivityPage_FAB);
        this.FAB.setOnClickListener(this);
        this.TitleText = this.ActivityInstance.findViewById(R.id.ActivityPageNew_TitleText);
        this.DescText = this.ActivityInstance.findViewById(R.id.ActivityPageNew_Description);
        this.CalendarInstance = Calendar.getInstance();
        this.DateButton = this.ActivityInstance.findViewById(R.id.ActivityPageNew_DateButton);
        this.DateButton.setOnClickListener(this);
        this.LocationButton = this.ActivityInstance.findViewById(R.id.ActivityPageNew_LocationButton);
        this.LocationButton.setOnClickListener(this);
        ImageView completeButton = this.ActivityInstance.findViewById(R.id.ActivityPageNew_DoneButton);
        completeButton.setOnClickListener(this);

        // Bottom Sheet Interaction
        this.BottomSheet = this.ActivityInstance.findViewById(R.id.ActivityPageNew_BottomSheet);
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
                        ActivityPage_New.this.FAB.setVisibility(View.GONE);
                        ActivityPage_New.this.TitleText.requestFocus();
                        Helper.showKeyboard(ActivityPage_New.this.ActivityInstance);
                        break;
                    }

                    case BottomSheetBehavior.STATE_HIDDEN:
                    {
                        ActivityPage_New.this.FAB.setVisibility(View.VISIBLE);
                        Helper.hideKeyboard(ActivityPage_New.this.ActivityInstance);
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
    private void createNewActivity()
    {
        if (!Helper.isEmpty(this.TitleText)
                && !Helper.isEmpty(this.DescText)
                && !this.DateButton.getText().toString().equals(""))
        {
            // Set ref first to get the destination document id
            DocumentReference ref = FirebaseFirestore
                    .getInstance()
                    .collection(this.ActivityInstance.getString(R.string.collection_plans))
                    .document(this.Plan.getPlanID())
                    .collection(this.ActivityInstance.getString(R.string.collection_activities))
                    .document();

            Map<String, Object> activity = new HashMap<>();

            activity.put("ownerID", FirebaseAuth.getInstance().getUid());
            activity.put("planID", this.Plan.getPlanID());
            activity.put("activityID", ref.getId());
            activity.put("title", this.TitleText.getText().toString());
            activity.put("description", this.DescText.getText().toString());
            activity.put("timestamp", this.DateButton.getText().toString());

            ref.set(activity).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    ActivityPage_New.this.ActivityInstance.finish();
                }
            })
               .addOnFailureListener(new OnFailureListener()
               {
                   @Override
                   public void onFailure(@NonNull Exception e)
                   {
                       ActivityPage_New.this.makeBottomSheetSnackbarMessage("Error: Could Not Add Activity");
                   }
               });
        }
        else
        {
            this.makeBottomSheetSnackbarMessage("Must Enter All Text Fields");
        }
    }

    /************************************************************************
     * Purpose:         Click Events
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ActivityPage_FAB:
            {
                this.FAB.setVisibility(View.GONE);
                this.setSheetState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.ActivityPageNew_DoneButton:
            {
                this.createNewActivity();
                break;
            }
            case R.id.ActivityPageNew_DateButton:
            {
                this.showDatePicker();
                break;
            }
            case R.id.ActivityPageNew_LocationButton:
            {
                Intent intent = new Intent(this.ActivityInstance, ActivityPage_Map.class);
                this.ActivityInstance.startActivityForResult(intent, MAPACTIVITY_REQUESTCODE);
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         On Google Map Activity Completed
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void ActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == MAPACTIVITY_REQUESTCODE && resultCode == Activity.RESULT_OK && data != null)
        {
            String text = this.ActivityInstance.getString(R.string.intent_location);
            Location location = data.getParcelableExtra(text);
            this.LocationButton.setText(location.getName());
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
    private void makeBottomSheetSnackbarMessage(String text)
    {
        Snackbar snackbar = Snackbar.make(this.ActivityInstance.findViewById(R.id.ActivityPageNew_BottomSheet),
                                          text,
                                          Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(this.BottomSheet);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.show();
    }
}
