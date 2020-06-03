package com.deconstructors.krono.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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

public class ProfilePage extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener
{

    private TextView NameTextView;
    private TextView EmailTextView;
    private TextView BioTextView;
    private TextView NumberOfPlans;
    private TextView NumberOfFriends;

    private CollapsingToolbarLayout ToolbarLayout;
    private AppBarLayout AppBarLayout;
    private ImageView Profile;
    private Toolbar Toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        this.setUserDB();
        this.setToolbar();
        this.setContents();
    }

    private void setToolbar()
    {
        // XML Widgets
        this.Toolbar = findViewById(R.id.profile_Toolbar);
        this.Toolbar.setTitle(getString(R.string.menu_profile));
        this.setSupportActionBar(this.Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

        this.AppBarLayout = findViewById(R.id.profilePage_Appbar);
        this.AppBarLayout.addOnOffsetChangedListener(this);
        this.Profile = findViewById(R.id.profile_picture);

        this.ToolbarLayout = findViewById(R.id.profile_coltoolbar);
        //this.ToolbarLayout.setExpandedTitleColor(getColor(R.color.transparent));
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

                            try
                            {
                                Map<String, Boolean> friendMap = (Map<String, Boolean>) documentSnapshot.get(getString(R.string.collection_friends));

                                if (friendMap != null)
                                {
                                    NumberOfFriends.setText(String.valueOf(friendMap.size()));
                                }
                            }
                            catch (Exception innner)
                            {
                                NumberOfFriends.setText(String.valueOf(0));
                            }

                            Object pic_url;
                            if ((pic_url = documentSnapshot.get(getString(R.string.profilepicture))) == null) {
                                pic_url = getString(R.string.default_picture);
                            }

                            Picasso.get().load(pic_url.toString()).into(
                                    Profile
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.profile_edit_icon)
        {
            startActivity(new Intent(ProfilePage.this, ProfilePage_Edit.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /************************************************************************
     * Purpose:         Custom Profile Image Behavior
     * Precondition:    .
     * Postcondition:   Fades profile image as scroll up
     ************************************************************************/
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
        float percentage = (appBarLayout.getTotalScrollRange() - (float)Math.abs(verticalOffset))
                /appBarLayout.getTotalScrollRange();
        this.Profile.setAlpha(percentage);
    }
}
