package com.deconstructors.krono;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPlan extends AppCompatActivity
{
    private TextView title;
    private TextView startTime;
    private RecyclerView activities;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1_plans_newplan);

        title = findViewById(R.id.txtTitle);
        startTime = findViewById(R.id.txtStartTime);
    }

    public void createPlanOnClick(View view)
    {
        Map<String, Object> userPlan = new HashMap<>();

        Toast emptyTextFailureMessage = Toast.makeText(NewPlan.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        if (title.getText().toString() != null && startTime.getText().toString() != null)
        {
            userPlan.put("title", title.getText().toString());
            userPlan.put("startTime", startTime.getText().toString());

            final Toast successMessage = Toast.makeText(NewPlan.this, "Plan Added Successfully.", Toast.LENGTH_SHORT);
            final Toast failureMessage = Toast.makeText(NewPlan.this, "Error: Could Not Add Plan.", Toast.LENGTH_SHORT);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("userplans")
                    .add(userPlan)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            UpdateUsersToPlans();
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

    public void UpdateUsersToPlans()
    {
        Map<String, Object> userToPlan = new HashMap<>();

        //Currently using GetUserID() as the value for planID until I understand what we're supposed to be using as the planID
        userToPlan.put("planid", SessionData.GetInstance().GetUserID());
        userToPlan.put("userid", SessionData.GetInstance().GetUserID());

        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        db2.collection("userstoplans")
                .add(userToPlan);
    }
}
