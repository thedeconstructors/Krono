package com.deconstructors.krono.activities.plans;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.deconstructors.structures.Plans;
import com.deconstructors.krono.helpers.PlansListAdapter;
import com.deconstructors.krono.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Menu1_Plans extends AppCompatActivity {

    // Error Handler Log Search
    private static final String m_Tag = "Krono_Firebase_Log";

    // Database
    private FirebaseFirestore m_Firestore;

    // List of Plan (Class) -> Plan List Adapter -> Recycler View (XML)
    private RecyclerView m_MainList;
    private PlansListAdapter m_PlansListAdapter;
    private List<Plans> m_PlansList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1__plans);

        // List of Plan (Class) -> Plan List Adapter -> Recycler View (XML)
        m_PlansList = new ArrayList<>();
        m_PlansListAdapter = new PlansListAdapter(m_PlansList);

        m_MainList = (RecyclerView)findViewById(R.id.MainMenu_PlanListID);
        m_MainList.setHasFixedSize(true);
        m_MainList.setLayoutManager(new LinearLayoutManager(this));
        m_MainList.setAdapter(m_PlansListAdapter);

        // Database Listener
        m_Firestore = FirebaseFirestore.getInstance();
        m_Firestore.collection("plans").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            /************************************************************************
             * Purpose:         On Event
             * Precondition:    An Item has been added
             * Postcondition:   Change the plan list accordingly and
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
                            Plans plans = doc.getDocument().toObject(Plans.class);
                            m_PlansList.add(plans);

                            // Notify the Adapter something is changed
                            m_PlansListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public void btnNewPlan(View view)
    {
        Intent intent = new Intent(this, NewPlan.class);
        startActivity(intent);
    }
}
