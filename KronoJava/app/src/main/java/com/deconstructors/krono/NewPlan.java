package com.deconstructors.krono;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
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

    Map<String, Object> userPlan = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1_plans_newplan);

        title = findViewById(R.id.txtTitle);
    }

    public void createPlanOnClick(View view)
    {
        Toast emptyTextFailureMessage = Toast.makeText(NewPlan.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        if (title.getText().toString() != null && startTime.getText().toString() != null)
        {
            userPlan.put("title", title.getText().toString());
            userPlan.put("starTime", startTime.getText().toString());

            final Toast successMessage = Toast.makeText(NewPlan.this, "Plan Added Successfully.", Toast.LENGTH_SHORT);
            final Toast failureMessage = Toast.makeText(NewPlan.this, "Error: Could Not Add Plan.", Toast.LENGTH_SHORT);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("useractivities")
                    .add(userPlan)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
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
}
