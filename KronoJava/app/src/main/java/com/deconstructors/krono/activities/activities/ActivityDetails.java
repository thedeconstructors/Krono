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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivityDetails extends AppCompatActivity
{
    final String AcitivityExtra = "activity_name";

    /*Activity to be displayed*/
    private TextView title;
    private TextView description;
    private TextView duration;
    private TextView isPublic;

    String clicked_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu0_activitydetails);
        findViewById(R.id.activity_title);

        //String For Finding current document
        clicked_activity = getIntent().getExtras().getString(AcitivityExtra);

        title = findViewById(R.id.activity_title);
        description = findViewById(R.id.description);
        duration = findViewById(R.id.date_time);
        isPublic = findViewById(R.id.is_public);

        PopulateDetails();
    }

    void PopulateDetails()
    {
        /* Query The Database */
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(SessionData.GetInstance().GetUserID())
                .collection("activities")
                .document(clicked_activity)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshot) {

                        title.setText(queryDocumentSnapshot.get("title").toString());
                        description.setText(queryDocumentSnapshot.get("description").toString());
                        duration.setText(queryDocumentSnapshot.get("timestamp").toString());
                        isPublic.setText(queryDocumentSnapshot.get("public").toString());
                    }
                });
    }
}
