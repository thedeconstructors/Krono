package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.activities.Menu0_Activities;
import com.deconstructors.krono.activities.plans.NewPlan;
import com.deconstructors.krono.activities.plans.Plan;
import com.deconstructors.krono.adapter.PlanRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;
import java.util.Set;

public class PlanFragment extends Fragment
        implements View.OnClickListener,
                   PlanRVAdapter.PlanRVClickListener,
                   SwipeRefreshLayout.OnRefreshListener
{
    // Error Handler Log Search
    private static final String TAG = "Plan Fragment";

    // Database
    private FirebaseFirestore FirestoreDB;

    // Views
    private ProgressBar ProgressBar;
    private RecyclerView RecyclerView;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private FloatingActionButton FAB;

    // Vars
    private List<Plan> PlanList;
    private Set<String> PlanListIDs;
    private DocumentSnapshot LastQueriedList;
    //private Set<String> FilterListIDs = new HashSet<>();
    private PlanRVAdapter PlanRVAdapter;
    private ListenerRegistration PlanEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.ui_plan, container, false);

        setContents(rootView);
        setRecyclerView(rootView);

        return rootView;
    }

    private void setContents(View view)
    {
        this.ProgressBar = (ProgressBar) view.findViewById(R.id.plan_progressBar);
        this.SwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.plans_refreshlayout);
        this.SwipeRefreshLayout.setOnRefreshListener(this);
        this.FAB = (FloatingActionButton) view.findViewById(R.id.plan_fab);
        this.FAB.setOnClickListener(this);

        this.FirestoreDB = FirebaseFirestore.getInstance();
        this.PlanList = new ArrayList<>();
        this.PlanListIDs = new HashSet<>();
    }

    private void setRecyclerView(View view)
    {
        this.PlanRVAdapter = new PlanRVAdapter(this.PlanList, this);
        this.RecyclerView = (RecyclerView) view.findViewById(R.id.plan_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.RecyclerView.setAdapter(this.PlanRVAdapter);
        this.RecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setFragmentInteraction(View view)
    {

    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onResume()
    {
        super.onResume();
        this.getPlan();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(this.PlanEventListener != null)
        {
            this.PlanEventListener.remove();
        }
    }

    private void getPlan()
    {
        Query planRef = FirestoreDB
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID", FirebaseAuth.getInstance().getUid());

        PlanEventListener = planRef.addSnapshotListener(new EventListener<QuerySnapshot>()
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

                boolean x = documentSnapshots.isEmpty();
                boolean y = documentSnapshots.getDocuments().isEmpty();

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
                    PlanRVAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /************************************************************************
     * Purpose:         PlanRecyclerAdapter Override
     * Precondition:    RVAdapter passed a plan position
     * Postcondition:   Start a new fragment (activity) and pass the position
     ************************************************************************/
    @Override
    public void onPlanSelected(int position)
    {
        navPlanActivity(this.PlanList.get(position));
    }

    private void navPlanActivity(Plan plan)
    {
        Intent intent = new Intent(this.getActivity(), Menu0_Activities.class);
        intent.putExtra(getString(R.string.intent_plans), plan);
        startActivity(intent);

        // Bugged
        /*Fragment activity = new ActivityFragment();

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.intent_plans), plan.getPlanID());
        activity.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_plans, activity);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
    }

    /************************************************************************
     * Purpose:         User Interface Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.plan_fab:
            {
                Intent intent = new Intent(this.getActivity(), NewPlan.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRefresh()
    {
        this.getPlan();
        SwipeRefreshLayout.setRefreshing(false);
    }
}
