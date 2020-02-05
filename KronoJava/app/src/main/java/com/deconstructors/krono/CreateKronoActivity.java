package com.deconstructors.krono;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CreateKronoActivity extends AppCompatActivity
{
    private TextView title;
    private TextView description;
    private TextView duration;

    Map<String, Object> userActivity = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //These four lines of code will not work until merged with Codey's branch
        setContentView(R.layout.activity_menu0__activities_newactivity);
        title = findViewById(R.id.txtTitle);
        description = findViewById(R.id.txtDescription);
        duration = findViewById(R.id.txtDuration);
    }

    public void createActivityOnClick()
    {
        Toast emptyTextFailureMessage = Toast.makeText(CreateKronoActivity.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        if (title != null && description != null && duration != null)
        {
            userActivity.put("title", title.getText().toString());
            userActivity.put("description", description.getText().toString());
            userActivity.put("duration", duration.getText().toString());

            final Toast successMessage = Toast.makeText(CreateKronoActivity.this, "Activity Added Successfully.", Toast.LENGTH_SHORT);
            final Toast failureMessage = Toast.makeText(CreateKronoActivity.this, "Error: Could Not Add Activity.", Toast.LENGTH_SHORT);

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
        }
        else
        {
            emptyTextFailureMessage.show();
        }
    }
}