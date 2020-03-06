package com.deconstructors.krono.activities.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityDetails extends AppCompatActivity
{
    final String AcitivityExtra = "activity_name";
    //result constant for extra
    final String RESULT_REFRESH = "RESULT_REFRESH";
    final int IRESULT_REFRESH = 1;

    /*Activity to be displayed*/
    private TextView title;
    private TextView description;
    private TextView duration;
    private TextView isPublic;

    private ProgressBar _progress;

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

        _progress = findViewById(R.id.menu0_activities_activitydetails_progress);

        PopulateDetails();
    }

    void RemoveActivity()
    {
        _progress.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(SessionData.GetInstance().GetUserID())
                .collection("activities")
                .document(clicked_activity)
                .delete()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_REFRESH,true);
                    setResult(IRESULT_REFRESH, intent);
                    _progress.setVisibility(View.GONE);
                    finish();
                }
                else
                {
                    Toast.makeText(ActivityDetails.this,
                            "Unable to remove activity ):",
                                Toast.LENGTH_SHORT);
                    _progress.setVisibility(View.GONE);
                }
            }
        });
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
                        duration.setText(queryDocumentSnapshot.get("duration").toString());
                        isPublic.setText(queryDocumentSnapshot.get("isPublic").toString());
                    }
                });
    }

    public void DeleteActivityOnClick(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(ActivityDetails.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this activity?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemoveActivity();
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
