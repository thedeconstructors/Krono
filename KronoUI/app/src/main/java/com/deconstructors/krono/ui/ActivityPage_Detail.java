package com.deconstructors.krono.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityPage_Detail extends AppCompatActivity implements View.OnClickListener
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
    private Activity Activity;

    // Database
    private FirebaseFirestore FirestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.setContents();
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

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    private void getActivityIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_activity)))
        {
            this.Activity = getIntent().getParcelableExtra(getString(R.string.intent_activity));
            this.getSupportActionBar().setTitle(this.Activity.getTitle());

            this.Title.setText(this.Activity.getTitle());
            this.Description.setText(this.Activity.getDescription());
            if (this.Activity.getDuration() != null)
            {
                this.DateTime.setText(this.Activity.getDuration().toString());
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
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                finish();
                            }
                            else
                            {
                                Log.d(TAG, "Failed to delete");
                            }
                        }
                    });

            /*FirestoreDB.collection(getString(R.string.collection_plans))
                       .document(this.Activity.getPlanID())
                       .collection(getString(R.string.collection_activities))
                       .document(this.Activity.getActivityID())
                       .update(activity)
            .addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    finish();
                }
            });*/
        }
        else
        {
            makeSnackbarMessage("Please fill in all the fields");
        }
    }

    private void deleteActivity()
    {
        FirestoreDB.collection(getString(R.string.collection_activities))
                .document(this.Activity.getActivityID())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            finish();
                        }
                        else
                        {
                            Log.d(TAG, "Failed to delete");
                        }
                    }
                });
        /*FirestoreDB.collection(getString(R.string.collection_plans))
                   .document(this.Activity.getPlanID())
                   .collection(getString(R.string.collection_activities))
                   .document(this.Activity.getActivityID())
                   .delete()
                   .addOnSuccessListener(new OnSuccessListener<Void>()
                   {
                       @Override
                       public void onSuccess(Void aVoid)
                       {
                           finish();
                       }
                   });*/
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
            case R.id.activitydetail_fab:
            {
                this.saveActivity();
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
