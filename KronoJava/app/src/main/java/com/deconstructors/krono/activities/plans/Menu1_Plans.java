package com.deconstructors.krono.activities.plans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.deconstructors.krono.activities.activities.Activity;
import com.deconstructors.krono.helpers.PlansListAdapter;
import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Menu1_Plans extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    // Variables
    private PlansListAdapter _PlansListAdapter;
    private List<Plans> _PlansList;

    // XML Widgets
    private RecyclerView _RecyclerView;
    private Toolbar _PlanToolbar;
    //SwipeRefreshLayout _SwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1__plans);

        _PlansList = new ArrayList<>();
        _PlansListAdapter = new PlansListAdapter(_PlansList);

        _RecyclerView = findViewById(R.id.MainMenu_PlanListID);
        _PlanToolbar = findViewById(R.id.menu1_toolbar);
        //_SwipeRefreshLayout = findViewById(R.id.MainMenu_SwipeRefreshPlanListID);
        //_SwipeRefreshLayout.setOnRefreshListener(this);

        setUpRecyclerView();
        setupToolbar();
        getPlans();
    }

    /***********************************************************************
     * Purpose:         Setup the RecyclerView
     * Precondition:    Called from onCreate
     * Postcondition:   RecyclerView matched to MainMenu_PlanListID
     ************************************************************************/
    private void setUpRecyclerView()
    {
        _RecyclerView.setHasFixedSize(true);
        _RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        _RecyclerView.setAdapter(_PlansListAdapter);
    }

    /***********************************************************************
     * Purpose:         Setup the Toolbar
     * Precondition:    Called from onCreate
     * Postcondition:   .
     ************************************************************************/
    private void setupToolbar()
    {
        setSupportActionBar(_PlanToolbar);
        getSupportActionBar().setTitle("Plans Menu");
    }

    /******************************* XML ***********************************/
    /************************************************************************
     * Purpose:         Toolbar Menu Inflater
     * Precondition:    .
     * Postcondition:   Activates the toolbar menu by inflating it
     *                  See more from res/menu/activity_boolbar_menu
     *                  and layout/menu0_toolbar
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_toolbar_menu, menu);

        // Search Button
        MenuItem searchItem = menu.findItem(R.id.activity_toolbar_searchbutton);
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
                _PlansListAdapter.getFilter().filter(newText);
                _PlansListAdapter.resetFilterList();
                return false;
            }
        });

        return true;
    }

    /***********************************************************************
     * Purpose:         Get plans
     * Precondition:    Called from onCreate
     * Postcondition:   Retrieve plans from the database
     *                  Grabs current user, and then their planss
     *
     ************************************************************************/
    private void getPlans()
    {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("plans")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                          @Override
                                          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                              /* Grab the iterator from the built in google library */
                                              Iterator<QueryDocumentSnapshot> iterator = queryDocumentSnapshots.iterator();

                                              /* Reset the plan list view */
                                              if (!_PlansList.isEmpty()) {
                                                  _PlansList.clear();
                                              }

                                              /* Populate the list of plans using an iterator */
                                              while (iterator.hasNext()) {
                                                  QueryDocumentSnapshot snapshot = iterator.next();

                                                  Plans plan = snapshot.toObject(Plans.class);
                                                  plan.setPlanId(snapshot.getId());
                                                  _PlansList.add(plan);
                                              }

                                              _PlansListAdapter.notifyDataSetChanged();
                                              //_SwipeRefreshLayout.setRefreshing(false);
                                          }
                                      });
    }

    public void btnNewPlan(View view)
    {
        Intent intent = new Intent(this, NewPlan.class);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getPlans();
    }

    //Currently not used
    @Override
    public void onRefresh()
    {
        this.getPlans();
    }
}
