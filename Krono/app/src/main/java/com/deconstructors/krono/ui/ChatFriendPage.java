package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.FriendAdapter;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.widget.Toolbar;

public class ChatFriendPage extends AppCompatActivity implements FriendAdapter.FriendClickListener,
        SearchView.OnQueryTextListener
{
    // Error Log
    private static final String TAG = "ChatFriendPage";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView RecyclerView;
    private ChatFriendPage ChatFriendPage;
    private SearchView Search;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private Query FriendQuery;
    private FirestoreRecyclerOptions<User> FriendOptions;
    private FriendAdapter FriendAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_friends);

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
        this.Toolbar = findViewById(R.id.chatfriend_toolbar);
        this.Toolbar.setTitle(getString(R.string.menu_chat));
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_main, menu);

        MenuItem searchItem = menu.findItem(R.id.friend_menu_searchbutton);
        Search = (SearchView) searchItem.getActionView();
        Search.setQueryHint(getString(R.string.search_namehint));
        Search.setOnQueryTextListener(this);

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
                .whereEqualTo(getString(R.string.collection_friends)
                                + "."
                                + this.AuthInstance.getCurrentUser().getUid(),
                        1);
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
        if (Search != null)
            Search.setQuery("", false);
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Recycler View
        this.RecyclerView = findViewById(R.id.ChatFriendPage_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(this.FriendAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (this.FriendAdapter != null)
            this.FriendAdapter.getFilter().filter(newText);
        return true;
    }

    /************************************************************************
     * Purpose:         Parcelable Friend Interaction
     * Precondition:    .
     * Postcondition:   Go to Friend Details page
     ************************************************************************/
    @Override
    public void onFriendSelected(int position)
    {
        Intent intent = new Intent(ChatFriendPage.this, ChatPage.class);
        intent.putExtra(getString(R.string.intent_friend), this.FriendAdapter.getItem(position));
        startActivity(intent);
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