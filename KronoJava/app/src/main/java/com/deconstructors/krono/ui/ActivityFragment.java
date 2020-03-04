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
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.activities.activities.NewActivity;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.adapter.ActivityRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class ActivityFragment extends Fragment
        implements View.OnClickListener,
                   SwipeRefreshLayout.OnRefreshListener
{
    // Logcat
    private static final String TAG = "ActivitiesActivity";

    // Views
    private android.widget.ProgressBar ProgressBar;
    private RecyclerView RecyclerView;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private FloatingActionButton FAB;

    // Vars
    private Plan Plan;
    private ArrayList<Activity> ActivityList;
    private Set<String> ActivityListIDs;
    private ActivityRVAdapter ActivityRVAdapter;
    private FirebaseFirestore FirestoreDB;
    private ListenerRegistration ActivityEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.ui_activity, container, false);

        setContents(rootView);
        setRecyclerView(rootView);
        getPlanIntent();

        return rootView;
    }

    private void setContents(View view)
    {
        this.ProgressBar = (ProgressBar) view.findViewById(R.id.activity_progressBar);
        this.SwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_refreshlayout);
        this.SwipeRefreshLayout.setOnRefreshListener(this);
        this.FAB = (FloatingActionButton) view.findViewById(R.id.activity_fab);
        this.FAB.setOnClickListener(this);

        this.FirestoreDB = FirebaseFirestore.getInstance();
        this.Plan = new Plan();
        this.ActivityList = new ArrayList<>();
        this.ActivityListIDs = new HashSet<>();
    }

    private void setRecyclerView(View view)
    {
        this.ActivityRVAdapter = new ActivityRVAdapter(this.ActivityList);
        this.RecyclerView = (RecyclerView) view.findViewById(R.id.activity_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.RecyclerView.setAdapter(this.ActivityRVAdapter);
        this.RecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void getPlanIntent()
    {
        Bundle bundle = getArguments();
        Plan.setPlanID(bundle.getString(getString(R.string.intent_plans)));
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
        this.getActivities();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(this.ActivityEventListener != null)
        {
            this.ActivityEventListener.remove();
        }
    }

    private void getActivities()
    {
        Query planRef = FirestoreDB
                .collection(getString(R.string.collection_plans))
                .document(this.Plan.getPlanID())
                .collection(getString(R.string.collection_activities));

        ActivityEventListener = planRef.addSnapshotListener(new EventListener<QuerySnapshot>()
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

                        Activity activity = doc.toObject(Activity.class);

                        if(!ActivityListIDs.contains(activity.getActivityID()))
                        {
                            ActivityListIDs.add(activity.getActivityID());
                            ActivityList.add(activity);
                        }
                    }

                    Log.d(TAG, "getPlans: number of plans: " + ActivityList.size());
                    ActivityRVAdapter.notifyDataSetChanged();
                }
            }
        });
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
                Intent intent = new Intent(this.getActivity(), NewActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRefresh()
    {
        this.getActivities();
        SwipeRefreshLayout.setRefreshing(false);
    }
}
