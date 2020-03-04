package com.demo.planactivityuserdemo.userinterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.demo.planactivityuserdemo.R;
import com.demo.planactivityuserdemo.adapter.PlansRecyclerAdapter;
import com.demo.planactivityuserdemo.model.Plan;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
                   PlansRecyclerAdapter.PlansRecyclerClickListener, com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
{
    // Logcat
    private static final String TAG = "PlansActivity";

    // XML Widgets
    private Toolbar Toolbar;
    private DrawerLayout DrawerLayout;
    private NavigationView NavigationView;
    private ActionBarDrawerToggle DrawerToggle;
    private AppBarConfiguration AppBarConfig;

    // Vars
    private ArrayList<Plan> PlanList = new ArrayList<>();
    private Set<String> PlanIDList = new HashSet<>();
    private PlansRecyclerAdapter RecyclerAdapter;
    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration PlansEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);

        setContentViews();
        //setRecyclerView();
    }

    private void setContentViews()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(this.Toolbar);
        getSupportActionBar().setTitle(R.string.navigation_allActivitiesText);

        // Navigation Drawer
        this.DrawerLayout = findViewById(R.id.main_drawerLayout);
        this.DrawerToggle = new ActionBarDrawerToggle(this,
                                                      this.DrawerLayout,
                                                      this.Toolbar,
                                                      R.string.navigation_openText,
                                                      R.string.navigation_closeText);
        this.DrawerLayout.addDrawerListener(DrawerToggle);
        this.DrawerToggle.syncState();
        this.NavigationView = findViewById(R.id.main_navigationView);
        this.NavigationView.setNavigationItemSelectedListener(this);
        this.NavigationView.setCheckedItem(R.id.nav_allActivities);

        // Database
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    /************************************************************************
     * Purpose:         Navigation View Sync and Toolbar Icon Animation
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        this.DrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        this.DrawerToggle.onConfigurationChanged(newConfig);
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void getPlans()
    {
        Query planRef = this.FirestoreDB
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID", FirebaseAuth.getInstance().getCurrentUser().getUid());

        PlansEventListener = planRef.addSnapshotListener(new EventListener<QuerySnapshot>()
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
                    for (QueryDocumentSnapshot doc : documentSnapshots)
                    {
                        //doc.getReference().getParent();
                        Plan plan = doc.toObject(Plan.class);

                        if(!PlanIDList.contains(plan.getPlanID()))
                        {
                            PlanIDList.add(plan.getPlanID());
                            PlanList.add(plan);
                        }
                    }

                    Log.d(TAG, "getPlans: number of plans: " + PlanList.size());
                    RecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getSharedUsers()
    {

    }

    /************************************************************************
     * Purpose:         PlanRecyclerAdapter Override
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onPlanSelected(int position)
    {
        navPlanActivity(this.PlanList.get(position));
    }

    private void navPlanActivity(Plan plan)
    {
        Intent intent = new Intent(MainActivity.this, ActivitiesActivity.class);
        intent.putExtra(getString(R.string.intent_plans), plan);
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Toolbar & Menu Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_plans, menu);

        // Search Button
        MenuItem searchItem = menu.findItem(R.id.plansMenu_search);
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
                //RecyclerAdapter.getFilter().filter(newText);
                //RecyclerAdapter.resetFilterList();
                return false;
            }
        });

        return true;
    }

    /************************************************************************
     * Purpose:         Other Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    protected void onResume()
    {
        super.onResume();
        //getPlans();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(this.PlansEventListener != null)
        {
            this.PlansEventListener.remove();
        }
    }

    @Override
    public void onClick(View view)
    {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.nav_allActivities)
        {
            getSupportActionBar().setTitle(R.string.navigation_allActivitiesText);
        }
        else if (item.getItemId() == R.id.nav_friends)
        {
            getSupportActionBar().setTitle(R.string.navigation_friendsText);
        }

        DrawerLayout drawer = findViewById(R.id.main_drawerLayout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
