package com.demo.planactivityuserdemo.userinterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.demo.planactivityuserdemo.R;
import com.demo.planactivityuserdemo.adapter.PlansRecyclerAdapter;
import com.demo.planactivityuserdemo.model.Plan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
                   PlansRecyclerAdapter.PlansRecyclerClickListener
{
    // Logcat
    private static final String TAG = "PlansActivity";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView RecyclerView;
    private DrawerLayout DrawerLayout;
    private ActionBarDrawerToggle DrawerToggle;

    // Vars
    private ArrayList<Plan> PlanList = new ArrayList<>();
    private Set<String> PlanListIDs = new HashSet<>();
    private PlansRecyclerAdapter RecyclerAdapter;
    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration PlansEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);

        setContentViews();
        setRecyclerView();
    }

    private void setContentViews()
    {
        // Toolbar & Navigation Drawer
        this.Toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(this.Toolbar);
        this.DrawerLayout = findViewById(R.id.main_drawerLayout);
        this.DrawerToggle = new ActionBarDrawerToggle(this,
                                                      this.DrawerLayout,
                                                      this.Toolbar,
                                                      R.string.navigation_openText,
                                                      R.string.navigation_closeText);
        this.DrawerLayout.addDrawerListener(this.DrawerToggle);

        // Recycler View & Database
        this.RecyclerView = findViewById(R.id.main_planRecyclerview);
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    private void setRecyclerView()
    {
        this.RecyclerAdapter = new PlansRecyclerAdapter(PlanList, this);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(RecyclerAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /************************************************************************
     * Purpose:         OnClickListener & Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        this.DrawerToggle.syncState();
    }

    private void getPlans()
    {
        CollectionReference planRef = FirestoreDB
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid())
                .collection(getString(R.string.collection_plans));

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

                        doc.getReference().getParent();

                        Plan plan = doc.toObject(Plan.class);

                        if(!PlanListIDs.contains(plan.getPlanID()))
                        {
                            PlanListIDs.add(plan.getPlanID());
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
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_plans, menu);

        // Search Button
        /*MenuItem searchItem = menu.findItem(R.id.plansMenu_search);
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
        });*/

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
        getPlans();
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
}
