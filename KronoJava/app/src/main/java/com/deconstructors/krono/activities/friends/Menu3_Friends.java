package com.deconstructors.krono.activities.friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.deconstructors.firestoreinteract.IntegerCounter;
import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.FriendsListAdapter;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.local.QueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class Menu3_Friends
        extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener
{
    //private WebView m_webView;
    private TextView _emailField;
    private SwipeRefreshLayout _SwipeRefreshLayout;
    private Button _removeFriendButton;

    //friends list management
    private RecyclerView _friendsRecycler;
    private List<FriendHolder> _friendsList;
    private FriendsListAdapter _adapter;
    private List<String> _tempIDList;
    private DocumentSnapshot _LastQueriedList;

    // DB Paths
    private static final String _userfriendsPath = "userfriends";
    private static final String _userPath = "users";

    /******************************************
     * OVERRIDE FUNCTIONS
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu3__friends);
        //get all views
        _friendsRecycler = (RecyclerView) findViewById(R.id.MainMenu_Friends_RecyclerView);
        _emailField = (TextView) findViewById(R.id.MainMenu_Friends_Email);
        _SwipeRefreshLayout = findViewById(R.id.MainMenu_Friends_RefreshLayout);
        _SwipeRefreshLayout.setOnRefreshListener(this);
        _removeFriendButton = (Button) findViewById(R.id.MainMenu_Friends_RemoveFriend);

        setupRecycleView();
        _SwipeRefreshLayout.setRefreshing(true);
        getFriends();
    }

    @Override
    public void onRefresh()
    {
        _friendsList.clear();
        this.getFriends();
    }

    /*******************************************
     * BUTTON HANDLERS
     */

    public void OnAddFriendClick(View view)
    {
        final String friendEmail = _emailField.getText().toString().trim();

        if (friendEmail.compareTo("") != 0) {
            //find the user with that email and save their id
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("loginEmail", friendEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                String friendId = queryDocumentSnapshots.getDocuments().get(0).getId();

                                //create couple as (myId,friendId)
                                Map<String, String> couple1 = new HashMap<String, String>();
                                couple1.put("user1", SessionData.GetInstance().GetUserID());
                                couple1.put("user2", friendId);

                                //create couple as (friendId,myId)
                                Map<String, String> couple2 = new HashMap<String, String>();
                                couple2.put("user1", friendId);
                                couple2.put("user2", SessionData.GetInstance().GetUserID());

                                //add couples to userfriends
                                FirebaseFirestore.getInstance()
                                        .collection("userfriends")
                                        .add(couple1)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Menu3_Friends.this,
                                                        "Couldn't add friend", Toast.LENGTH_SHORT);
                                            }
                                        });
                                FirebaseFirestore.getInstance()
                                        .collection("userfriends")
                                        .add(couple2)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Menu3_Friends.this,
                                                        "Couldn't add friend", Toast.LENGTH_SHORT);
                                            }
                                        });
                            }
                            else
                            {
                                Toast.makeText(Menu3_Friends.this,
                                        "No user with that email", Toast.LENGTH_SHORT);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Menu3_Friends.this,
                                    "No user with that email", Toast.LENGTH_SHORT);
                        }
                    });
        }
        else
        {
            Toast.makeText(Menu3_Friends.this,
                    "Please enter an email", Toast.LENGTH_SHORT);
        }
    }

    public void RemoveFriendOnClick(View view)
    {
        int selectedIndex = _adapter.GetSelectedIndex();

        if (selectedIndex == -1)
        {
            NotifyMessage("No friend is selected");
        }
        else
        {
            String friendId = _friendsList.get(selectedIndex).GetFriend().GetID();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //query to retrieve doc (user,friend)
            final Query friendDocLocalQuery = db.collection("userfriends")
                    .whereEqualTo("user1",SessionData.GetInstance().GetUserID())
                    .whereEqualTo("user2",friendId);
            //query to retrieve doc (friend,user)
            Query friendDocRemoteQuery = db.collection("userfriends")
                    .whereEqualTo("user1", friendId)
                    .whereEqualTo("user2",SessionData.GetInstance().GetUserID());

            List<Task<QuerySnapshot>> removeFriends = new ArrayList<>();

            removeFriends.add(friendDocLocalQuery.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty())
                            {
                                Tasks.whenAllComplete(
                                    task.getResult()
                                            .getDocuments()
                                            .get(0)
                                            .getReference()
                                            .delete()
                                );
                            }
                        }
                    })
            );

            removeFriends.add(friendDocRemoteQuery.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty())
                            {
                                Tasks.whenAllComplete(
                                        task.getResult()
                                                .getDocuments()
                                                .get(0)
                                                .getReference()
                                                .delete()
                                );
                            }
                        }
                    })
            );

            Tasks.whenAllSuccess(removeFriends)
                .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        if (task.isSuccessful())
                        {
                            NotifyMessage("Successfully removed friend");
                            //Refresh page
                            _SwipeRefreshLayout.setRefreshing(true);
                            onRefresh();
                        }
                        else
                        {
                            NotifyMessage("Failed to remove friend");
                        }
                    }
                }
            );
        }
    }

    /*******************************************
     * HELPER FUNCTIONS
     */

    private void NotifyMessage(String message)
    {
        Toast.makeText(Menu3_Friends.this,
                message,
                Toast.LENGTH_SHORT)
            .show();
    }

    private void setupRecycleView()
    {
        //set up recycler behavior
        _tempIDList = new ArrayList<>();
        _friendsList = new ArrayList<>();
        _adapter = new FriendsListAdapter(_friendsList);

        _friendsRecycler.setHasFixedSize(true);
        _friendsRecycler.setLayoutManager(new LinearLayoutManager(this));
        _friendsRecycler.setAdapter(_adapter);
    }

    private void getFriends_HOTFIX()
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference friendsCollectionRef = db.collection("userfriends");

        Query friendsQuery = friendsCollectionRef
                .whereEqualTo("user1", SessionData.GetInstance().GetUserID());

        Task getID = friendsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document: task.getResult())
                    {
                        _tempIDList.add(document.getString("user2"));
                    }
                }
            }
        });

        Tasks.whenAllSuccess(getID).addOnCompleteListener(new OnCompleteListener<List<Object>>()
        {
            @Override
            public void onComplete(@NonNull Task<List<Object>> task)
            {
                Query userInfoQuery = null;

                for (String id : _tempIDList)
                {
                    if(_LastQueriedList != null)
                    {
                        userInfoQuery = db.collection("users")
                                .whereEqualTo(FieldPath.documentId(), id)
                                .startAfter(_LastQueriedList);
                    }
                    else
                    {
                         userInfoQuery = db.collection("users")
                                .whereEqualTo(FieldPath.documentId(), id);
                    }

                    userInfoQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                for(QueryDocumentSnapshot document : task.getResult())
                                {
                                    // Not sure why I can't convert this document
                                    // directly to a Friend Object
                                    // Change this code later.
                                    String fn = document.get("firstname").toString();
                                    String ln = document.get("lastname").toString();
                                    String id = document.getId();
                                    _friendsList.add(new FriendHolder(new Friend(fn, ln, id)));
                                }

                                // To not replicate items users already have
                                if(task.getResult().size() != 0)
                                {
                                    _LastQueriedList = task.getResult().getDocuments()
                                            .get(task.getResult().size() - 1);
                                }

                                _adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                _SwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getFriends()
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query getFriends = db.collection("userfriends")
                .whereEqualTo("user1",
                        SessionData.GetInstance().GetUserID());
        //get all friendIds
        final List<String> friendIds = new ArrayList<>();

        //define task to retrieve all friend ids
        Task<QuerySnapshot> gettingFriendIds = getFriends.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot doc : task.getResult().getDocuments())
                            {
                                friendIds.add(doc.get("user2").toString());
                            }
                        }
                        else
                        {
                            NotifyMessage("Unable to retrieve friends");
                        }
                    }
                });

        //when that is finished, define new tasks to get each friend
        Tasks.whenAllSuccess(gettingFriendIds)
                .continueWithTask(new Continuation<List<Object>, Task<List<DocumentSnapshot>>>() {
                    @Override
                    public Task<List<DocumentSnapshot>> then(@NonNull Task<List<Object>> task) throws Exception {
                        List<Task<DocumentSnapshot>> getEachFriend = new ArrayList<>();

                        for (String id : friendIds)
                        {
                            getEachFriend.add(db.collection("users")
                                    .document(id)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful())
                                            {
                                                String fname = task.getResult().get("firstname").toString();
                                                String lname = task.getResult().get("lastname").toString();
                                                String fid = task.getResult().getId();
                                                Friend newfriend = new Friend(fname,lname,fid);
                                                FriendHolder holder = new FriendHolder(newfriend);
                                                _friendsList.add(holder);
                                            }
                                        }
                                    }));
                        }

                        return Tasks.whenAllSuccess(getEachFriend);
                    }
                })
                //once all tasks are finished to retrieve friends, notify user
                .addOnCompleteListener(new OnCompleteListener<List<DocumentSnapshot>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<DocumentSnapshot>> task) {
                        if (task.isSuccessful())
                        {
                            NotifyMessage("Retrieved all friends successfully");
                        }
                        else
                        {
                            NotifyMessage("Unable to retrieve friends");
                        }
                        _adapter.notifyDataSetChanged();
                        _SwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }
}
