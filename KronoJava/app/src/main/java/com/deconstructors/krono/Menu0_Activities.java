package com.deconstructors.krono;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import io.opencensus.tags.Tag;

public class Menu0_Activities extends AppCompatActivity
{
    private static final String m_Tag = "FireLog";
    private RecyclerView m_MainList;
    private FirebaseFirestore m_Firestore;

    private PlansListAdapter m_PlansListAdapter;
    private List<Plans> m_PlansList;

    /************************************************************************
     * Purpose:         Set Event
     * Precondition:    Menu items are w/o button events
     * Postcondition:   Menu items are set with onClick indexing events
     *                  This is mainly to separate the xml files
     ************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu0__activities);

        m_PlansList = new ArrayList<>();
        m_PlansListAdapter = new PlansListAdapter(m_PlansList);

        m_MainList = (RecyclerView)findViewById(R.id.mainList);
        m_MainList.setHasFixedSize(true);
        m_MainList.setLayoutManager(new LinearLayoutManager(this));
        m_MainList.setAdapter(m_PlansListAdapter);

        m_Firestore = FirebaseFirestore.getInstance();

        m_Firestore.collection("plans").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if (e != null)
                {
                    Log.d(m_Tag, "Error : " + e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                {
                    if (doc.getType() == DocumentChange.Type.ADDED)
                    {
                        // Arrange the data according to the model class
                        // All by itself
                        Plans plans = doc.getDocument().toObject(Plans.class);
                        m_PlansList.add(plans);

                        m_PlansListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
