package com.deconstructors.krono.ui;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.PlanAdapter;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FriendPage_Detail extends AppCompatActivity implements View.OnClickListener,
                                                                    AppBarLayout.OnOffsetChangedListener,
                                                                    TabLayout.OnTabSelectedListener,
                                                                    PlanAdapter.PlanClickListener
{
    // Error Log
    private static final String TAG = "FriendDetailPage";

    //result constant for extra
    private Toolbar Toolbar;
    private AppBarLayout AppBarLayout;
    private ImageView Profile;
    private TextView DisplayName;
    private TextView Email;
    private TextView Bio;
    private TabLayout Tabs;

    private FirebaseFirestore DBInstance;

    private Query PublicPlanQuery;
    private FirestoreRecyclerOptions PublicPlanOptions;
    private Query SharedPlanQuery;
    private FirestoreRecyclerOptions SharedPlanOptions;

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

        this.Tabs = findViewById(R.id.friend_detail_tablayout);
        Tabs.addOnTabSelectedListener(this);

        this.PlansRecycler = findViewById(R.id.friend_detail_plans);
        this.PlansRecycler.setHasFixedSize(true);
        this.PlansRecycler.setLayoutManager(new LinearLayoutManager(this));

        this.Tabs.selectTab(Tabs.getTabAt(0));
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
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void deleteFriend()
    {

    }

    /************************************************************************
     * Purpose:         Click Listener
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View v)
    {

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
                finish();
                break;
        }
        return true;
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
        //nothing (for now)
    }
}
