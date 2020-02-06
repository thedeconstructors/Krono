package com.deconstructors.krono;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewActivity extends AppCompatActivity
{
    public static final String USER_NAME = "com.deconstructors.krono.USER_NAME";

    private TextView title;
    private TextView description;
    private TextView duration;

    Map<String, Object> userActivity = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu0_activities_newactivity);
    }

    public void createActivityOnClick(View view)
    {
        Toast emptyTextFailureMessage = Toast.makeText(NewActivity.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        // We need to get values from the user first
        title = (TextView) findViewById(R.id.txtTitle);
        description = (TextView) findViewById(R.id.txtDescription);
        duration = (TextView) findViewById(R.id.txtDuration);

        // Use 'stringX.getText().toString().matches("")' instead of 'stringX == null'
        if (!title.getText().toString().matches("") && !description.getText().toString().matches("") && !duration.getText().toString().matches(""))
        {
            userActivity.put("title", title.getText().toString());
            userActivity.put("description", description.getText().toString());
            userActivity.put("duration", duration.getText().toString());

            final Toast successMessage = Toast.makeText(NewActivity.this, "Activity Added Successfully.", Toast.LENGTH_SHORT);
            final Toast failureMessage = Toast.makeText(NewActivity.this, "Error: Could Not Add Activity.", Toast.LENGTH_SHORT);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("useractivities")
                    .add(userActivity)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            successMessage.show();

                            //Not exactly if this is correct. Copied from line 84 of Kacey's LoginEmailPassRegister code.
                            documentReference.update("id", documentReference.getId());
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
}
