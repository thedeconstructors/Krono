package com.deconstructors.krono.activities.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.SessionData;
import com.deconstructors.krono.helpers.SwipeController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Menu0_Activities
        extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener
{
    // Error Handler Log Search
    private static final String _Tag = "Krono_Menu0_Log";
    private static final String _dbPath = "users";

    // Variables
    private List<Activity> _ActivityList = new ArrayList<>();
    private ActivityRVAdapter _ActivityRVAdapter;
    private DocumentSnapshot _LastQueriedList;

    // XML Widgets
    private RecyclerView _RecyclerView;
    private Toolbar _ActivityToolbar;
    private SwipeController swipeController;
    private ItemTouchHelper itemTouchhelper;
    private SwipeRefreshLayout _SwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu0_activities);
        _RecyclerView = findViewById(R.id.MainMenu_ActivityListID);
        _ActivityToolbar = findViewById(R.id.menu0_toolbar);
        _SwipeRefreshLayout = findViewById(R.id.MainMenu_SwipeRefreshLayoutID);
        _SwipeRefreshLayout.setOnRefreshListener(this);

        setUpRecyclerView();
        setupToolbar();
        getActivities();
    }

    /***********************************************************************
     * Purpose:         Setup the RecyclerView
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setUpRecyclerView()
    {
        if (_ActivityRVAdapter == null)
        {
            _ActivityRVAdapter = new ActivityRVAdapter(_ActivityList);
        }

        _RecyclerView.setHasFixedSize(true);
        _RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        _RecyclerView.setAdapter(_ActivityRVAdapter);

        // Touch Control
        swipeController = new SwipeController();
        itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(_RecyclerView);
    }

    /***********************************************************************
     * Purpose:         Setup the Toolbar
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setupToolbar()
    {
        setSupportActionBar(_ActivityToolbar);
        getSupportActionBar().setTitle("Activities Menu");
        // Toolbar Back Button
        // Toolbar doesn't need a button click event because of this
        // AndroidMenifest.xml -> Set Parent Activity
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /***********************************************************************
     * Purpose:         Get Activities
     * Precondition:    .
     * Postcondition:   Retrieve Activities from the database
     *                  Grabs current user, and then their activities
     *
     ************************************************************************/
    private void getActivities()
    {
        /* Query The Database */
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(SessionData.GetInstance().GetUserID())
                .collection("activities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        /* Grab the iterator from the built in google library */
                        Iterator<QueryDocumentSnapshot> iterator = queryDocumentSnapshots.iterator();

                        /* Reset the activity list view */
                        if (!_ActivityList.isEmpty()) {
                            _ActivityList.clear();
                        }

                        /* Populate the list of activites using an iterator */
                        while (iterator.hasNext())
                        {
                            _ActivityList.add(iterator.next().toObject(Activity.class));
                        }

                        /* Notify the adapter that the data has changed */
                        _ActivityRVAdapter.notifyDataSetChanged();
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
        });

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
        int x = _ActivityRVAdapter.getItemCount();

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
        _SwipeRefreshLayout.setRefreshing(false);
    }
}