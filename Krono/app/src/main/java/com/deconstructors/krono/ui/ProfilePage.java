package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import javax.annotation.Nullable;

public class ProfilePage extends AppCompatActivity {

    private TextView NameTextView;
    private TextView EmailTextView;
    private TextView BioTextView;

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
        // User
        // Database
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
                            NameTextView.setText(Objects.requireNonNull(documentSnapshot.get("displayName")).toString());
                            EmailTextView.setText(Objects.requireNonNull(documentSnapshot.get("email")).toString());
                            BioTextView.setText(Objects.requireNonNull(documentSnapshot.get("bio")).toString());

                            //Load the users current profile picture
                            Picasso.get().load(
                                    Objects.requireNonNull(
                                            documentSnapshot.get("picture")
                                    ).toString()
                            ).into((ImageView)findViewById(R.id.profile_picture));
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
