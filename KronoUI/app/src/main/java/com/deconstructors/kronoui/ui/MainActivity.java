package com.deconstructors.kronoui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.adapter.PlanAdapter;
import com.deconstructors.kronoui.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements PlanAdapter.PlanClickListener,
                   View.OnClickListener
{
    // Error Log
    private static final String TAG = "MainActivity";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView PlanRecyclerView;
    private FloatingActionButton FAB;
    private TextView NameTextView;
    private TextView EmailTextView;

    // Database
    private FirebaseFirestore DBInstance;
    private Query PlanQuery;
    private FirestoreRecyclerOptions<Plan> PlanOptions;
    private PlanAdapter PlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_main);

        this.setDatabase();
        this.setContents();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.ui_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.NameTextView = findViewById(R.id.ui_main_displayName);
        this.EmailTextView = findViewById(R.id.ui_main_email);

        // Recycler View
        this.PlanRecyclerView = findViewById(R.id.ui_main_recyclerview);
        this.PlanRecyclerView.setHasFixedSize(true);
        this.PlanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.PlanRecyclerView.setAdapter(this.PlanAdapter);

        // Other XML Widgets
        this.FAB = findViewById(R.id.ui_main_fab);
        this.FAB.setOnClickListener(this);
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
        this.PlanQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID", FirebaseAuth.getInstance().getCurrentUser().getUid());;
        this.PlanOptions = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(this.PlanQuery, Plan.class)
                .build();
        this.PlanAdapter = new PlanAdapter(this.PlanOptions, this);

        // User
        this.DBInstance
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
                    {
                        if (documentSnapshot != null)
                        {
                            NameTextView.setText(documentSnapshot.get("displayName").toString());
                            EmailTextView.setText(documentSnapshot.get("email").toString());
                        }
                    }
                });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.PlanAdapter != null) { this.PlanAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.PlanAdapter != null) { this.PlanAdapter.stopListening(); }
    }

    /************************************************************************
     * Purpose:         Parcelable Plan Interaction
     * Precondition:    .
     * Postcondition:   Send Plan Intent from MainActivity
     ************************************************************************/
    @Override
    public void onPlanSelected(int position)
    {
        Intent intent = new Intent(MainActivity.this, ActivityPage.class);
        intent.putExtra(getString(R.string.intent_plans), this.PlanAdapter.getItem(position));
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Main Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ui_menu_allActivities:
            {
                Intent intent = new Intent(MainActivity.this, ActivityPage_All.class);
                startActivity(intent);
                break;
            }
            case R.id.ui_menu_friends:
            {
                Intent intent = new Intent(MainActivity.this, FriendPage.class);
                startActivity(intent);
                break;
            }
            case R.id.ui_menu_chat:
            {
                break;
            }
            case R.id.ui_main_fab:
            {
                Intent intent = new Intent(MainActivity.this, PlanPage_New.class);
                startActivity(intent);
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Menu Inflater
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }
}
