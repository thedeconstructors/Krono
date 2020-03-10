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
import com.deconstructors.krono.activities.activities.ActivityRVAdapter;
import com.deconstructors.krono.activities.activities.Activity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NewPlan extends AppCompatActivity
{
    // Error Handler Log Search
    private static final String m_Tag = "Krono_Firebase_Log";

    private TextView title;
    private TextView startTime;
    private TextView description;
    private ArrayList<String> activityIds;

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

        title = (TextView) findViewById(R.id.txtTitle);
        startTime = (TextView) findViewById(R.id.txtStartTime);
        description = (TextView)findViewById(R.id.txtDescription);
        activityIds = null;

        myActivities_RecyclerView = (RecyclerView) findViewById(R.id.recyclerMyActivities);
        planActivities_RecyclerView = (RecyclerView) findViewById(R.id.recyclerPlan);

        //Initialize plan activities recycler view
        InitPlanActivitiesRecycler();

        //Initializes user activities recycler view
        InitMyActivitiesRecycler();

        //Show user activities in 'My Activities' recycler view
        PopulateMyActivitiesRecycler();
    }

    public void createPlanOnClick(View view)
    {
        Map<String, Object> userPlan = new HashMap<>();
        final Map<String, Object> userActivity = new HashMap<>();

        Toast emptyTextFailureMessage = Toast.makeText(NewPlan.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        //check if title and start time are not empty
        if (title.getText().toString().trim().compareTo("") != 0 && startTime.getText().toString().trim().compareTo("") != 0)
        {
            userPlan.put("ownerId", FirebaseAuth.getInstance().getUid());
            userPlan.put("title", title.getText().toString());
            userPlan.put("description", description.getText().toString());
            userPlan.put("startTime", startTime.getText().toString());

            final Toast successMessage = Toast.makeText(NewPlan.this,
                    "Plan Added Successfully.", Toast.LENGTH_SHORT);
            final Toast failureMessage = Toast.makeText(NewPlan.this,
                    "Error: Could Not Add Plan.", Toast.LENGTH_SHORT);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("plans")
                    .add(userPlan)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference)
                        {
                            for (int i = 0; i < planActivities_ActivityList.size(); i++)
                            {
                                userActivity.put("title", planActivities_ActivityList.get(i).getTitle());
                                userActivity.put("description", planActivities_ActivityList.get(i).getDescription());
                                userActivity.put("duration", planActivities_ActivityList.get(i).getDuration());
                                userActivity.put("isPublic", planActivities_ActivityList.get(i).IsPublic());
                                userActivity.put("ownerId", FirebaseAuth.getInstance().getUid());

                                db.collection("users")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("plans")
                                        .document(documentReference.getId())
                                        .collection("activities")
                                        .add(userActivity);
                            }
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

    //Sets up 'My Activities' recycler view
    private void PopulateMyActivitiesRecycler() {
        /* Query The Database */
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("activities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        /* Grab the iterator from the built in google library */
                        Iterator<QueryDocumentSnapshot> iterator = queryDocumentSnapshots.iterator();

                        /* Reset the activity list view */
                        if (!myActivities_ActivityList.isEmpty()) {
                            myActivities_ActivityList.clear();
                        }

                        /* Populate the list of activities using an iterator */
                        while (iterator.hasNext())
                        {
                            QueryDocumentSnapshot snapshot = iterator.next();

                            Activity activity = snapshot.toObject(Activity.class);
                            activity.setId(snapshot.getId());
                            myActivities_ActivityList.add(activity);
                        }

                        /* Notify the adapter that the data has changed */
                        myActivities_ActivityListAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void InitPlanActivitiesRecycler()
    {
        // List of Activity (Class) -> Activity List Adapter -> Recycler View (XML)
        planActivities_ActivityList = new ArrayList<>();
        planActivities_ActivityListAdapter = new ActivityRVAdapter(planActivities_ActivityList);

        planActivities_RecyclerView.setHasFixedSize(true);
        planActivities_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        planActivities_RecyclerView.setAdapter(planActivities_ActivityListAdapter);
    }

    private void InitMyActivitiesRecycler()
    {
        // List of Activity (Class) -> Activity List Adapter -> Recycler View (XML)
        myActivities_ActivityList = new ArrayList<>();
        myActivities_ActivityListAdapter = new ActivityRVAdapter(myActivities_ActivityList);
        myActivities_ActivityListAdapter.setInActivitiesMenu(false);

        myActivities_RecyclerView.setHasFixedSize(true);
        myActivities_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myActivities_RecyclerView.setAdapter(myActivities_ActivityListAdapter);
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
