package com.deconstructors.kronoui.ui;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewActivityPage extends AppCompatActivity
        implements View.OnClickListener, android.app.DatePickerDialog.OnDateSetListener {
    // Error Log
    private static final String TAG = "NewActivityPage";

    //result constant for extra
    private androidx.appcompat.widget.Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private TextView DateTime;
    private FloatingActionButton FAB;
    private DatePicker DatePicker;

    // Vars
    private DatePickerDialog DatePickerDialog;
    private Calendar Calendar;
    private Plan Plan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_new);

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
        this.DateTime.setOnClickListener(this);
        this.FAB = findViewById(R.id.newactivity_fab);
        this.FAB.setOnClickListener(this);

        //
        this.Calendar = java.util.Calendar.getInstance();
        this.DatePickerDialog.setOnDateSetListener(this);
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
            activity.put("activityID", ref.getId());
            activity.put("title", this.Title.getText().toString());
            activity.put("description", this.Description.getText().toString());
            activity.put("timestamp", Helper.getDateFromString(this.DateTime.getText().toString()));

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
                this.setDatePicker();
                break;
            }
        }
    }

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

    private void setDatePicker()
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        this.DatePickerDialog = new DatePickerDialog(
                NewActivityPage.this,
                R.style.DialogTheme,
                this.DatePickerListener,
                year, month, day);

        this.DatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.DatePickerDialog.show();

        this.DatePickerListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy");
                c.set(year, month, dayOfMonth);
                String dateString = sdf.format(c.getTime());
                DateTime.setText(dateString);
            }
        };
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy");
        c.set(year, month, dayOfMonth);
        String dateString = sdf.format(c.getTime());
        DateTime.setText(dateString);
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
