package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.ActivityRVAdapter;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.module.Plan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class ActivityPage extends AppCompatActivity
        implements ActivityRVAdapter.ActivityRVClickListener,
                   View.OnClickListener
{
    // Logcat
    private static final String TAG = "ActivityPage";

    // XML Widgets
    private Toolbar Toolbar;
    private TextView DescriptionTextView;
    private ProgressBar ProgressBar;
    private RecyclerView RecyclerView;
    private FloatingActionButton FAB;

    // Vars
    private Plan Plan;
    private ArrayList<Activity> ActivityList = new ArrayList<>();
    private Set<String> ActivityListIDs = new HashSet<>();
    private ActivityRVAdapter ActivityRVAdapter;

    // Database
    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration ActivitiesEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity);

        this.setContents();
        this.getPlanIntent();
        this.getActivities();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.ui_activityToolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Other Widgets
        this.ProgressBar = findViewById(R.id.activities_progressBar);
        this.RecyclerView = findViewById(R.id.activities_recyclerview);
        this.FAB = findViewById(R.id.activities_fab);
        this.FAB.setOnClickListener(this);
        this.DescriptionTextView = findViewById(R.id.ui_activityDescription);

        // RecyclerView
        this.ActivityRVAdapter = new ActivityRVAdapter(this.ActivityList, this);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(ActivityRVAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    /************************************************************************
     * Purpose:         Parcelable Plan Interaction
     * Precondition:    .
     * Postcondition:   Save Plan Intent from MainActivity
     ************************************************************************/
    private void getPlanIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)))
        {
            this.Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
            this.getSupportActionBar().setTitle(this.Plan.getTitle());
            this.DescriptionTextView.setText(this.Plan.getDescription());
        }
        else
        {
            this.getSupportActionBar().setTitle(R.string.menu_allactivities);
        }
    }

    /************************************************************************
     * Purpose:         Parcelable Activity Interaction
     * Precondition:    .
     * Postcondition:   Send Activity Intent from MainActivity
     ************************************************************************/
    @Override
    public void onActiviySelected(int position)
    {
        /*Intent intent = new Intent(MainActivity.this, ActivityPage.class);
        intent.putExtra(getString(R.string.intent_plans), this.PlanList.get(position));
        startActivity(intent);*/
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void getActivities()
    {
        if (this.Plan != null)
        {
            CollectionReference activitiesRef = FirestoreDB
                    .collection(getString(R.string.collection_plans))
                    .document(this.Plan.getPlanID())
                    .collection(getString(R.string.collection_activities));

            ActivitiesEventListener = activitiesRef.addSnapshotListener(new EventListener<QuerySnapshot>()
            {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                    @Nullable FirebaseFirestoreException e)
                {
                    if (e != null)
                    {
                        Log.e(TAG, "getActivities: onEvent Listen failed.", e);
                        return;
                    }

                    if (documentSnapshots != null)
                    {
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                        {
                            if (doc.getType() == DocumentChange.Type.ADDED)
                            {
                                Activity activity = doc.getDocument().toObject(Activity.class);
                                ActivityList.add(activity);
                            }
                        }

                        Log.d(TAG, "getActivities: number of activities: " + ActivityList.size());
                        ActivityRVAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        else
        {

        }
    }

    private void getAllActivities()
    {
        Query activitiesRef = FirestoreDB
                .collectionGroup(getString(R.string.collection_activities))
                .whereEqualTo("ownerID", FirebaseAuth.getInstance().getCurrentUser().getUid());

        ActivitiesEventListener = activitiesRef.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                @Nullable FirebaseFirestoreException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "getActivities: onEvent Listen failed.", e);
                    return;
                }

                if(documentSnapshots != null)
                {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                    {
                        if (doc.getType() == DocumentChange.Type.ADDED)
                        {
                            Activity activity = doc.getDocument().toObject(Activity.class);
                            ActivityList.add(activity);
                        }
                    }

                    Log.d(TAG, "getActivities: number of activities: " + ActivityList.size());
                    ActivityRVAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /************************************************************************
     * Purpose:         Swipe Refresh Layout onRefresh Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    /*@Override
    public void onRefresh()
    {
        //this.getActivities();
        this.SwipeRefreshLayout.setRefreshing(false);
    }*/

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
                Intent intent = new Intent(ActivityPage.this, NewActivityPage.class);
                intent.putExtra(getString(R.string.intent_activity), this.Plan);
                startActivity(intent);
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
}
