package com.deconstructors.krono.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewActivityPage extends AppCompatActivity
        implements View.OnClickListener
{
    //result constant for extra
    private Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private EditText DateTime;
    private FloatingActionButton FAB;

    // Vars
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
        this.FAB = findViewById(R.id.newactivity_fab);
        this.FAB.setOnClickListener(this);
    }

    private void getPlanIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_activity)))
        {
            this.Plan = getIntent().getParcelableExtra(getString(R.string.intent_activity));
            this.getSupportActionBar().setTitle(this.Plan.getTitle());
            //this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createNewActivity()
    {
        if (!Helper.isEmpty(this.Title)
                && !Helper.isEmpty(this.Description)
                && !Helper.isEmpty(this.DateTime))
        {
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
