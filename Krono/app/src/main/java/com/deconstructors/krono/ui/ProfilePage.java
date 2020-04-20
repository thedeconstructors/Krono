package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

public class ProfilePage extends AppCompatActivity {

    // Error Log
    private static final String TAG = "ProfilePage";

    // XML Widgets
    private androidx.appcompat.widget.Toolbar _toolbar;
    private TextView NameTextView;
    private TextView EmailTextView;
    private TextView BioTextView;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private Query PlanQuery;
    private FirestoreRecyclerOptions<Plan> PlanOptions;
    private com.deconstructors.krono.adapter.PlanAdapter PlanAdapter;
    private ListenerRegistration UserRegistration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        this.setUserDB();
        this.setToolbar();
        this.setContents();
    }

    private void setToolbar() {
        this._toolbar = findViewById(R.id.profile_Toolbar);
        this.setSupportActionBar(this._toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        this.NameTextView = findViewById(R.id.profile_DisplayName);
        this.EmailTextView = findViewById(R.id.profile_Email);
        this.BioTextView = findViewById(R.id.profile_Bio);
    }

    public void profileEdit(View view) {
        startActivity(new Intent(ProfilePage.this, ProfilePage_Edit.class));
    }
}
