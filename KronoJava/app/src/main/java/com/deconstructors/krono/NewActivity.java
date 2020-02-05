package com.deconstructors.krono;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class NewActivity extends AppCompatActivity
{
    public static final String USER_NAME = "com.deconstructors.krono.USER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu0_activities_newactivity);
    }
}
