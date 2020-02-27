package com.demo.planactivityuserdemo.userinterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.SearchView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import com.demo.planactivityuserdemo.R;
import com.demo.planactivityuserdemo.UserClient;
import com.demo.planactivityuserdemo.adapter.PlansRecyclerAdapter;
import com.demo.planactivityuserdemo.model.Plan;
import com.demo.planactivityuserdemo.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class PlansActivity extends AppCompatActivity
        implements View.OnClickListener,
                   PlansRecyclerAdapter.PlansRecyclerClickListener,
                   SwipeRefreshLayout.OnRefreshListener
{
    // Logcat
    private static final String TAG = "PlansActivity";

    // Views
    private ProgressBar ProgressBar;
    private RecyclerView RecyclerView;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private SearchView SearchView;

    // Vars
    private ArrayList<Plan> PlanList = new ArrayList<>();
    private Set<String> PlanListIDs = new HashSet<>();
    private Set<String> FilterListIDs = new HashSet<>();
    private ArrayList<User> UserList = new ArrayList<>();
    private PlansRecyclerAdapter RecyclerAdapter;
    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration PlansEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_plans);

        setContentViews();
        setRecyclerView();
        setToolbar();
    }

    /************************************************************************
     * Purpose:         setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContentViews()
    {
        this.ProgressBar = findViewById(R.id.plans_progressBar);
        this.RecyclerView = findViewById(R.id.plans_recyclerview);
        this.SwipeRefreshLayout = findViewById(R.id.plans_refreshlayout);
        this.SwipeRefreshLayout.setOnRefreshListener(this);
        this.findViewById(R.id.plans_fab).setOnClickListener(this);
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    private void setRecyclerView()
    {
        this.RecyclerAdapter = new PlansRecyclerAdapter(PlanList, this);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(RecyclerAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setToolbar()
    {
        getSupportActionBar().setTitle(R.string.collection_plans);
    }

    /************************************************************************
     * Purpose:         OnClickListener & Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.plans_fab:
            {
                //
            }
        }
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
            public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                @Nullable FirebaseFirestoreException e)
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
        Intent intent = new Intent(PlansActivity.this, ActivitiesActivity.class);
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
    public void onRefresh()
    {
        getPlans();
        SwipeRefreshLayout.setRefreshing(false);
    }
}
