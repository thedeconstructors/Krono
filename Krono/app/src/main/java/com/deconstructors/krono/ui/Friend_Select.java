package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
                        implements FriendAdapter_Selectable.FriendClickListener,
                                    View.OnClickListener {
    // Error Log
    private static final String TAG = "FriendPage";

    //activity results
    final int AR_COLLAB = 5;
    final String EXTRA_COLLAB = "COLLAB";

    // Collaborators
    List<String> Collaborators;

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
        this.FriendAdapter = new FriendAdapter_Selectable(this.FriendOptions,
                this,
                new FriendAdapter_Selectable.SelectModifer() {
                    @Override
                    public void selectionCheck(User model, FriendAdapter_Selectable.FriendHolder holder) {
                        for (String id : Collaborators)
                        {
                            if (id.compareTo(model.getUid()) == 0)
                            {
                                holder.cb.setChecked(true);
                            }
                        }
                    }
                });
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
        findViewById(R.id.FriendSelect_FAB).setOnClickListener(this);

        Collaborators = getIntent().getStringArrayListExtra(EXTRA_COLLAB);
    }

    @Override
    public void onClick(View view)
    {
        Collaborators = new ArrayList<>();
        for (int ii = 0; ii < RecyclerView.getChildCount(); ++ii)
        {
            View item = RecyclerView.getChildAt(ii);
            CheckBox cb = item.findViewById(R.id.friendlistitem_checkbox);
            if (cb.isChecked())
            {
                Collaborators.add(FriendAdapter.getItem(ii).getUid());
            }
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_COLLAB,new ArrayList<String>(Collaborators));
        setResult(AR_COLLAB, data);
        finish();
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
