package com.deconstructors.krono.activities.plans;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.activities.ActivityRVAdapter;
import com.deconstructors.krono.helpers.SessionData;
import com.deconstructors.krono.activities.activities.Activity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class NewPlan extends AppCompatActivity
{
    // Error Handler Log Search
    private static final String m_Tag = "Krono_Firebase_Log";

    private TextView title;
    private TextView startTime;
    private RecyclerView myActivities;
    private RecyclerView planActivities;

    // Database
    private FirebaseFirestore m_Firestore;

    //MyActivities box vars
    private RecyclerView myActivities_RecyclerView;
    private ActivityRVAdapter myActivities_ActivityListAdapter;
    private List<Activity> myActivities_ActivityList;

    //Plan Activities box vars
    private RecyclerView planActivities_RecyclerView;
    private ActivityRVAdapter planActivities_ActivityListAdapter;
    private List<Activity> planActivities_ActivityList;

    /****************************************
     * GUI Methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1_plans_newplan);

        //Show user activities in 'My Activities' recycler view
        PopulateMyActivitiesRecycler();

        //Initialize plan activities recycler view
        InitPlanActivitiesRecycler();

        title = (TextView) findViewById(R.id.txtTitle);
        startTime = (TextView) findViewById(R.id.txtStartTime);
        myActivities = (RecyclerView) findViewById(R.id.recyclerMyActivities);
        planActivities = (RecyclerView) findViewById(R.id.recyclerPlan);
    }

    public void createPlanOnClick(View view)
    {
        Map<String, Object> userPlan = new HashMap<>();

        Toast emptyTextFailureMessage = Toast.makeText(NewPlan.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        //check if title and start time are not empty
        if (title.getText().toString().trim().compareTo("") != 0 && startTime.getText().toString().trim().compareTo("") != 0)
        {
            userPlan.put("ownerId", SessionData.GetInstance().GetUserID());
            userPlan.put("title", title.getText().toString());
            userPlan.put("startTime", startTime.getText().toString());

            //populate activityids string
            String activityIds = "";
            for (Activity act : planActivities_ActivityList)
            {
                activityIds += act.getId() + ";";
            }

            userPlan.put("activityids", activityIds);

            final Toast successMessage = Toast.makeText(NewPlan.this,
                    "Plan Added Successfully.", Toast.LENGTH_SHORT);
            final Toast failureMessage = Toast.makeText(NewPlan.this,
                    "Error: Could Not Add Plan.", Toast.LENGTH_SHORT);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("userplans")
                    .add(userPlan)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            UpdateUsersToPlans(documentReference.getId());
                            successMessage.show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            failureMessage.show();
                        }
                    });
        }
        else
        {
            emptyTextFailureMessage.show();
        }
    }

    /***********************************************
     * Helper functions
     */
    public void UpdateUsersToPlans(String activityId)
    {
        Map<String, Object> userToPlan = new HashMap<>();
        
        userToPlan.put("userid", SessionData.GetInstance().GetUserID());
        userToPlan.put("activityid", activityId);

        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("userstoplans")
                .add(userToPlan);
    }

    //Sets up 'My Activities' recycler view
    private void PopulateMyActivitiesRecycler()
    {
        // List of Activity (Class) -> Activity List Adapter -> Recycler View (XML)
        myActivities_ActivityList = new ArrayList<>();
        myActivities_ActivityListAdapter = new ActivityRVAdapter(myActivities_ActivityList);

        myActivities_RecyclerView = (RecyclerView)findViewById(R.id.recyclerMyActivities);
        myActivities_RecyclerView.setHasFixedSize(true);
        myActivities_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myActivities_RecyclerView.setAdapter(myActivities_ActivityListAdapter);

        // Database Listener
        m_Firestore = FirebaseFirestore.getInstance();
        m_Firestore.collection("useractivities").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            /************************************************************************
             * Purpose:         On Event
             * Precondition:    An Item has been added
             * Postcondition:   Change the Activity list accordingly and
             *                  Notify the adapter
             *                  This way, we don't have to scan DB every time
             ************************************************************************/
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if (e != null)
                {
                    Log.d(m_Tag, "Error : " + e.getMessage());
                }
                else
                {
                    // Document contains data read from a document in your Firestore database
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                    {
                        if (doc.getType() == DocumentChange.Type.ADDED)
                        {
                            // Arrange the data according to the model class
                            // All by itself
                            Activity activity = doc.getDocument().toObject(Activity.class);
                            activity.setId(doc.getDocument().getId());
                            myActivities_ActivityList.add(activity);

                            // Notify the Adapter something is changed
                            myActivities_ActivityListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void InitPlanActivitiesRecycler()
    {
        // List of Activity (Class) -> Activity List Adapter -> Recycler View (XML)
        planActivities_ActivityList = new ArrayList<>();
        planActivities_ActivityListAdapter = new ActivityRVAdapter(planActivities_ActivityList);

        planActivities_RecyclerView = (RecyclerView)findViewById(R.id.recyclerPlan);
        planActivities_RecyclerView.setHasFixedSize(true);
        planActivities_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        planActivities_RecyclerView.setAdapter(planActivities_ActivityListAdapter);
    }

    public void AddActivityOnClick(View view)
    {
        //iterate through activities list
        //  for each selected activity
        //  add it to the plan list
        for (Activity act : myActivities_ActivityList)
        {
            if (act.isSelected())
            {
                myActivities_ActivityList.get(myActivities_ActivityList.indexOf(act)).setSelected(false);
                Activity copyAct = new Activity(act);
                planActivities_ActivityList.add(copyAct);
            }
        }
        planActivities_ActivityListAdapter.notifyDataSetChanged();
        myActivities_ActivityListAdapter.notifyDataSetChanged();
    }

}
