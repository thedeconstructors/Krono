package com.deconstructors.krono.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.widget.Toolbar;
import java.util.HashMap;
import java.util.Map;

public class ActivityPage_Detail extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "NewActivityPage";

    //result constant for extra
    private Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private EditText DateTime;
    private FloatingActionButton FAB_Save;
    private FloatingActionButton FAB_Delete;

    // Vars
    private Activity Activity;
    private ActivityPage.EditMode Editable;

    // Database
    private FirebaseFirestore FirestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.setContents();
        this.getActivityIntent();
        this.checkPermissions();
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
        this.FAB_Save = findViewById(R.id.activitydetail_fab_save);
        this.FAB_Save.setOnClickListener(this);
        this.FAB_Delete = findViewById(R.id.activitydetail_fab_delete);
        this.FAB_Delete.setOnClickListener(this);

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    private void getActivityIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_activity)) && getIntent().hasExtra(getString(R.string.intent_editable)))
        {
            this.Activity = getIntent().getParcelableExtra(getString(R.string.intent_activity));
            this.Editable = (ActivityPage.EditMode) getIntent().getSerializableExtra(getString(R.string.intent_editable));
            this.getSupportActionBar().setTitle(this.Activity.getTitle());

            this.Title.setText(this.Activity.getTitle());
            this.Description.setText(this.Activity.getDescription());
            if (this.Activity.getDuration() != null)
            {
                this.DateTime.setText(this.Activity.getDuration().toString());
            }
        }
        else
        {
            finish();
        }
    }

    private void checkPermissions()
    {
        if (Editable == ActivityPage.EditMode.PUBLIC)
        {
            //disable textboxes
            this.Title.setEnabled(false);
            this.Description.setEnabled(false);
            this.DateTime.setEnabled(false);

            //hide buttons
            this.FAB_Save.setVisibility(View.GONE);
            this.FAB_Delete.setVisibility(View.GONE);

            //change title
            ((TextView)findViewById(R.id.activitydetail_editActivityHeader)).setText(getString(R.string.activitydetail_headerViewOnly));
        }
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void saveActivity()
    {
        if (Editable == ActivityPage.EditMode.PUBLIC)
            return;

        boolean duration_valid = false;
        Integer activity_duration = 0;
        try
        {
            activity_duration = Integer.parseInt(this.DateTime.getText().toString());
            if (activity_duration > 0)
                duration_valid = true;
        }
        catch (NumberFormatException e)
        {}

        if ( duration_valid
                && !Helper.isEmpty(this.Title)
                && !Helper.isEmpty(this.Description)
                && !Helper.isEmpty(this.DateTime))
        {
            Map<String, Object> activity = new HashMap<>();

            activity.put("ActivityID", this.Activity.getActivityID());
            activity.put("Title", this.Title.getText().toString());
            activity.put("Description", this.Description.getText().toString());
            activity.put("Duration", activity_duration);

            FirestoreDB.collection(getString(R.string.collection_activities))
                    .document(this.Activity.getActivityID())
                    .update(activity)
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                finish();
                            }
                            else
                            {
                                Log.d(TAG, "Failed to save");
                            }
                        }
                    });
        }
        else
        {
            makeSnackbarMessage("Please fill in all the fields");
        }
    }

    private void deleteActivity()
    {
        if (Editable == ActivityPage.EditMode.PUBLIC) {
            Toast.makeText(this, "This plan is not editable", Toast.LENGTH_SHORT).show();
            return;
        }
        // This should be done in Firebase Functions and not fully dependant on the user side
        // It's like we have subcollections called plans inside activities.
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
            case R.id.activitydetail_fab_save:
            {
                this.saveActivity();
                break;
            }
            case R.id.activitydetail_fab_delete:
            {
                this.deleteActivity();
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
            // Back Button Animation Override
            case android.R.id.home:
            {
                finish();
                break;
            }
            case R.id.activitydetail_menu_deleteActivity:
            {
                this.deleteActivity();
                break;
            }
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
        inflater.inflate(R.menu.menu_activity_detail, menu);

        return true;
    }

    /************************************************************************
     * Purpose:         Utilities
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void makeSnackbarMessage(String string)
    {
        Snackbar.make(findViewById(R.id.activitydetail_background), string, Snackbar.LENGTH_SHORT).show();
    }
}
