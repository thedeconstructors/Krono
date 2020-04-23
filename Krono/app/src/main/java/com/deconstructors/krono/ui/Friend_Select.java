package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.FriendAdapter;
import com.deconstructors.krono.adapter.FriendAdapter_Selectable;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Friend_Select extends AppCompatActivity
                        implements com.deconstructors.krono.adapter.FriendAdapter_Selectable.FriendClickListener {
    // Error Log
    private static final String TAG = "FriendPage";

    // XML Widgets
    private androidx.appcompat.widget.Toolbar Toolbar;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private Query FriendQuery;
    private FirestoreRecyclerOptions<User> FriendOptions;
    private com.deconstructors.krono.adapter.FriendAdapter_Selectable FriendAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_select);

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
        // Friend Info
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBInstance = FirebaseFirestore.getInstance();
        this.FriendQuery = this.DBInstance
                .collection(getString(R.string.collection_users))
                .whereArrayContains("friends", this.AuthInstance.getCurrentUser().getUid());
        this.FriendOptions = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(this.FriendQuery, User.class)
                .build();
        this.FriendAdapter = new FriendAdapter_Selectable(this.FriendOptions, this);
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
        this.RecyclerView = findViewById(R.id.FriendPage_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(this.FriendAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    /************************************************************************
     * Purpose:         Parcelable Friend Interaction
     * Precondition:    .
     * Postcondition:   Go to Friend Details page
     ************************************************************************/
    @Override
    public void onFriendSelected(int position)
    {
        //does nothing but multiselect
        CheckBox cb = RecyclerView.getChildAt(position).findViewById(R.id.friendlistitem_checkbox);
        cb.setChecked(!cb.isChecked());
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
