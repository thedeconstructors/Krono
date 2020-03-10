package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.PlanRVAdapter;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PlanRVAdapter.PlanRVClickListener,
                   View.OnClickListener
{
    // Error Log
    private static final String TAG = "MainActivity";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView PlanRecyclerView;
    private FloatingActionButton FAB;
    private TextView NameTextView;
    private TextView EmailTextView;

    // Vars
    private ListenerRegistration PlanEventListener;
    private List<Plan> PlanList;
    private PlanRVAdapter PlanRVAdapter;

    // Database
    private FirebaseFirestore FirestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);

        this.setContents();
        this.getProfile();
        this.getMenu();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.ui_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.NameTextView = findViewById(R.id.ui_main_displayName);
        this.EmailTextView = findViewById(R.id.ui_main_email);

        // Recycler View
        this.PlanList = new ArrayList<>();
        this.PlanRVAdapter = new PlanRVAdapter(PlanList, this);
        this.PlanRecyclerView = findViewById(R.id.ui_main_recyclerview);
        this.PlanRecyclerView.setHasFixedSize(true);
        this.PlanRecyclerView.setAdapter(this.PlanRVAdapter);
        this.PlanRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Others
        this.FAB = findViewById(R.id.ui_main_fab);
        this.FAB.setOnClickListener(this);

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void getProfile()
    {
        FirestoreDB.collection(getString(R.string.collection_users))
                   .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                   {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot)
                       {
                           if (documentSnapshot != null)
                           {
                               String fn = documentSnapshot.get("firstname").toString();
                               String ln = documentSnapshot.get("lastname").toString();
                               String em = documentSnapshot.get("loginEmail").toString();

                               NameTextView.setText(fn + " " + ln);
                               EmailTextView.setText(em);
                           }
                       }
                   });
    }


    private void getMenu()
    {
        Query planRef = FirestoreDB.collection(getString(R.string.collection_plans));

        this.PlanEventListener = planRef.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "getPlans: onEvent Listen failed.", e);
                    return;
                }

                if(documentSnapshots != null)
                {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                    {
                        if (doc.getType() == DocumentChange.Type.ADDED)
                        {
                            Plan plan = doc.getDocument().toObject(Plan.class);
                            PlanList.add(plan);
                        }
                        else if (doc.getType() == DocumentChange.Type.MODIFIED)
                        {
                            Plan plan = doc.getDocument().toObject(Plan.class);
                            PlanList.remove(Helper.getPlan(PlanList, plan.getPlanID()));
                            PlanList.add(plan);
                        }
                        else if (doc.getType() == DocumentChange.Type.REMOVED)
                        {
                            Plan plan = doc.getDocument().toObject(Plan.class);
                            PlanList.remove(Helper.getPlan(PlanList, plan.getPlanID()));
                        }
                    }

                    Log.d(TAG, "getPlans: number of plans: " + PlanList.size());
                    PlanRVAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onPlanSelected(int position)
    {
        Intent intent = new Intent(MainActivity.this, ActivityPage.class);
        intent.putExtra(getString(R.string.intent_plans), this.PlanList.get(position));
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Main Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ui_menu_allActivities:
            {
                Intent intent = new Intent(MainActivity.this, ActivityPage.class);
                startActivity(intent);
            }
            case R.id.ui_menu_friends:
            {

            }
            case R.id.ui_menu_chat:
            {

            }
            case R.id.ui_main_fab:
            {
                int x = 5;
            }
        }
    }
}
