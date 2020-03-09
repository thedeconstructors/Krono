package com.deconstructors.krono.activities.plans;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.activities.ActivityAdapter_MultiSelect;
import com.deconstructors.krono.activities.activities.ActivityAdapter_NoSelect;
import com.deconstructors.krono.activities.activities.ActivityRVAdapter;
import com.deconstructors.krono.activities.activities.Activity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PlanDetails extends AppCompatActivity
{
    // Error Handler Log Search
    private static final String m_Tag = "Krono_Firebase_Log";

    final String EXTRA_PLANID = "EXTRA_PLANID";

    private TextView title;
    private TextView startTime;
    private RecyclerView planActivities;

    private String planId;

    // Database
    private FirebaseFirestore m_Firestore;

    //Plan Activities box vars
    private RecyclerView planActivities_RecyclerView;
    private ActivityAdapter_NoSelect planActivities_ActivityListAdapter;
    private List<Activity> planActivities_ActivityList;

    /****************************************
     * GUI Methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1_viewplan);

        planId = getIntent().getStringExtra(EXTRA_PLANID);

        m_Firestore = FirebaseFirestore.getInstance();

        title = (TextView) findViewById(R.id.ViewPlan_Title);
        startTime = (TextView) findViewById(R.id.ViewPlan_StartTime);
        planActivities_RecyclerView = (RecyclerView) findViewById(R.id.ViewPlan_recyclerActivities);

        InitPlanActivitiesRecycler();
        //Show user activities in 'My Activities' recycler view
        PopulateDetails();
    }


    /***********************************************
     * Helper functions
     */

    private void PopulateDetails()
    {
        m_Firestore.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("plans")
                .document(planId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot planDoc = task.getResult();
                            title.setText(planDoc.get("title").toString());
                            startTime.setText(planDoc.get("startTime").toString());

                            m_Firestore.collection("users")
                                    .document(FirebaseAuth.getInstance().getUid())
                                    .collection("plans")
                                    .document(planId)
                                    .collection("activities")
                                    .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        QuerySnapshot query = task.getResult();

                                        for (DocumentSnapshot doc : query)
                                        {
                                            Activity act = doc.toObject(Activity.class);
                                            act.setId(doc.getId());
                                            planActivities_ActivityList.add(act);
                                        }
                                        planActivities_ActivityListAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void InitPlanActivitiesRecycler()
    {
        // List of Activity (Class) -> Activity List Adapter -> Recycler View (XML)
        planActivities_ActivityList = new ArrayList<>();
        planActivities_ActivityListAdapter = new ActivityAdapter_NoSelect(planActivities_ActivityList);

        planActivities_RecyclerView.setHasFixedSize(true);
        planActivities_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        planActivities_RecyclerView.setAdapter(planActivities_ActivityListAdapter);
    }
}
