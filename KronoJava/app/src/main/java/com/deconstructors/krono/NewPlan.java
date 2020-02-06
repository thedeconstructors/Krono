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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPlan extends AppCompatActivity
{
    private TextView title;
    private TextView description;
    private Switch isPublic;
    private Switch isCollaborative;

    Map<String, Object> userPlan = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_menu1_plans_newplan);

        title = findViewById(R.id.txtTitle);
        description = findViewById(R.id.txtDescription);
        isPublic = findViewById(R.id.swPublic);
        //isCollaborative = findViewById(R.id.swCollab);
    }

    public void createPlanOnClick(View view)
    {
        Toast emptyTextFailureMessage = Toast.makeText(NewPlan.this, "Must Enter All Text Fields", Toast.LENGTH_SHORT);

        if (title != null && description != null)
        {
            userPlan.put("title", title.getText().toString());
            userPlan.put("description", description.getText().toString());
            userPlan.put("isPublic", isPublic.isChecked());
            userPlan.put("isCollaborative", isCollaborative);
        }
        else
        {
            emptyTextFailureMessage.show();
        }
    }
}
