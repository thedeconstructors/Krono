package com.deconstructors.krono.utility;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Location;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.module.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************************************************************
 * Purpose:         Help the project to have cleaner code.
 * Precondition:    .
 * Postcondition:   .
 ************************************************************************/
public class Helper
{
    /************************************************************************
     * Purpose:         Check if an input is empty
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public static boolean isEmpty(EditText editText) { return editText.getText().toString().trim().equals(""); }
    public static boolean isEmpty(String string)
    {
        return string.trim().equals("");
    }

    /************************************************************************
     * Purpose:         Date Picker
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public static final String displayDateFormat = "EEE, MMM d";

    /************************************************************************
     * Purpose:         Database Document Mappers
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public static  Map<String, Object> mapActivity(Activity instance,
                                                   DocumentReference ref,
                                                   String description,
                                                   Integer duration,
                                                   Location location,
                                                   Plan plan,
                                                   String date,
                                                   String title)
    {
        Map<String, Object> activity = new HashMap<>();

        activity.put(instance.getString(R.string.activities_activityID), ref.getId());
        activity.put(instance.getString(R.string.activities_description), description);
        activity.put(instance.getString(R.string.activities_duration), duration);
        activity.put(instance.getString(R.string.activities_geoAddr), location.getAddress());
        activity.put(instance.getString(R.string.activities_geoName), location.getName());
        activity.put(instance.getString(R.string.activities_geoPoint),
                     new GeoPoint(location.getLatLng().latitude, location.getLatLng().longitude));
        activity.put(instance.getString(R.string.activities_ownerID), FirebaseAuth.getInstance().getUid());
        if (plan != null)
        {
            activity.put(instance.getString(R.string.collection_planIDs), Collections.singletonList(plan.getPlanID()));
        }
        else
        {
            activity.put(instance.getString(R.string.collection_planIDs), Collections.emptyList());
        }
        activity.put(instance.getString(R.string.activities_timestamp), date);
        activity.put(instance.getString(R.string.activities_title), title);

        return activity;
    }

    /************************************************************************
     * Purpose:         Reusable XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public static void makeSnackbarMessage(View view, String string)
    {
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show();
    }

    public static void showProgressBar(Activity instance, ProgressBar progressBar)
    {
        if (progressBar.getVisibility() == View.INVISIBLE)
        {
            progressBar.setVisibility(View.VISIBLE);
            instance.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public static void hideProgressBar(Activity instance, ProgressBar progressBar)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
            instance.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public static void hideKeyboard(Activity instance)
    {
        View view = instance.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity instance)
    {
        View view = instance.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
