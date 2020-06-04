package com.deconstructors.krono.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.FriendAdapter;
import com.deconstructors.krono.adapter.NotificationAdapter;
import com.deconstructors.krono.module.Notification;
import com.deconstructors.krono.module.User;
import com.deconstructors.krono.utility.Helper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsPage extends AppCompatActivity implements NotificationAdapter.NotificationClickListener
{
    //Notification Types (can add more later)
    private final int FRIEND_REQUEST = 0;

    // Error Log
    private static final String TAG = "NotificationsPage";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView RecyclerView;
    private LinearLayoutManager Manager;
    private NotificationAdapter Adapter;
    private CoordinatorLayout Background;
    private ProgressBar ProgressBar;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private NotificationAdapter NotifAdapter;
    private FirebaseFunctions DBFunctions;
    private Query FriendQuery;
    private FirestoreRecyclerOptions<User> FriendOptions;

    //Data Members
    ArrayList<String> FriendRequestIds;
    ArrayList<String> FriendRequestNames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_main);

        setToolbar();
        setDatabase();
        setContents();
    }

    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.notif_toolbar);
        this.Toolbar.setTitle(getString(R.string.notif_title));
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setDatabase()
    {
        // DB
        this.DBInstance = FirebaseFirestore.getInstance();
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBFunctions = FirebaseFunctions.getInstance();

        // Friend Request Info
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBInstance = FirebaseFirestore.getInstance();
        this.FriendQuery = this.DBInstance
                .collection(getString(R.string.collection_users))
                .whereEqualTo(getString(R.string.collection_friends) + "." + this.AuthInstance.getCurrentUser().getUid(),
                              0);
        // -1 = request received
        // 0 = request sent
        // 1 = request accepted
        this.FriendOptions = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(this.FriendQuery, User.class)
                .build();
        this.NotifAdapter = new NotificationAdapter(this.FriendOptions, this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.NotifAdapter != null) { this.NotifAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.NotifAdapter != null) { this.NotifAdapter.stopListening(); }
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Recycler View
        this.RecyclerView = findViewById(R.id.notif_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(this.NotifAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // xml
        this.Background = findViewById(R.id.notif_background);
        this.ProgressBar = findViewById(R.id.notif_progressBar);
    }

    @Override
    public void onRequestAccepted(final int position)
    {
        this.getacceptFriendRequest(NotifAdapter.getItem(position).getUid())
            .addOnCompleteListener(new OnCompleteListener<String>()
            {
                @Override
                public void onComplete(@NonNull Task<String> task)
                {
                    Log.d(TAG, "getacceptFriendRequest: success");
                    getAcceptFunctions(position);
                }
            });
    }

    private void getAcceptFunctions(int position)
    {
        Map<String, Object> user = new HashMap<>();
        user.put(getString(R.string.collection_friends) + "." + NotifAdapter.getItem(position).getUid(), 1);
        Helper.showProgressBar(this, ProgressBar);

        this.DBInstance
                .collection(getString(R.string.collection_users))
                .document(AuthInstance.getCurrentUser().getUid())
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Helper.hideProgressBar(NotificationsPage.this, NotificationsPage.this.ProgressBar);
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Helper.makeSnackbarMessage(NotificationsPage.this.Background,
                                                   "Unable to accept a friend request: " + e.getMessage());
                        Helper.hideProgressBar(NotificationsPage.this, NotificationsPage.this.ProgressBar);
                    }
                });
    }

    private Task<String> getacceptFriendRequest(String friendID)
    {
        // Create the arguments to the callable function.
        Map<String, Object> snap = new HashMap<>();
        snap.put(getString(R.string.friend_friendID), friendID);
        snap.put(getString(R.string.functions_push), true);

        return this.DBFunctions
                .getHttpsCallable(getString(R.string.functions_acceptfriend))
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

    ///////////////

    @Override
    public void onRequestRefused(final int position)
    {
        Map<String, Object> users = new HashMap<>();
        users.put(getString(R.string.collection_friends) + "." + this.NotifAdapter.getItem(position).getUid(), FieldValue.delete());
        Helper.showProgressBar(this, ProgressBar);

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
                        onRejectFunctions(NotifAdapter.getItem(position).getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Helper.makeSnackbarMessage(NotificationsPage.this.Background,
                                                   "Unable to refuse a friend request: " + e.getMessage());
                        Helper.hideProgressBar(NotificationsPage.this, NotificationsPage.this.ProgressBar);
                    }
                });
    }

    private void onRejectFunctions(String friendID)
    {
        this.getDeleteFriendFunctions(friendID)
            .addOnCompleteListener(new OnCompleteListener<String>()
            {
                @Override
                public void onComplete(@NonNull Task<String> task)
                {
                    Log.d(TAG, "getDeleteFriendFunctions: success");
                    Helper.hideProgressBar(NotificationsPage.this, NotificationsPage.this.ProgressBar);
                }
            });
    }

    private Task<String> getDeleteFriendFunctions(String friendID)
    {
        // Create the arguments to the callable function.
        Map<String, Object> snap = new HashMap<>();
        snap.put(getString(R.string.friend_friendID), friendID);
        snap.put(getString(R.string.functions_push), true);

        return this.DBFunctions
                .getHttpsCallable(getString(R.string.functions_deletefriend))
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
        }
        return true;
    }

    @Override
    public void onFriendSelected(int position)
    {
        Intent intent = new Intent(NotificationsPage.this, FriendPage_Detail.class);
        intent.putExtra(getString(R.string.intent_friend), this.NotifAdapter.getItem(position));
        startActivity(intent);
    }
}
