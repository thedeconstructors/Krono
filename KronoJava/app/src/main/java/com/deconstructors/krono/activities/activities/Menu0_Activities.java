package com.deconstructors.krono.activities.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.adapter.ActivityRVAdapter;
import com.deconstructors.krono.module.Activity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Menu0_Activities
        extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, com.deconstructors.krono.adapter.ActivityRVAdapter.ActivityRVClickListener, View.OnClickListener
{
    // Error Handler Log Search
    private static final String TAG = "ActivityActivity";

    // Views
    private ProgressBar ProgressBar;
    private RecyclerView RecyclerView;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private FloatingActionButton FAB;

    // Vars
    private Plan Plan;
    private List<Activity> ActivityList = new ArrayList<>();
    private Set<String> ActivityListIDs = new HashSet<>();
    private DocumentSnapshot _LastQueriedList;
    private ActivityRVAdapter ActivityRVAdapter;

    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration ActivitiesEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity);

        setContentViews();
        setUpRecyclerView();
        getPlanIntent();
        getActivities();
    }

    private void setContentViews()
    {
        this.ProgressBar = findViewById(R.id.activity_progressBar);
        this.RecyclerView = findViewById(R.id.activity_recyclerview);
        this.SwipeRefreshLayout = findViewById(R.id.activity_refreshlayout);
        this.SwipeRefreshLayout.setOnRefreshListener(this);
        this.FAB = findViewById(R.id.activity_fab);
        this.FAB.setOnClickListener(this);

        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    private void setUpRecyclerView()
    {
        this.ActivityRVAdapter = new ActivityRVAdapter(this.ActivityList);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(ActivityRVAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getPlanIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)))
        {
            Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
        }
    }

    /***********************************************************************
     * Purpose:         Setup the Toolbar
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setupToolbar()
    {
        //setSupportActionBar(_ActivityToolbar);
        //getSupportActionBar().setTitle("Activities Menu");
    }

    /***********************************************************************
     * Purpose:         Get Activities
     * Precondition:    .
     * Postcondition:   Retrieve Activities from the database
     *                  Grabs current user, and then their activities
     *
     ************************************************************************/
    @Override
    public void onResume()
    {
        super.onResume();
        getActivities();
    }

    private void getActivities()
    {
        /* Query The Database */
        FirebaseFirestore.getInstance()
                .collection("Plans")
                .document(this.Plan.getPlanID())
                .collection("Activities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        /* Grab the iterator from the built in google library */
                        Iterator<QueryDocumentSnapshot> iterator = queryDocumentSnapshots.iterator();

                        boolean x = queryDocumentSnapshots.isEmpty();

                        /* Reset the activity list view */
                        if (!ActivityList.isEmpty()) {
                            ActivityList.clear();
                        }

                        /* Populate the list of activities using an iterator */
                        while (iterator.hasNext())
                        {
                            QueryDocumentSnapshot snapshot = iterator.next();

                            Activity activity = snapshot.toObject(Activity.class);
                            activity.setId(snapshot.getId());
                            ActivityList.add(activity);
                        }

                        /* Notify the adapter that the data has changed */
                        ActivityRVAdapter.notifyDataSetChanged();
                    }
                });
    }

    /******************************* XML ***********************************/
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
        inflater.inflate(R.menu.activity_toolbar_menu, menu);

        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_toolbar_menu, menu);

        // Search Button
        MenuItem searchItem = menu.findItem(R.id.activity_toolbar_searchbutton);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                _ActivityRVAdapter.getFilter().filter(newText);
                _ActivityRVAdapter.resetFilterList();
                return false;
            }
        });*/

        return true;
    }

    /************************************************************************
     * Purpose:         Toolbar Menu Options - Item Selected
     * Precondition:    .
     * Postcondition:   See more from res/menu/activity_boolbar_menu
     *                   and layout/menu0_toolbar
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int x = ActivityRVAdapter.getItemCount();

        switch (item.getItemId())
        {
            case R.id.activity_toolbar_sortbybutton:
                Toast.makeText(this, "Sort By button selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.activity_toolbar_simpleview:
                Toast.makeText(this, "Item Count: " + x, Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /************************************************************************
     * Purpose:         New Activity Button Click Event
     * Precondition:    The Floating Action Button Clicked
     * Postcondition:   Start NewActivity Intent
     ************************************************************************/
    public void btnNewActivity(View view)
    {
        Intent intent = new Intent(this, NewActivity.class);
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         on Recycler View Refresh
     * Precondition:    Swipe Up SwipeRefreshLayout Containing RecyclerView
     * Postcondition:   get Activities Manually
     ************************************************************************/
    @Override
    public void onRefresh()
    {
        this.getActivities();
        this.SwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActiviySelected(int position)
    {
        // on click
    }

    @Override
    public void onClick(View v)
    {

    }
}