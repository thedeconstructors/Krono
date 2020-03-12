package com.deconstructors.kronoui.utility;

import android.widget.EditText;

import com.deconstructors.kronoui.module.Activity;
import com.deconstructors.kronoui.module.Plan;
import com.deconstructors.kronoui.module.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Helper
{
    public static final String displayFormat = "EEE, MMM d";
    public static final String firebaseFormat = "dd-MM-yyyy HH:mm:ss";

    public static boolean isEmpty(EditText editText){ return editText.getText().toString().equals(""); }
    public static boolean isEmpty(String string){ return string.equals(""); }

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
}
