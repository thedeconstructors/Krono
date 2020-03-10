package com.deconstructors.krono.utility;

import android.widget.EditText;

import com.deconstructors.krono.module.Plan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Helper
{
    public static boolean isEmpty(EditText editText){ return editText.getText().toString().equals(""); }
    public static boolean isEmpty(String string){ return string.equals(""); }

    public static Plan getPlan(List<Plan> list, String id)
    {
        for (Plan plan : list)
        {
            if (plan.getPlanID() == id)
            {
                return plan;
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
