package com.deconstructors.kronoui.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.utility.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainPage_New extends AppCompatActivity
        implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "NewActivityPage";

    //result constant for extra
    private androidx.appcompat.widget.Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private EditText DateTime;
    private FloatingActionButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_new);

        this.setContents();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.newPlan_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Other Widgets
        this.Title = findViewById(R.id.newPlan_titleEditText);
        this.Description = findViewById(R.id.newPlan_descriptionText);
        this.FAB = findViewById(R.id.newPlan_fab);
        this.FAB.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void createNewPlan()
    {
        if (!Helper.isEmpty(this.Title) && !Helper.isEmpty(this.Description))
        {
            // Set ref first to get the destination document id
            DocumentReference ref = FirebaseFirestore
                    .getInstance()
                    .collection(getString(R.string.collection_plans))
                    .document();

            Map<String, Object> activity = new HashMap<>();

            activity.put("ownerID", FirebaseAuth.getInstance().getUid());
            activity.put("planID", ref.getId());
            activity.put("title", this.Title.getText().toString());
            activity.put("description", this.Description.getText().toString());

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
            case R.id.newPlan_fab:
            {
                this.createNewPlan();
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
        Snackbar.make(findViewById(R.id.newPlan_background), string, Snackbar.LENGTH_SHORT).show();
    }
}
