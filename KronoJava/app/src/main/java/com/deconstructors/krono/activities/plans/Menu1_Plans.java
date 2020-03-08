package com.deconstructors.krono.activities.plans;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.deconstructors.krono.helpers.PlansListAdapter;
import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Menu1_Plans extends AppCompatActivity {

    // Error Handler Log Search
    private static final String _dbPath = "userplans";

    // Database
    private FirebaseFirestore m_Firestore;

    // List of Plan (Class) -> Plan List Adapter -> Recycler View (XML)
    private RecyclerView m_MainList;
    private PlansListAdapter m_PlansListAdapter;
    private List<Plans> m_PlansList;
    private DocumentSnapshot _LastQueriedList;

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

        getPlans();
    }

    private void getPlans()
    {
        // Database Listener
        m_Firestore = FirebaseFirestore.getInstance();

        CollectionReference PlansCollectionRef = m_Firestore.collection(_dbPath);

        Query plansQuery;
        if (_LastQueriedList != null)
        {
            plansQuery = PlansCollectionRef
                    .whereEqualTo("ownerId", FirebaseAuth.getInstance().getUid())
                    .startAfter(_LastQueriedList);
        }
        else
        {
            plansQuery = PlansCollectionRef
                    .whereEqualTo("ownerId", FirebaseAuth.getInstance().getUid());
        }

        plansQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isComplete())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Plans plan = document.toObject(Plans.class);
                        m_PlansList.add(plan);
                    }

                    if (task.getResult().size() != 0)
                    {
                        _LastQueriedList = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }

                    m_PlansListAdapter.notifyDataSetChanged();
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
