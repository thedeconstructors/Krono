package com.deconstructors.krono.activities.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewActivity extends AppCompatActivity
{
    private TextView title;
    private TextView description;
    private TextView duration;
    private Switch isPublic;

    Map<String, Object> userActivity = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_newactivity);

        title = findViewById(R.id.txtTitle);
        description = findViewById(R.id.txtDescription);
        duration = findViewById(R.id.txtDuration);
        isPublic = findViewById(R.id.swPublic);
    }

    public void createActivityOnClick(View view)
    {
        Toast emptyTextFailureMessage = Toast.makeText(NewActivity.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        // Use 'stringX.getText().toString().matches("")' instead of 'stringX == null'
        if (!title.getText().toString().matches("") &&
                !description.getText().toString().matches("") &&
                !duration.getText().toString().matches(""))
        {
            userActivity.put("title", title.getText().toString());
            userActivity.put("description", description.getText().toString());
            userActivity.put("timestamp", new Timestamp(new Date(0,0,0,0,45)));
            userActivity.put("isPublic", isPublic.isChecked());
            userActivity.put("ownerId", SessionData.GetInstance().GetUserID());

            final Toast successMessage = Toast.makeText(NewActivity.this, "Activity Added Successfully.", Toast.LENGTH_SHORT);
            final Toast failureMessage = Toast.makeText(NewActivity.this, "Error: Could Not Add Activity.", Toast.LENGTH_SHORT);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("useractivities")
                    .add(userActivity)
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

            finish();
        }
        else
        {
            emptyTextFailureMessage.show();
        }
    }

    public void cancleNewActivityOnClick(View view)
    {
        finish();
    }

    public void cancelOnClick(View view)
    {
        finish();
    }
}
