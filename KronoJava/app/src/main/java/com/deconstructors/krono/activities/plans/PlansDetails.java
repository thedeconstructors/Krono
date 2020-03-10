package com.deconstructors.krono.activities.plans;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlansDetails  extends AppCompatActivity
{
    final String PlanExtra = "plan_name";

    // Plan to be displayed
    private TextView _title;
    private TextView _description;
    //private TextView _startTime;

    String clicked_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1_plansdetails);
        findViewById(R.id.plan_title);

        clicked_plan = getIntent().getExtras().getString(PlanExtra);

        _title = findViewById(R.id.plan_title);
        _description = findViewById(R.id.plan_desc);

        PopulateDetails();
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
                        _description.setText(queryDocumentSnapshot.get("title").toString());
                        //_startTime.setText(queryDocumentSnapshot.get("startTime").toString());
                    }
                });
    }
}
