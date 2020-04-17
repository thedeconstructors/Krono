package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.PlanAdapter;
import com.deconstructors.krono.auth.WelcomePage;
import com.deconstructors.krono.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPage extends AppCompatActivity implements PlanAdapter.PlanClickListener,
                                                           View.OnClickListener
{
    // Error Log
    private static final String TAG = "MainActivity";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView RecyclerView;
    private FloatingActionButton FAB;
    private TextView NameTextView;
    private TextView EmailTextView;
    private CircleImageView ProfilePicture;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private Query PlanQuery;
    private FirestoreRecyclerOptions<Plan> PlanOptions;
    private PlanAdapter PlanAdapter;
    private ListenerRegistration UserRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_main);

        this.setFirebaseAuth();
        this.setToolbar();
        this.setPlanDB();
        this.setUserDB();
        this.setContents();
    }

    /************************************************************************
     * Purpose:         Set Toolbar & Inflate Toolbar Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setToolbar()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.ui_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.NameTextView = findViewById(R.id.ui_main_displayName);
        this.EmailTextView = findViewById(R.id.ui_main_email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_plan_main, menu);

        return true;
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setPlanDB()
    {
        // Plan
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBInstance = FirebaseFirestore.getInstance();
        this.PlanQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID", this.AuthInstance.getCurrentUser().getUid());;
        this.PlanOptions = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(this.PlanQuery, Plan.class)
                .build();
        this.PlanAdapter = new PlanAdapter(this.PlanOptions, this);
    }

    private void setUserDB()
    {
        // User
        this.UserRegistration = this.DBInstance
                .collection(getString(R.string.collection_users))
                .document(this.AuthInstance.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        if (documentSnapshot != null)
                        {
                            NameTextView.setText(documentSnapshot.get("displayName").toString());
                            EmailTextView.setText(documentSnapshot.get("email").toString());
                        }
                    }
                });
    }

    void setFirebaseAuth()
    {
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Intent intent = new Intent(MainPage.this, WelcomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.PlanAdapter != null) { this.PlanAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.PlanAdapter != null) { this.PlanAdapter.stopListening(); }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (this.UserRegistration != null) { this.UserRegistration.remove(); }
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Recycler View
        this.RecyclerView = findViewById(R.id.MainActivity_RecyclerView);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.RecyclerView.setAdapter(this.PlanAdapter);

        // Other XML Widgets
        this.FAB = findViewById(R.id.ui_main_fab);
        this.FAB.setOnClickListener(this);
        this.ProfilePicture = findViewById(R.id.ui_main_profilepicture);
        this.ProfilePicture.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Parcelable Plan Interaction
     * Precondition:    .
     * Postcondition:   Send Plan Intent from MainActivity
     ************************************************************************/
    @Override
    public void onPlanSelected(int position)
    {
        Intent intent = new Intent(MainPage.this, ActivityPage.class);
        intent.putExtra(getString(R.string.intent_plans), this.PlanAdapter.getItem(position));
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Click Listener
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
                Intent intent = new Intent(MainPage.this, ActivityPage_All.class);
                startActivity(intent);
                break;
            }
            case R.id.ui_menu_friends:
            {
                Intent intent = new Intent(MainPage.this, FriendPage.class);
                startActivity(intent);
                break;
            }
            case R.id.ui_menu_chat:
            {
                break;
            }
            case R.id.ui_main_fab:
            {
                Intent intent = new Intent(MainPage.this, MainPage_New.class);
                startActivity(intent);
                break;
            }
            case R.id.ui_main_profilepicture:
            {
                LogOut();
                break;
            }
        }
    }

    private void LogOut()
    {
        FirebaseAuth.getInstance().signOut();
    }
}
