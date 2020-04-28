package com.deconstructors.krono.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ProfilePage_Edit extends AppCompatActivity implements View.OnClickListener {

    private androidx.appcompat.widget.Toolbar _toolbar;

    private TextView NameTextView;
    private TextView EmailTextView;
    private TextView BioTextView;

    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private ListenerRegistration UserRegistration;

    private FloatingActionButton FAB;

    // Database
    private FirebaseFirestore FirestoreDB;

    //Profile Data
    Map<String, Object> profile = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);

        //Load Profile Data
        setToolbar();
        setUserDB();
        setContents();
    }

    private void setToolbar() {
        this._toolbar = findViewById(R.id.profileEdit_toolbar);
        this.setSupportActionBar(this._toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setUserDB()
    {
        // User
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBInstance = FirebaseFirestore.getInstance();

        this.UserRegistration = this.DBInstance
                .collection(getString(R.string.collection_users))
                .document(this.AuthInstance.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot,
                                        @androidx.annotation.Nullable FirebaseFirestoreException e)
                    {
                        if (documentSnapshot != null)
                        {
                            NameTextView.setText(documentSnapshot.get("displayName").toString());
                            EmailTextView.setText(documentSnapshot.get("email").toString());
                            BioTextView.setText(documentSnapshot.get("bio").toString());
                        }
                    }
                });
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void saveProfile()
    {
        profile.put("name", this.NameTextView.getText().toString());
        profile.put("email", this.EmailTextView.getText().toString());
        profile.put("bio", this.BioTextView.getText().toString());

        this.DBInstance.collection(getString(R.string.collection_users))
                .document(this.AuthInstance.getCurrentUser().getUid())
                .update(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(ProfilePage_Edit.this, "Edit Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setContents()
    {
        this.NameTextView = findViewById(R.id.profileEdit_titleText);
        this.EmailTextView = findViewById(R.id.profileEdit_emailText);
        this.BioTextView = findViewById(R.id.profileEdit_bioText);

        this.FAB = findViewById(R.id.profiledetail_fab);
        this.FAB.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.profiledetail_fab:
            {
                this.saveProfile();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Back Button Animation Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Back Button Animation Override
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
