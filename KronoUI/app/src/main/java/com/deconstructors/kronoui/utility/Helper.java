package com.deconstructors.kronoui.utility;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.deconstructors.kronoui.module.Activity;
import com.deconstructors.kronoui.module.Plan;
import com.deconstructors.kronoui.module.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    public static boolean isEmpty(EditText editText)
    {
        return editText.getText().toString().equals("");
    }

    public static boolean isEmpty(String string)
    {
        return string.equals("");
    }

    /************************************************************************
     * Purpose:         Check a list using an input
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    // Change field type to getDocumentID and combine both
    public static Activity getActivity(List<Activity> list, String id)
    {
        for (Activity activity : list)
        {
            if (activity.getActivityID().equals(id))
            {
                return activity;
            }
        }

        return null;
    }

    public static Plan getPlan(List<Plan> list, String id)
    {
        for (Plan plan : list)
        {
            if (plan.getPlanID().equals(id))
            {
                return plan;
            }
        }

        return null;
    }

    public static User getFriend(List<User> list, String email)
    {
        for (User user : list)
        {
            if (user.getEmail().equals(email))
            {
                return user;
            }
        }

        return null;
    }

    /************************************************************************
     * Purpose:         Date Picker
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public static final String displayDateFormat = "EEE, MMM d";
    public static final String firebaseDateFormat = "dd-MM-yyyy HH:mm:ss";

    public static Date getDateFromString(String date)
    {
        try
        {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date);
        }
        catch (ParseException e)
        {
            return null ;
        }

    }

    private void showProgressBar(ProgressBar progressBar)
    {
        if (progressBar.getVisibility() == View.INVISIBLE)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(ProgressBar progressBar)
    {
        if(progressBar.getVisibility() == View.VISIBLE)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
