package com.deconstructors.krono;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;

import com.deconstructors.firestoreinteract.FirestoreDB;
import com.deconstructors.firestoreinteract.ListHandler;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.opencensus.tags.Tag;

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
    }

    public void btnNewActivity(View view)
    {
        Intent intent = new Intent(this, NewActivity.class);
        startActivity(intent);
    }

    public void ToolbarBackButton_OnClick(View view)
    {
        finish();
    }
}
