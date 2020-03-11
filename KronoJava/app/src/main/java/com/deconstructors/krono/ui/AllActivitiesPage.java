package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.ActivityRVAdapter;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.utility.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllActivitiesPage extends AppCompatActivity
        implements ActivityRVAdapter.ActivityRVClickListener,
                   View.OnClickListener
{
    // Error Log
    private static final String TAG = "MainActivity";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView AllActivitiesRecyclerView;
    private FloatingActionButton FAB;

    // Database
    private FirebaseFirestore FirestoreDB;
    private List<Activity> AllActivitiesList;
    private ActivityRVAdapter AllActivitiesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_allactivities);

        this.setContents();
        this.getAllActivities();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.allactivities_toolbar);
        this.Toolbar.setTitle(getString(R.string.menu_allactivities));
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Recycler View
        this.AllActivitiesList = new ArrayList<>();
        this.AllActivitiesAdapter = new ActivityRVAdapter(AllActivitiesList, this);
        this.AllActivitiesRecyclerView = findViewById(R.id.allactivities_recyclerview);
        this.AllActivitiesRecyclerView.setHasFixedSize(true);
        this.AllActivitiesRecyclerView.setAdapter(this.AllActivitiesAdapter);
        this.AllActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Others Widgets
        this.FAB = findViewById(R.id.allactivities_fab);
        this.FAB.setOnClickListener(this);

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void getAllActivities()
    {
        FirestoreDB.collectionGroup(getString(R.string.collection_activities))
                   .whereEqualTo("ownerID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                   .addSnapshotListener(new EventListener<QuerySnapshot>()
                   {
                       @Override
                       public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                           @Nullable FirebaseFirestoreException e)
                       {
                           if (e != null)
                           {
                               Log.d(TAG,"getAllActivities: onEvent Listen failed.", e);
                               return;
                           }

                           for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                           {
                               if (doc.getType() == DocumentChange.Type.ADDED)
                               {
                                   Activity activity = doc.getDocument().toObject(Activity.class);
                                   AllActivitiesList.add(activity);
                               }
                               else if (doc.getType() == DocumentChange.Type.MODIFIED)
                               {
                                   Activity activity = doc.getDocument().toObject(Activity.class);
                                   AllActivitiesList.remove(Helper.getActivity(AllActivitiesList, activity.getActivityID()));
                                   AllActivitiesList.add(activity);
                               }
                               else if (doc.getType() == DocumentChange.Type.REMOVED)
                               {
                                   Activity activity = doc.getDocument().toObject(Activity.class);
                                   AllActivitiesList.remove(Helper.getActivity(AllActivitiesList, activity.getActivityID()));
                               }
                           }

                           Log.d(TAG, "getAllActivities: number of activities: " + AllActivitiesList.size());
                           AllActivitiesAdapter.notifyDataSetChanged();
                       }
                   });
    }

    /************************************************************************
     * Purpose:         Parcelable Activity Interaction
     * Precondition:    .
     * Postcondition:   Send Activity Intent to ActivityDetailPage
     ************************************************************************/
    @Override
    public void onActiviySelected(int position)
    {
        Intent intent = new Intent(AllActivitiesPage.this, ActivityDetailPage.class);
        intent.putExtra(getString(R.string.intent_activity), this.AllActivitiesList.get(position));
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Floating Action Button onClick Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View v)
    {

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
        inflater.inflate(R.menu.menu_allactivities, menu);

        return true;
    }
}
