package com.deconstructors.krono.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationsPage extends AppCompatActivity
{
    // Error Log
    private static final String TAG = "NotificationsPage";

    // XML Widgets
    private androidx.appcompat.widget.Toolbar Toolbar;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_main);

        this.RecyclerView = findViewById(R.id.notif_recyclerview);
        setToolbar();
    }

    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.notif_toolbar);
        this.Toolbar.setTitle("Notifications");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void onClick(View view)
    {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

        //Switch statement for if we add more than one button on the notifications page.
        switch(view.getId())
        {
            case R.id.btn_notifTest:
            {
                Notification newNotif = new Notification("Test", timestamp, "This is a test notification");
                break;
            }
        }
    }
}
