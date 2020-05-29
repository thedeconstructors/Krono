package com.deconstructors.krono.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainPage_Detail extends AppCompatActivity
        implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "PlanDetailPage";

    //result constant for extra
    private Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private FloatingActionButton FAB;

    // Vars
    private Plan Plan;

    // Database
    private FirebaseFirestore FirestoreDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_detail);

        this.setContents();
        this.getPlanIntent();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.planDetail_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Other Widgets
        this.Title = findViewById(R.id.planDetail_titleEditText);
        this.Description = findViewById(R.id.planDetail_descriptionText);
        this.FAB = findViewById(R.id.planDetail_fab);
        this.FAB.setOnClickListener(this);

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    private void getPlanIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)))
        {
            this.Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
            this.getSupportActionBar().setTitle(this.Plan.getTitle());

            this.Title.setText(this.Plan.getTitle());
            this.Description.setText(this.Plan.getDescription());
        }
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void savePlan()
    {
        if (!Helper.isEmpty(this.Title) && !Helper.isEmpty(this.Description))
        {
            Map<String, Object> plan = new HashMap<>();

            plan.put(getString(R.string.plans_title), this.Title.getText().toString());
            plan.put(getString(R.string.plans_description), this.Description.getText().toString());

            FirestoreDB.collection(getString(R.string.collection_plans))
                       .document(this.Plan.getPlanID())
                       .update(plan)
                       .addOnSuccessListener(new OnSuccessListener<Void>()
                       {
                           @Override
                           public void onSuccess(Void aVoid)
                           {
                               finish();
                           }
                       });
        }
        else
        {
            makeSnackbarMessage("Please fill in all the fields");
        }
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
            case R.id.planDetail_fab:
            {
                this.savePlan();
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
        Snackbar.make(findViewById(R.id.planDetail_background), string, Snackbar.LENGTH_SHORT).show();
    }
}
