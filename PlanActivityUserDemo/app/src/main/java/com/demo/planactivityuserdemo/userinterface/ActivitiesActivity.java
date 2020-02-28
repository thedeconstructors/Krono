package com.demo.planactivityuserdemo.userinterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import com.demo.planactivityuserdemo.R;
import com.demo.planactivityuserdemo.adapter.ActivitiesRecyclerAdapter;
import com.demo.planactivityuserdemo.model.Activity;
import com.demo.planactivityuserdemo.model.Plan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class ActivitiesActivity extends AppCompatActivity
        implements View.OnClickListener,
                   ActivitiesRecyclerAdapter.ActivitiesRecyclerClickListener,
                   SwipeRefreshLayout.OnRefreshListener
{
    // Logcat
    private static final String TAG = "ActivitiesActivity";

    // Views
    private ProgressBar ProgressBar;
    private RecyclerView RecyclerView;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private FloatingActionButton FAB;

    // Vars
    private Plan Plan;
    private ArrayList<Activity> ActivityList = new ArrayList<>();
    private Set<String> ActivityListIDs = new HashSet<>();
    private ActivitiesRecyclerAdapter RecyclerAdapter;
    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration ActivitiesEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activities);

        setContentViews();
        setRecyclerView();
        setPlanIntent();
    }

    /************************************************************************
     * Purpose:         Content Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContentViews()
    {
        this.ProgressBar = findViewById(R.id.activities_progressBar);
        this.RecyclerView = findViewById(R.id.activities_recyclerview);
        this.SwipeRefreshLayout = findViewById(R.id.activities_refreshlayout);
        this.SwipeRefreshLayout.setOnRefreshListener(this);
        this.FAB = findViewById(R.id.activities_fab);
        this.FAB.setOnClickListener(this);

        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    private void setRecyclerView()
    {
        this.RecyclerAdapter = new ActivitiesRecyclerAdapter(this.ActivityList, this);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(RecyclerAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setPlanIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)))
        {
            Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
            setToolbar();
        }
    }

    private void setToolbar()
    {
        getSupportActionBar().setTitle(this.Plan.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /************************************************************************
     * Purpose:         OnClickListener & Database
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
                //
            }
        }
    }

    private void getActivities()
    {
        CollectionReference activitiesRef = FirestoreDB
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_plans))
                .document(Plan.getPlanID())
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

                if(documentSnapshots != null)
                    {
                        for (QueryDocumentSnapshot doc : documentSnapshots)
                        {
                            Activity activity = doc.toObject(Activity.class);

                            if(!ActivityListIDs.contains(activity.getActivityID()))
                            {
                                ActivityListIDs.add(activity.getActivityID());
                                ActivityList.add(activity);
                            }
                        }

                    Log.d(TAG, "getActivities: number of activities: " + ActivityList.size());
                    RecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /************************************************************************
     * Purpose:         ActivitiesRecyclerAdapter Override
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onActiviySelected(int position)
    {
        navActivitiesActivity(this.ActivityList.get(position));
    }

    private void navActivitiesActivity(Activity activity)
    {
        //Intent intent = new Intent(ActivitiesActivity.this, ActivitiesActivity.class);
        //intent.putExtra(getString(R.string.intent_plans), plan);
        //startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Toolbar & Menu Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_activities, menu);
        return true;
    }

    /************************************************************************
     * Purpose:         Other Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    protected void onResume()
    {
        super.onResume();
        getActivities();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(this.ActivitiesEventListener != null)
        {
            this.ActivitiesEventListener.remove();
        }
    }

    @Override
    public void onRefresh()
    {
        getActivities();
        SwipeRefreshLayout.setRefreshing(false);
    }
}
