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

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class FriendPage_Detail extends AppCompatActivity implements View.OnClickListener,
                                                                    AppBarLayout.OnOffsetChangedListener,
                                                                    TabLayout.OnTabSelectedListener
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

    // Vars
    private User Friend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail);

        this.setToolbar();
        this.setContents();
        this.getFriendIntent();
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

        this.Tabs = findViewById(R.id.friend_detail_tablayout);
        Tabs.addOnTabSelectedListener(this);
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
            this.getSupportActionBar().setTitle(this.Friend.getDisplayName());

            this.DisplayName.setText(this.Friend.getDisplayName());
            this.Email.setText(this.Friend.getEmail());
            this.Bio.setText(this.Friend.getBio());
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
                Toast.makeText(this, "Here are PUBLIC plans", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "Here are SHARED plans", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //nothing
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //nothing
    }
}
