package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.PlanAdapter;
import com.deconstructors.krono.auth.RegisterPage;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.module.User;
import com.deconstructors.krono.utility.Helper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FriendPage_Detail extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
                                                                    TabLayout.OnTabSelectedListener,
                                                                    PlanAdapter.PlanClickListener
{
    // Error Log
    private static final String TAG = "FriendDetailPage";

    //result constant for extra
    private CoordinatorLayout Background;
    private Toolbar Toolbar;
    private AppBarLayout AppBarLayout;
    private ImageView Profile;
    private TextView DisplayName;
    private TextView Email;
    private TextView Bio;
    private TabLayout Tabs;

    private FirebaseFirestore DBInstance;
    private FirebaseAuth AuthInstance;

    private Query PublicPlanQuery;
    private FirestoreRecyclerOptions PublicPlanOptions;
    private Query SharedPlanQuery;
    private FirestoreRecyclerOptions SharedPlanOptions;
    private FirebaseFunctions DBFunctions;

    private RecyclerView PlansRecycler;
    private PlanAdapter PublicPlansAdapter;
    private PlanAdapter SharedPlansAdapter;

    // Vars
    private User Friend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail);

        this.setToolbar();
        this.getFriendIntent();
        this.setPlansDB();
        this.setContents();
    }

    /************************************************************************
     * Purpose:         Set Toolbar & Inflate Toolbar Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.FriendPageDetail_Toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_detail, menu);

        return true;
    }

    /************************************************************************
     * Purpose:         Sets database interaction
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setPlansDB()
    {
        this.DBInstance = FirebaseFirestore.getInstance();
        this.DBFunctions = FirebaseFunctions.getInstance();
        this.AuthInstance = FirebaseAuth.getInstance();

        this.PublicPlanQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID", this.Friend.getUid())
                .whereEqualTo("public",true);
        this.PublicPlanOptions = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(this.PublicPlanQuery, Plan.class)
                .build();
        this.PublicPlansAdapter = new PlanAdapter(this.PublicPlanOptions, this);

        this.SharedPlanQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .whereEqualTo("ownerID",this.Friend.getUid())
                .whereArrayContains("collaborators",FirebaseAuth.getInstance().getCurrentUser().getUid());
        this.SharedPlanOptions = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(this.SharedPlanQuery, Plan.class)
                .build();
        this.SharedPlansAdapter = new PlanAdapter(this.SharedPlanOptions, this);
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        this.AppBarLayout = findViewById(R.id.FriendDetaiPage_Appbar);
        this.AppBarLayout.addOnOffsetChangedListener(this);

        this.Profile = findViewById(R.id.FriendPageDetail_Profile);
        this.DisplayName = findViewById(R.id.FriendPageDetail_DisplayName);
        this.Email = findViewById(R.id.FriendPageDetail_Email);
        this.Bio = findViewById(R.id.FriendPageDetail_Bio);

        this.getSupportActionBar().setTitle(this.Friend.getDisplayName());

        this.DisplayName.setText(this.Friend.getDisplayName());
        this.Email.setText(this.Friend.getEmail());
        this.Bio.setText(this.Friend.getBio());

        //Profile Picture
        this.DBInstance
                .collection(getString(R.string.collection_users))
                .document(Friend.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            Object pic_url;
                            if ((pic_url = documentSnapshot.get("picture")) == null) {
                                pic_url = getString(R.string.default_picture);
                            }

                            Picasso.get().load(pic_url.toString()).into(Profile);

                        }
                });

        this.Tabs = findViewById(R.id.friend_detail_tablayout);
        Tabs.addOnTabSelectedListener(this);

        this.PlansRecycler = findViewById(R.id.friend_detail_plans);
        this.PlansRecycler.setHasFixedSize(true);
        this.PlansRecycler.setLayoutManager(new LinearLayoutManager(this));

        this.Tabs.selectTab(Tabs.getTabAt(0));
        this.Background = findViewById(R.id.ui_friendDetailLayout);
    }

    /************************************************************************
     * Purpose:         Parcelable Friend Interaction
     * Precondition:    .
     * Postcondition:   Get Plan Intent from MainActivity
     ************************************************************************/
    private void getFriendIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_friend)))
        {
            this.Friend = getIntent().getParcelableExtra(getString(R.string.intent_friend));
        }
        else
        {
            finish();
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Back Button Animation Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                break;
            }
            case R.id.Menu_FriendPageDetail_DeleteFriend:
            {
                this.deleteFriend();
                break;
            }
        }
        return true;
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void deleteFriend()
    {
        Map<String, Object> users = new HashMap<>();
        users.put(getString(R.string.collection_friends) + "." + this.Friend.getUid(), FieldValue.delete());
        Log.d(TAG, "Detail: " + getString(R.string.collection_friends) + "." + this.Friend.getUid());

        // Delete the friend relationship from the Owner's Document
        this.DBInstance
                .collection(getString(R.string.collection_users))
                .document(this.AuthInstance.getCurrentUser().getUid())
                .update(users)
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
                        Helper.makeSnackbarMessage(FriendPage_Detail.this.Background,
                                                   "Friend Delete Error: " + e.getMessage());
                    }
                });

        // Delete the friend relationship from the Friend's Document
        this.getDeleteFriendFunctions(this.Friend.getUid())
            .addOnSuccessListener(new OnSuccessListener<String>()
            {
                @Override
                public void onSuccess(String s)
                {
                    /*Helper.makeSnackbarMessage(FriendPage_Detail.this.Background,
                                               "Friend Delete Success");*/
                }
            })
            .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Log.d(TAG, "getDeleteFriendFunctions: " + e.getMessage());
                    /*Helper.makeSnackbarMessage(FriendPage_Detail.this.Background,
                                               "Friend Delete Error: " + e.getMessage());*/
                }
            });
    }

    private Task<String> getDeleteFriendFunctions(String friendID)
    {
        // Create the arguments to the callable function.
        Map<String, Object> snap = new HashMap<>();
        snap.put("friendID", friendID);
        snap.put("push", true);

        return this.DBFunctions
                .getHttpsCallable("deleteFriend")
                .call(snap)
                .continueWith(new Continuation<HttpsCallableResult, String>()
                {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception
                    {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch(tab.getPosition())
        {
            case 0:
                this.PlansRecycler.setAdapter(this.PublicPlansAdapter);
                this.PublicPlansAdapter.startListening();
                this.SharedPlansAdapter.stopListening();
                break;
            case 1:
                this.PlansRecycler.setAdapter(this.SharedPlansAdapter);
                this.SharedPlansAdapter.startListening();
                this.PublicPlansAdapter.stopListening();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //nothing
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        switch(tab.getPosition())
        {
            case 0:
                this.PlansRecycler.setAdapter(this.PublicPlansAdapter);
                this.PublicPlansAdapter.startListening();
                this.SharedPlansAdapter.stopListening();
                break;
            case 1:
                this.PlansRecycler.setAdapter(this.SharedPlansAdapter);
                this.SharedPlansAdapter.startListening();
                this.PublicPlansAdapter.stopListening();
                break;
        }
    }

    @Override
    public void onPlanSelected(int position) {
        Intent intent = new Intent(this, ActivityPage.class);

        PlanAdapter chosenRecycler;
        ActivityPage.EditMode canEdit;

        if (PlansRecycler.getAdapter() == PublicPlansAdapter) {
            chosenRecycler = PublicPlansAdapter;
            canEdit = ActivityPage.EditMode.PUBLIC;
        }
        else {
            chosenRecycler = SharedPlansAdapter;
            canEdit = ActivityPage.EditMode.COLLAB;
        }

        intent.putExtra(getString(R.string.intent_plans), chosenRecycler.getItem(position));
        intent.putExtra(getString(R.string.intent_editable), canEdit);

        startActivity(intent);
    }
}
