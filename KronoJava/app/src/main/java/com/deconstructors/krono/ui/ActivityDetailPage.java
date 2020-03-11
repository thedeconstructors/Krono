package com.deconstructors.krono.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.module.Plan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityDetailPage extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "NewActivityPage";

    //result constant for extra
    private androidx.appcompat.widget.Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private EditText DateTime;
    private FloatingActionButton FAB;

    // Vars
    private Plan Plan;
    private Activity Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_detail);

        this.setContents();
        this.getPlanIntent();
        this.getActivityIntent();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.activitydetail_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Other Widgets
        this.Title = findViewById(R.id.activitydetail_titleEditText);
        this.Description = findViewById(R.id.activitydetail_descriptionText);
        this.DateTime = findViewById(R.id.activitydetail_dueDateEditText);
        this.FAB = findViewById(R.id.activitydetail_fab);
        this.FAB.setOnClickListener(this);
    }

    private void getPlanIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)))
        {
            this.Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
        }
    }

    private void getActivityIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_activity)))
        {
            this.Activity = getIntent().getParcelableExtra(getString(R.string.intent_activity));
            this.getSupportActionBar().setTitle(this.Activity.getTitle());

            this.Title.setText(this.Activity.getTitle());
            this.Description.setText(this.Activity.getDescription());
            if (this.Activity.getTimestamp() != null)
            {
                this.DateTime.setText(this.Activity.getTimestamp().toDate().toString());
            }
        }
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void saveActivity()
    {

    }

    /************************************************************************
     * Purpose:         Floating Action Button onClick Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.activities_fab:
            {
                this.saveActivity();
                this.finish();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Back Button Animation Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /************************************************************************
     * Purpose:         Toolbar Menu Inflater
     * Precondition:    .
     * Postcondition:   Activates the toolbar menu by inflating it
     *                  See more from res/menu/activity_boolbar_menu
     *                  and layout/menu0_toolbar
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activitydetail, menu);

        return true;
    }
}
