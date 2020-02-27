package com.demo.planactivityuserdemo.utility;

import android.widget.EditText;

public class Helper
{
    public static boolean isEmpty(EditText editText){ return editText.getText().toString().equals(""); }
    public static boolean isEmpty(String string){ return string.equals(""); }

}
