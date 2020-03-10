package com.deconstructors.krono.activities.plans;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.activities.Activity;
import com.deconstructors.krono.activities.activities.ActivityRVAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;

public class PlansDetails extends AppCompatActivity
{
    final String PlanExtra = "plan_name";

    // Plan to be displayed
    private TextView _title;
    private TextView _description;
    private TextView _startTime;

    private RecyclerView _activitiesInPlanRecyclerView;
    private ArrayList<Activity> _activitiesInPlanList;
    private ActivityRVAdapter _activityListAdapter;

    String clicked_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1_plansdetails);
        findViewById(R.id.plan_details_title_text);

        clicked_plan = getIntent().getExtras().getString(PlanExtra);

        _title = findViewById(R.id.plan_details_title_text);
        _description = findViewById(R.id.plans_details_description_text);
        _startTime = findViewById(R.id.plans_details_start_time_text);

        _activitiesInPlanRecyclerView = (RecyclerView) findViewById(R.id.recyclerPlanDetailsActivities);
        _activitiesInPlanList = new ArrayList<>();
        _activityListAdapter = new ActivityRVAdapter(_activitiesInPlanList);
        _activityListAdapter.setInActivitiesMenu(false);

        _activitiesInPlanRecyclerView.setHasFixedSize(true);
        _activitiesInPlanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        _activitiesInPlanRecyclerView.setAdapter(_activityListAdapter);

        PopulateActivitiesRecycler();
        PopulateDetails();
    }

    private void PopulateActivitiesRecycler()
    {
        /* Query The Database */
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("plans")
                .document(clicked_plan)
                .collection("activities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        /* Grab the iterator from the built in google library */
                        Iterator<QueryDocumentSnapshot> iterator = queryDocumentSnapshots.iterator();

                        /* Reset the activity list view */
                        if (!_activitiesInPlanList.isEmpty()) {
                            _activitiesInPlanList.clear();
                        }

                        /* Populate the list of activities using an iterator */
                        while (iterator.hasNext()) {
                            QueryDocumentSnapshot snapshot = iterator.next();

                            Activity activity = snapshot.toObject(Activity.class);
                            activity.setId(snapshot.getId());
                            _activitiesInPlanList.add(activity);
                        }

                        // Notify the adapter that the data has changed
                    }
                });
    }

    private void PopulateDetails()
    {
        //Query The Database
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("plans")
                .document(clicked_plan)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshot) {

                        _title.setText(queryDocumentSnapshot.get("title").toString());
                        _description.setText(queryDocumentSnapshot.get("description").toString());
                        _startTime.setText(queryDocumentSnapshot.get("startTime").toString());


                    }
                });
    }

    public void RemovePlan()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i < _activitiesInPlanList.size(); i++) {
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getUid()) //Refactored to use UID
                    .collection("plans")
                    .document(clicked_plan)
                    .collection("activities")
                    .document(_activitiesInPlanList.get(i).getActivityID())
                    .delete();
        }

        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid()) //Refactored to use UID
                .collection("plans")
                .document(clicked_plan)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            finish();
                        }
                        else
                        {
                            Toast.makeText(PlansDetails.this,
                                    "Unable to remove plan ):",
                                    Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    public void DeletePlanOnClick(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(PlansDetails.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this plan?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemovePlan();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
}
