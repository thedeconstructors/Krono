package com.deconstructors.kronoui.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.module.Plan;
import com.deconstructors.kronoui.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityPage_New extends AppCompatActivity implements View.OnClickListener,
                                                                   DatePickerDialog.OnDateSetListener
{
    // Error Log
    private static final String TAG = "NewActivityPage";

    //result constant for extra
    private androidx.appcompat.widget.Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private TextView DateTime;
    private TextView Location;
    private FloatingActionButton FAB;

    // Vars
    private Plan Plan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        this.setContents();
        this.getPlanIntent();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.newactivity_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Other Widgets
        this.Title = findViewById(R.id.newactivity_titleEditText);
        this.Description = findViewById(R.id.newactivity_descriptionText);
        this.DateTime = findViewById(R.id.newactivity_dueDateEditText);
        //this.DateTime.setOnTouchListener(this);
        this.DateTime.setOnClickListener(this);
        this.FAB = findViewById(R.id.newactivity_fab);
        this.FAB.setOnClickListener(this);
        this.Location = findViewById(R.id.newactivity_addLocEditText);
        this.Location.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Parcelable Plan Interaction
     * Precondition:    .
     * Postcondition:   Get Plan Intent from MainActivity
     ************************************************************************/
    private void getPlanIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)))
        {
            this.Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
            this.getSupportActionBar().setTitle(this.Plan.getTitle());
        }
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void createNewActivity()
    {
        if (!Helper.isEmpty(this.Title)
                && !Helper.isEmpty(this.Description)
                && !this.DateTime.getText().toString().equals(""))
        {
            // Set ref first to get the destination document id
            DocumentReference ref = FirebaseFirestore
                    .getInstance()
                    .collection(getString(R.string.collection_plans))
                    .document(Plan.getPlanID())
                    .collection(getString(R.string.collection_activities))
                    .document();

            Map<String, Object> activity = new HashMap<>();

            activity.put("ownerID", FirebaseAuth.getInstance().getUid());
            activity.put("planID", this.Plan.getPlanID());
            activity.put("activityID", ref.getId());
            activity.put("title", this.Title.getText().toString());
            activity.put("description", this.Description.getText().toString());
            activity.put("timestamp", this.DateTime.getText().toString());

            ref.set(activity).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    makeSnackbarMessage("Error: Could Not Add Activity");
                }
            });
        }
        else
        {
            makeSnackbarMessage("Must Enter All Text Fields");
        }
    }

    /************************************************************************
     * Purpose:         Click Events
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.newactivity_fab:
            {
                this.createNewActivity();
                break;
            }
            case R.id.newactivity_dueDateEditText:
            {
                this.showDatePicker();
                break;
            }
            case R.id.newactivity_addLocEditText:
            {
                Intent intent = new Intent(ActivityPage_New.this, ActivityPage_Map.class);
                startActivity(intent);
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Back Button Override for animation
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
     * Purpose:         Date Picker
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void showDatePicker()
    {
        DatePickerDialog dpd = new DatePickerDialog(
                ActivityPage_New.this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Helper.displayDateFormat, Locale.getDefault());
        Calendar.getInstance().set(year, month, dayOfMonth);
        String dateString = sdf.format(Calendar.getInstance().getTime());
        this.DateTime.setText(dateString);
    }

    /************************************************************************
     * Purpose:         Utilities
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void makeSnackbarMessage(String string)
    {
        Snackbar.make(findViewById(R.id.newactivity_background), string, Snackbar.LENGTH_SHORT).show();
    }
}
