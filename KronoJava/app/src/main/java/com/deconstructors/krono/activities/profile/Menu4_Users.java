package com.deconstructors.krono.activities.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Menu4_Users extends AppCompatActivity
{
    public static final String USER_NAME = "com.deconstructors.krono.USER_NAME";

    private EditText _firstNameField;
    private EditText _lastNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu4__users);
        _firstNameField = findViewById(R.id.inputFirstName);
        _lastNameField = findViewById(R.id.inputLastName);

        GetUserInfo();
    }

    private void GetUserInfo()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference activitiesCollectionRef = db.collection("users");

        DocumentReference docRef = db
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        _firstNameField.setText(document.get("firstname").toString());
                        _lastNameField.setText(document.get("lastname").toString());
                    }
                }
            }
        });
    }

    public void btnDisplayPersonClick(View view)
    {

    }
}
