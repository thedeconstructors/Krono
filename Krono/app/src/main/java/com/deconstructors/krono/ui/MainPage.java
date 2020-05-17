package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.PlanAdapter;
import com.deconstructors.krono.auth.WelcomePage;
import com.deconstructors.krono.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPage extends AppCompatActivity implements PlanAdapter.PlanClickListener,
                                                           View.OnClickListener,
                                                            TabLayout.OnTabSelectedListener,
                                                            SearchView.OnQueryTextListener
{
    //Xml Widgets
    androidx.recyclerview.widget.RecyclerView recyclerView;
    private TextView NameTextView;
    private TextView EmailTextView;
    private CircleImageView ProfilePicture;
    private TabLayout Tabs;
    private FloatingActionButton FAB;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private PlanAdapter PlanAdapter;
    private ListenerRegistration UserRegistration;

    //Plan stuff
    Filterable CurrentFilterAdapter = null;

    private PlanAdapter MyPlanAdapter;

    private PlanAdapter SharedPlanAdapter;

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
        // XML Widgets
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.ui_toolbar);
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);
        this.NameTextView = findViewById(R.id.ui_main_displayName);
        this.EmailTextView = findViewById(R.id.ui_main_email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_plan_main, menu);

        MenuItem searchItem = menu.findItem(R.id.activity_toolbar_searchbutton);
        SearchView search = (SearchView) searchItem.getActionView();
        search.setOnQueryTextListener(this);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activity_toolbar_settingsButton) {
            Intent intent = new Intent(MainPage.this, SettingsPage_Main.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Query planQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID", Objects.requireNonNull(
                        this.AuthInstance.getCurrentUser()
                ).getUid());

        FirestoreRecyclerOptions<Plan> planOptions = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(planQuery, Plan.class)
                .build();
        this.PlanAdapter = new PlanAdapter(planOptions, this);

        //My plans
        Query myPlanQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID", this.AuthInstance.getCurrentUser().getUid());
        FirestoreRecyclerOptions<Plan> myPlanOptions = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(myPlanQuery, Plan.class)
                .build();
        this.MyPlanAdapter = new PlanAdapter(myPlanOptions, this);

        //Shared plans
        Query sharedPlanQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .whereArrayContains("collaborators", this.AuthInstance.getCurrentUser().getUid());
        FirestoreRecyclerOptions<Plan> sharedPlanOptions = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(sharedPlanQuery, Plan.class)
                .build();
        this.SharedPlanAdapter = new PlanAdapter(sharedPlanOptions, this);
    }

    private void setUserDB()
    {
        // User
        this.UserRegistration = this.DBInstance
                .collection(getString(R.string.collection_users))
                .document(Objects.requireNonNull(
                        this.AuthInstance.getCurrentUser()
                ).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        if (documentSnapshot != null)
                        {
                            NameTextView.setText(Objects.requireNonNull(
                                    documentSnapshot.get("displayName")).toString());
                            EmailTextView.setText(Objects.requireNonNull(
                                    documentSnapshot.get("email")).toString());

                            Object pic_url;
                            if ((pic_url = documentSnapshot.get("picture")) == null) {
                                pic_url = getString(R.string.default_picture);
                            }

                            Picasso.get().load(pic_url.toString()).into(ProfilePicture);
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
        onTabSelected(Objects.requireNonNull(Tabs.getTabAt(Tabs.getSelectedTabPosition())));
        //if (this.PlanAdapter != null) { this.PlanAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        this.MyPlanAdapter.stopListening();
        this.SharedPlanAdapter.stopListening();
        //if (this.PlanAdapter != null) { this.PlanAdapter.stopListening(); }
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
        recyclerView = findViewById(R.id.MainActivity_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.PlanAdapter);

        // Other XML Widgets
        this.FAB = findViewById(R.id.ui_main_fab);
        this.FAB.setOnClickListener(this);
        this.ProfilePicture = findViewById(R.id.ui_main_profilepicture);
        this.ProfilePicture.setOnClickListener(this);
        this.Tabs = findViewById(R.id.main_tabLayout);
        this.Tabs.addOnTabSelectedListener(this);

        this.Tabs.selectTab(Tabs.getTabAt(0));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (CurrentFilterAdapter != null)
            CurrentFilterAdapter.getFilter().filter(newText);
        return true;
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
        if (Tabs.getSelectedTabPosition() == 0) {
            intent.putExtra(getString(R.string.intent_plans), this.MyPlanAdapter.getItem(position));
            intent.putExtra(getString(R.string.intent_editable),ActivityPage.EditMode.OWNER);
            MyPlanAdapter.clearFilteredList();
        }
        else {
            intent.putExtra(getString(R.string.intent_plans), this.SharedPlanAdapter.getItem(position));
            intent.putExtra(getString(R.string.intent_editable),ActivityPage.EditMode.COLLAB);
            SharedPlanAdapter.clearFilteredList();
        }
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
                Intent intent = new Intent(MainPage.this, ProfilePage.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition())
        {
            //My Plans
            case 0:
                this.recyclerView.setAdapter(this.MyPlanAdapter);
                this.CurrentFilterAdapter = this.MyPlanAdapter;
                this.MyPlanAdapter.startListening();
                this.SharedPlanAdapter.stopListening();
                this.FAB.setVisibility(View.VISIBLE);
                break;
            //Shared Plans
            case 1:
                this.recyclerView.setAdapter(this.SharedPlanAdapter);
                this.CurrentFilterAdapter = this.SharedPlanAdapter;
                this.SharedPlanAdapter.startListening();
                this.MyPlanAdapter.stopListening();
                this.FAB.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //nothing
    }
}
