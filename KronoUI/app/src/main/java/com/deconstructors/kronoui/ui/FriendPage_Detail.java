package com.deconstructors.kronoui.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.module.User;

public class FriendPage_Detail extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "FriendDetailPage";

    //result constant for extra
    private Toolbar Toolbar;
    private TextView DisplayName;
    private TextView Email;
    private TextView Bio;

    // Vars
    private User Friend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail);

        this.setTitle();
        this.setContents();
        this.getFriendIntent();
    }

    /************************************************************************
     * Purpose:         Set Toolbar & Inflate Toolbar Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setTitle()
    {
        this.Toolbar = findViewById(R.id.FriendPageDetail_Toolbar);
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
        this.DisplayName = findViewById(R.id.FriendPageDetail_DisplayName);
        this.Email = findViewById(R.id.FriendPageDetail_Email);
        this.Bio = findViewById(R.id.FriendPageDetail_Bio);
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
}
