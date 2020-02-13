package com.deconstructors.krono.activities.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.SwipeController;
import com.deconstructors.structures.Activity;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Menu0_Activities extends AppCompatActivity
{
    // Error Handler Log Search
    private static final String m_Tag = "Krono_Firebase_Log";

    // Database
    private FirebaseFirestore m_Firestore;

    // List of Activity (Class) -> Activity List Adapter -> Recycler View (XML)
    private RecyclerView _RecyclerView;
    private ActivityListAdapter _ActivityListAdapter;
    private List<Activity> _ActivityList;

    //Swipe Controller
    SwipeController swipeController = new SwipeController();
    ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu0_activities);

        // List of Activity (Class) -> Activity List Adapter -> Recycler View (XML)
        _ActivityList = new ArrayList<>();
        _ActivityListAdapter = new ActivityListAdapter(_ActivityList);

        _RecyclerView = (RecyclerView)findViewById(R.id.MainMenu_ActivityListID);
        _RecyclerView.setHasFixedSize(true);
        _RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        _RecyclerView.setAdapter(_ActivityListAdapter);
        itemTouchhelper.attachToRecyclerView(_RecyclerView);

        // Database Listener
        m_Firestore = FirebaseFirestore.getInstance();
        m_Firestore.collection("useractivities").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            /************************************************************************
             * Purpose:         On Event
             * Precondition:    An Item has been added
             * Postcondition:   Change the Activity list accordingly and
             *                  Notify the adapter
             *                  This way, we don't have to scan DB every time
             ************************************************************************/
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if (e != null)
                {
                    Log.d(m_Tag, "Error : " + e.getMessage());
                }
                else
                {
                    // Document contains data read from a document in your Firestore database
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                    {
                        if (doc.getType() == DocumentChange.Type.ADDED)
                        {
                            // Arrange the data according to the model class
                            // All by itself
                            Activity activity = doc.getDocument().toObject(Activity.class);
                            _ActivityList.add(activity);

                            // Notify the Adapter something is changed
                            _ActivityListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        // Toolbar Integration
        Toolbar toolbar = findViewById(R.id.menu0_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Activities Menu");

        // Doesn't need a button click even because
        // AndroidMenifest.xml -> Set Parent Activity
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                _ActivityListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    /************************************************************************
     * Purpose:         Options Item Selected
     * Precondition:    .
     * Postcondition:   See more from res/menu/activity_boolbar_menu
    *                   and layout/menu0_toolbar
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.activity_toolbar_sortbybutton:
                Toast.makeText(this, "Sort By button selected", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /************************************************************************
     * Purpose:         Create New Activity - The Hovering Smart Button
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void btnNewActivity(View view)
    {
        Intent intent = new Intent(this, NewActivity.class);
        startActivity(intent);
    }
}
