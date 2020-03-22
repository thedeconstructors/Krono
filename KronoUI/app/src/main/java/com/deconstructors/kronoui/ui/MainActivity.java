package com.deconstructors.kronoui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.adapter.PlanRVAdapter;
import com.deconstructors.kronoui.module.Plan;
import com.deconstructors.kronoui.utility.Helper;
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
    private ProgressBar ProgressBar;

    // Database
    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration PlanEventListener;
    private List<Plan> PlanList;
    private PlanRVAdapter PlanRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_main);

        this.setContents();
        this.getUserInfo();
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
        this.ProgressBar = findViewById(R.id.main_progressbar);
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
    private void getUserInfo()
    {
        this.showProgressBar();

        // Get User Name & Email
        // Change to a singleton object or write to strings.xml later
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
                               hideProgressBar();
                               NameTextView.setText(documentSnapshot.get("displayName").toString());
                               EmailTextView.setText(documentSnapshot.get("email").toString());
                           }
                       }
                   });
    }

    private void getMenu()
    {
        this.showProgressBar();

        Query planRef = FirestoreDB.collection(getString(R.string.collection_plans))
                                   .whereEqualTo("ownerID", FirebaseAuth.getInstance().getCurrentUser().getUid());

        this.PlanEventListener = planRef.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                @Nullable FirebaseFirestoreException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "getMenu: onEvent Listen failed.", e);
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
                    hideProgressBar();
                }
            }
        });
    }

    /************************************************************************
     * Purpose:         Parcelable Activity Interaction
     * Precondition:    .
     * Postcondition:   Send Activity Intent from MainActivity
     ************************************************************************/
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
                Intent intent = new Intent(MainActivity.this, AllActivitiesPage.class);
                startActivity(intent);
                break;
            }
            case R.id.ui_menu_friends:
            {
                Intent intent = new Intent(MainActivity.this, FriendPage.class);
                startActivity(intent);
                break;
            }
            case R.id.ui_menu_chat:
            {

                break;
            }
            case R.id.ui_main_fab:
            {
                Intent intent = new Intent(MainActivity.this, NewPlanPage.class);
                startActivity(intent);
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Menu Inflater
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    private void showProgressBar()
    {
        if (this.ProgressBar.getVisibility() == View.INVISIBLE)
        {
            this.ProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar()
    {
        if(this.ProgressBar.getVisibility() == View.VISIBLE)
        {
            this.ProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
