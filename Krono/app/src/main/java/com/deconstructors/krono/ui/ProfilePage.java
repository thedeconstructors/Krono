package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class ProfilePage extends AppCompatActivity {

    private TextView NameTextView;
    private TextView EmailTextView;
    private TextView BioTextView;
    private TextView NumberOfPlans;
    private TextView NumberOfFriends;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        this.setUserDB();
        this.setToolbar();
        this.setContents();
    }

    private void setToolbar() {
        // XML Widgets
        androidx.appcompat.widget.Toolbar _toolbar = findViewById(R.id.profile_Toolbar);
        this.setSupportActionBar(_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
        // User Database
        FirebaseAuth authInstance = FirebaseAuth.getInstance();
        FirebaseFirestore DBInstance = FirebaseFirestore.getInstance();

        DBInstance.collection(getString(R.string.collection_users))
                .document(Objects.requireNonNull(authInstance.getCurrentUser()).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot,
                                        @androidx.annotation.Nullable FirebaseFirestoreException e)
                    {
                        if (documentSnapshot != null)
                        {
                            NameTextView.setText(Objects.requireNonNull(documentSnapshot.get(getString(R.string.users_displayname))).toString());
                            EmailTextView.setText(Objects.requireNonNull(documentSnapshot.get(getString(R.string.users_email))).toString());
                            BioTextView.setText(Objects.requireNonNull(documentSnapshot.get(getString(R.string.users_bio))).toString());

                            Map<String, Boolean> friendMap = 
                                    (Map<String, Boolean>)documentSnapshot.get(getString(R.string.friends));

                            if (friendMap != null) {
                                NumberOfFriends.setText(String.valueOf(friendMap.size()));
                            }

                            Object pic_url;
                            if ((pic_url = documentSnapshot.get(getString(R.string.profilepicture))) == null) {
                                pic_url = getString(R.string.default_picture);
                            }

                            Picasso.get().load(pic_url.toString()).into(
                                    (ImageView)findViewById(R.id.profile_picture)
                            );
                        }
                    }
                });

        DBInstance.collection(getString(R.string.collection_plans))
                .whereEqualTo(getString(R.string.activities_ownerID), authInstance.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    NumberOfPlans.setText(String.valueOf(Objects.requireNonNull(
                            task.getResult()).size()));
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
        this.NumberOfFriends = findViewById(R.id.friends_num);
        this.NumberOfPlans = findViewById(R.id.plan_number);
    }

    public void profileEdit(android.view.View view) {
        startActivity(new Intent(ProfilePage.this, ProfilePage_Edit.class));
    }
}
