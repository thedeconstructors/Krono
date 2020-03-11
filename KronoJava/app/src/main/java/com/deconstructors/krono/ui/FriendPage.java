package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.FriendRVAdapter;
import com.deconstructors.krono.module.User;
import com.deconstructors.krono.utility.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendPage extends AppCompatActivity
        implements FriendRVAdapter.FriendRVClickListener,
                   View.OnClickListener
{
    // Error Log
    private static final String TAG = "FriendPage";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView FriendRecyclerView;
    private FloatingActionButton FAB;

    // Database
    private FirebaseFirestore FirestoreDB;
    private List<User> FriendList;
    private FriendRVAdapter FriendRVAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_friend);

        this.setContents();
        this.getFriends();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.friend_toolbar);
        this.Toolbar.setTitle(getString(R.string.menu_friends));
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Recycler View
        this.FriendList = new ArrayList<>();
        this.FriendRVAdapter = new FriendRVAdapter(FriendList, this);
        this.FriendRecyclerView = findViewById(R.id.friend_recyclerview);
        this.FriendRecyclerView.setHasFixedSize(true);
        this.FriendRecyclerView.setAdapter(this.FriendRVAdapter);
        this.FriendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Others Widgets
        this.FAB = findViewById(R.id.friend_fab);
        this.FAB.setOnClickListener(this);

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void getFriends()
    {
        FirestoreDB.collection(getString(R.string.collection_users))
                   .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                   .collection(getString(R.string.collection_friends))
                   .addSnapshotListener(new EventListener<QuerySnapshot>()
                   {
                       @Override
                       public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                           @Nullable FirebaseFirestoreException e)
                       {
                           if (e != null)
                           {
                               Log.d(TAG,"getFriends: onEvent Listen failed.", e);
                               return;
                           }

                           for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                           {
                               if (doc.getType() == DocumentChange.Type.ADDED)
                               {
                                   User user = doc.getDocument().toObject(User.class);
                                   FriendList.add(user);
                               }
                               else if (doc.getType() == DocumentChange.Type.MODIFIED)
                               {
                                   User user = doc.getDocument().toObject(User.class);
                                   FriendList.remove(Helper.getFriend(FriendList, user.getEmail()));
                                   FriendList.add(user);
                               }
                               else if (doc.getType() == DocumentChange.Type.REMOVED)
                               {
                                   User user = doc.getDocument().toObject(User.class);
                                   FriendList.remove(Helper.getFriend(FriendList, user.getEmail()));
                               }
                           }

                           Log.d(TAG, "getFriends: number of friends: " + FriendList.size());
                           FriendRVAdapter.notifyDataSetChanged();
                       }
                   });
    }


    @Override
    public void onFriendSelected(int position)
    {
        Intent intent = new Intent(FriendPage.this, FriendDetailPage.class);
        intent.putExtra(getString(R.string.intent_friend), this.FriendList.get(position));
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

    /************************************************************************
     * Purpose:         Toolbar Menu Inflater
     * Precondition:    .
     * Postcondition:   Activates the toolbar menu by inflating it
     *                  See more from res/menu/activity_boolbar_menu
     *                  and layout/menu0_toolbar
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend, menu);

        return true;
    }
}
