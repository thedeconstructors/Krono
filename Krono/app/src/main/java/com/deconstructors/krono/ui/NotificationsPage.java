package com.deconstructors.krono.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.NotificationAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsPage extends AppCompatActivity implements View.OnClickListener{
    //Notification Types (can add more later)
    private final int FRIEND_REQUEST = 0;

    // Error Log
    private static final String TAG = "NotificationsPage";

    // XML Widgets
    private androidx.appcompat.widget.Toolbar Toolbar;
    private androidx.recyclerview.widget.RecyclerView RecyclerView;
    private LinearLayoutManager Manager;
    private NotificationAdapter Adapter;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private NotificationAdapter NotifAdapter;
    private FirebaseFunctions DBFunctions;

    //Data Members
    Context context;
    ArrayList<String> FriendRequestIds;
    ArrayList<String> FriendRequestNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notif_main);
        this.DBInstance = FirebaseFirestore.getInstance();
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBFunctions = FirebaseFunctions.getInstance();
        this.RecyclerView = findViewById(R.id.notif_recyclerview);
        context = this;

        FriendRequestIds = new ArrayList<>();
        FriendRequestNames = new ArrayList<>();
        Map<String, String> FriendIdToNameMap = new HashMap<>();

        setToolbar();

        this.DBInstance
                .collection(getString(R.string.collection_users))
                .whereEqualTo(getString(R.string.notification_uid), this.AuthInstance.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Map<Object, Boolean> friendMap = (Map<Object, Boolean>) doc.get(getString(R.string.friends));

                                for (Map.Entry<Object, Boolean> entry : friendMap.entrySet()) {
                                    Object key = entry.getKey();
                                    Boolean value = entry.getValue();

                                    if (value == false) {
                                        FriendRequestIds.add(key.toString());
                                    }
                                }
                            }
                        }
                    }
                })
                .continueWithTask(new Continuation<QuerySnapshot, Task<QuerySnapshot>>() {
                    @Override
                    public Task<QuerySnapshot> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            return FirebaseFirestore.getInstance()
                                    .collection(getString(R.string.collection_users))
                                    .whereIn(getString(R.string.notification_uid), FriendRequestIds)
                                    .get();
                        }
                        return null;
                    }
                })
                .continueWithTask(new Continuation<QuerySnapshot, Task<List<DocumentSnapshot>>>() {
                    @Override
                    public Task<List<DocumentSnapshot>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            List<Task<DocumentSnapshot>> getNames = new ArrayList<>();

                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                getNames.add(FirebaseFirestore.getInstance()
                                        .collection(getString(R.string.collection_users))
                                        .document(doc.getId()).get());
                            }

                            return Tasks.whenAllSuccess(getNames);
                        }
                        return null;
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<List<DocumentSnapshot>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<DocumentSnapshot>> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> friendTasks = task.getResult();
                            for (DocumentSnapshot doc : friendTasks) {
                                FriendRequestNames.add((String) doc.get(getString(R.string.notification_displayname)));
                            }

                            RecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        }
                    }
                });

        this.NotifAdapter = new NotificationAdapter(FriendRequestNames);
        this.RecyclerView.setAdapter(this.NotifAdapter);
    }

    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.notif_toolbar);
        this.Toolbar.setTitle(getString(R.string.notif_title));
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.notif_listitem_accept:
            {
                System.out.println("in OnClick. Case: R.id.notif_listitem_accept");

                CharSequence charSeqDesc = ((TextView) findViewById(R.id.notif_Description)).getText();
                String desc = (String) charSeqDesc;

                for (int i = 0; i < FriendRequestNames.size(); i++)
                {
                    if (("You have a new friend request from " + FriendRequestNames.get(i)).equals(desc))
                    {
                        final String[] FriendId = new String[1];
                        DBInstance.collection(getString(R.string.collection_users))
                                .whereEqualTo(getString(R.string.notification_displayname), FriendRequestNames.get(i))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                                {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                    {
                                        if (!queryDocumentSnapshots.isEmpty())
                                        {
                                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                                            {
                                                FriendId[0] = doc.getId();
                                            }
                                            System.out.println(FriendId[0]);

                                            String updateString = getString(R.string.collection_friends) + "." + FriendId[0];

                                            DBInstance.collection(getString(R.string.collection_users))
                                                    .document(AuthInstance.getCurrentUser().getUid())
                                                    .update(updateString, true);

                                            onAccept(FriendId[0]);
                                        }
                                    }
                                });
                    }
                }
                break;
            }
            case R.id.notif_listitem_reject:
            {
                System.out.println("in OnClick. Case: R.id.notif_listitem_reject");

                CharSequence charSeqDesc = ((TextView) findViewById(R.id.notif_Description)).getText();
                String desc = (String) charSeqDesc;

                for (int i = 0; i < FriendRequestNames.size(); i++)
                {
                    if (("You have a new friend request from " + FriendRequestNames.get(i)).equals(desc))
                    {
                        final String[] FriendId = new String[1];
                        DBInstance.collection(getString(R.string.collection_users))
                                .whereEqualTo(getString(R.string.notification_displayname), FriendRequestNames.get(i))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                                {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                    {
                                        if (!queryDocumentSnapshots.isEmpty())
                                        {
                                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                                            {
                                                FriendId[0] = doc.getId();
                                            }

                                            System.out.println(FriendId[0]);

                                            String removeString = getString(R.string.collection_friends) + "." + FriendId[0];

                                            DocumentReference docRef = DBInstance.collection(getString(R.string.collection_users))
                                                                                 .document(AuthInstance.getCurrentUser().getUid());
                                            Map<String,Object> updates = new HashMap<>();
                                            updates.put(removeString, FieldValue.delete());

                                            docRef.update(updates);

                                            onReject(FriendId[0]);
                                        }
                                    }
                                });
                    }
                }
            }
        }
    }

    private void onAccept(String friendID)
    {
        this.getacceptFriendRequest(friendID)
            .addOnCompleteListener(new OnCompleteListener<String>()
            {
                @Override
                public void onComplete(@NonNull Task<String> task)
                {
                    Log.d(TAG, "getacceptFriendRequest: success");
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

    private void onReject(String friendID)
    {
        this.getDeleteFriendFunctions(friendID)
            .addOnCompleteListener(new OnCompleteListener<String>()
            {
                @Override
                public void onComplete(@NonNull Task<String> task)
                {
                    Log.d(TAG, "getDeleteFriendFunctions: success");
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
}
