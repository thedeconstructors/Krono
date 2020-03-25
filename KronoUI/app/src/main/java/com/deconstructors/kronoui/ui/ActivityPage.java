package com.deconstructors.kronoui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.adapter.ActivityAdapter;
import com.deconstructors.kronoui.module.Activity;
import com.deconstructors.kronoui.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityPage extends AppCompatActivity
        implements ActivityAdapter.ActivityClickListener,
                   View.OnClickListener
{
    // Logcat
    private static final String TAG = "ActivityPage";

    // XML Widgets
    private Toolbar Toolbar;
    private TextView ToolbarDescription;
    private RecyclerView RecyclerView;
    private FloatingActionButton FAB;

    // Database
    private Plan Plan;
    private FirebaseFirestore DBInstance;
    private Query ActivityQuery;
    private FirestoreRecyclerOptions<Activity> ActivityOptions;
    private ActivityAdapter ActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setToolbar();
        this.checkIntent();
        this.setDatabase();
        this.setContents();
    }

    /************************************************************************
     * Purpose:         Set Toolbar & Inflate Toolbar Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.ActivityPage_Toolbar);
        this.ToolbarDescription = findViewById(R.id.ActivityPage_Description);
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);

        return true;
    }

    /************************************************************************
     * Purpose:         Parcelable Plan Interaction
     * Precondition:    .
     * Postcondition:   Get Plan Intent from MainActivity
     ************************************************************************/
    private void checkIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)))
        {
            this.Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
            this.getSupportActionBar().setTitle(this.Plan.getTitle());
            this.ToolbarDescription.setText(this.Plan.getDescription());
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Plan not found");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();

            finish();
        }
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setDatabase()
    {
        // Plan
        this.DBInstance = FirebaseFirestore.getInstance();
        this.ActivityQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .document(this.Plan.getPlanID())
                .collection(getString(R.string.collection_activities));
        this.ActivityOptions = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(this.ActivityQuery, Activity.class)
                .build();
        this.ActivityAdapter = new ActivityAdapter(this.ActivityOptions, this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.ActivityAdapter != null) { this.ActivityAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.ActivityAdapter != null) { this.ActivityAdapter.stopListening(); }
    }

    private void deletePlan()
    {
        this.DBInstance
                .collection(getString(R.string.collection_plans))
                .document(this.Plan.getPlanID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        finish();
                    }
                });
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // RecyclerView
        this.RecyclerView = findViewById(R.id.ActivityPage_RecyclerView);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.RecyclerView.setAdapter(this.ActivityAdapter);

        // Other Widgets
        this.FAB = findViewById(R.id.ActivityPage_FAB);
        this.FAB.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Parcelable Plan & Activity Interaction
     * Precondition:    .
     * Postcondition:   Go to another page
     ************************************************************************/
    @Override
    public void onActiviySelected(int position)
    {
        Intent intent = new Intent(ActivityPage.this, ActivityPage_Detail.class);
        intent.putExtra(getString(R.string.intent_activity), this.ActivityAdapter.getItem(position));
        startActivity(intent);
    }

    private void editPlan()
    {
        Intent intent = new Intent(ActivityPage.this, PlanPage_Detail.class);
        intent.putExtra(getString(R.string.intent_plans), this.Plan);
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Click Listener
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ActivityPage_FAB:
            {
                Intent intent = new Intent(ActivityPage.this, ActivityPage_New.class);
                intent.putExtra(getString(R.string.intent_plans), this.Plan);
                startActivity(intent);
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Menu Selection
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
            case R.id.activity_menu_sortBy:
            {
                break;
            }
            case R.id.activity_menu_editPlan:
            {
                this.editPlan();
                break;
            }
            case R.id.activity_menu_deletePlan:
            {
                this.deletePlan();
                break;
            }
        }
        return true;
    }
}
