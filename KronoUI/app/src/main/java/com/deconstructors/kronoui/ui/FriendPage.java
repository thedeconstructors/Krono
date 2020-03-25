package com.deconstructors.kronoui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.adapter.FriendAdapter;
import com.deconstructors.kronoui.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

import androidx.appcompat.widget.Toolbar;

public class FriendPage extends AppCompatActivity implements FriendAdapter.FriendClickListener,
                                                             View.OnClickListener
{
    // Error Log
    private static final String TAG = "FriendPage";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView RecyclerView;
    private FloatingActionButton FAB;

    // Database
    private FirebaseFirestore DBInstance;
    private List<String> FriendList;
    private ListenerRegistration FriendRegistration;
    private Query FriendQuery;
    private FirestoreRecyclerOptions<User> FriendOptions;
    private FriendAdapter FriendAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_main);

        this.setToolbar();
        this.setDatabase();
        this.setContents();
    }

    /************************************************************************
     * Purpose:         Set Toolbar & Inflate Toolbar Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.friend_toolbar);
        this.Toolbar.setTitle(getString(R.string.menu_friends));
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_main, menu);

        return true;
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setDatabase()
    {
        this.DBInstance = FirebaseFirestore.getInstance();
        this.FriendQuery = this.DBInstance
                .collection(getString(R.string.collection_users))
                .whereArrayContains("friendList", FirebaseAuth.getInstance().getCurrentUser().getUid());
        this.FriendOptions = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(this.FriendQuery, User.class)
                .build();
        this.FriendAdapter = new FriendAdapter(this.FriendOptions, this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.FriendAdapter != null) { this.FriendAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.FriendAdapter != null) { this.FriendAdapter.stopListening(); }
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Recycler View
        this.RecyclerView = findViewById(R.id.friend_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(this.FriendAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Others Widgets
        this.FAB = findViewById(R.id.friend_fab);
        this.FAB.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Parcelable Friend Interaction
     * Precondition:    .
     * Postcondition:   Go to Friend Details page
     ************************************************************************/
    @Override
    public void onFriendSelected(int position)
    {
        Intent intent = new Intent(FriendPage.this, FriendPage_Detail.class);
        intent.putExtra(getString(R.string.intent_friend), this.FriendAdapter.getItem(position));
        startActivity(intent);
    }

    /************************************************************************
     * Purpose:         Floating Action Button onClick Overrides
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
