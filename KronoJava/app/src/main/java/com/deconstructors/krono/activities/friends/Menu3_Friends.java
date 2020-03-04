package com.deconstructors.krono.activities.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.FriendsListAdapter;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu3_Friends
        extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener
{
    //constants
    final String FRIENDID_EXTRA = "FRIEND_ID";
    final String DEBUG_TAG = "FRIENDPAGE";

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
        setContentView(R.layout.ui_friend);
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
        getFriends();
    }

    /*******************************************
     * BUTTON HANDLERS
     */

    public void OnAddFriendClick(View view)
    {
        //get email user entered
        final String friendEmail = _emailField.getText().toString().trim();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //check if email wasn't empty
        if (friendEmail.compareTo("") != 0)
        {
            //find the user with that email and save their id
            Task<QuerySnapshot> getFriendId =
                db.collection("users")
                    .whereEqualTo("loginEmail", friendEmail)
                    .get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Krono_Friends","FAILED: " + e.getMessage());
                        }
                    });

            //get friend document from db
            Task<DocumentSnapshot> getFriendDocument =
                Tasks.whenAllSuccess(getFriendId)
                    .continueWithTask(new Continuation<List<Object>, Task<DocumentSnapshot>>() {
                        @Override
                        public Task<DocumentSnapshot> then(@NonNull Task<List<Object>> task) throws Exception
                        {
                            String friendId = "";
                            if (!task.getResult().isEmpty())
                            {
                                friendId = ((QuerySnapshot)task.getResult().get(0)).getDocuments().get(0).getId();
                            }

                            return db.collection("users").document(friendId).get();
                        }
                    });

            //get my document from db
            Task<DocumentSnapshot> getMyDocument =
                    db.collection("users")
                    .document(SessionData.GetInstance().GetUserID())
                    .get();

            //create list of tasks
            List<Task<DocumentSnapshot>> getDocs = new ArrayList<>();
            getDocs.add(getFriendDocument);
            getDocs.add(getMyDocument);

            //once docs are retrieved,
            //copy document to user's friends
            Tasks.whenAllSuccess(getDocs)
                    .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Object>> task) {
                            if (task.isSuccessful())
                            {
                                //get friend document snapshot from task result
                                DocumentSnapshot friendDoc =
                                        (DocumentSnapshot) (task.getResult().get(0));

                                //get my doc
                                DocumentSnapshot myDoc =
                                        (DocumentSnapshot) (task.getResult().get(1));

                                //create new friend's document
                                HashMap<String,Object> friendInfo = new HashMap<>();
                                friendInfo.put("userid",friendDoc.getId());
                                friendInfo.put("firstname",friendDoc.get("firstname"));
                                friendInfo.put("lastname",friendDoc.get("lastname"));

                                //create my document for friend
                                HashMap<String,Object> myInfo = new HashMap<>();
                                myInfo.put("userid",myDoc.getId());
                                myInfo.put("firstname",myDoc.get("firstname"));
                                myInfo.put("lastname",myDoc.get("lastname"));

                                //add document to user's friend collection
                                Task<DocumentReference> addedFriendDoc =
                                    db.collection("users")
                                            .document(SessionData.GetInstance().GetUserID())
                                            .collection("friends")
                                            .add(friendInfo);

                                //add my document to friend's friend collection
                                Task<DocumentReference> addedMyDoc =
                                        db.collection("users")
                                        .document(friendDoc.getId())
                                        .collection("friends")
                                        .add(myInfo);

                                //make list of tasks
                                List<Task<DocumentReference>> addTaskList = new ArrayList<>();
                                addTaskList.add(addedFriendDoc);
                                addTaskList.add(addedMyDoc);

                                Tasks.whenAllSuccess(addTaskList)
                                        .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                                            @Override
                                            public void onComplete(@NonNull Task<List<Object>> task) {
                                                if (task.isSuccessful())
                                                {
                                                    //tell user it was a success
                                                    //refresh list
                                                    NotifyMessage("Successfully added friend!");
                                                    _SwipeRefreshLayout.setRefreshing(true);
                                                    onRefresh();
                                                }
                                                else
                                                {
                                                    NotifyMessage("Unable to add friend");
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                NotifyMessage("Failed to retrieve friend's info");
                            }
                        }
                    });
        }
        else
        {
            NotifyMessage("Please enter an email");
        }

    }

    public void ViewProfileOnClick(View view)
    {

        int selectedIndex = _adapter.GetSelectedIndex();

        if (selectedIndex == -1)
        {
            NotifyMessage("No friend is selected");
        }
        else
        {
            String friendId = _friendsList.get(selectedIndex).GetFriend().GetID();

            Intent intent = new Intent(Menu3_Friends.this, Menu3_Friends_ViewFriend.class);

            intent.putExtra(FRIENDID_EXTRA, friendId);

            startActivity(intent);
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

    public void RemoveFriendOnClick_NEWDB(View view)
    {
        int selectedIndex = _adapter.GetSelectedIndex();

        if (selectedIndex == -1)
        {
            NotifyMessage("No friend is selected");
        }
        else
        {
            final String friendId = _friendsList.get(selectedIndex).GetFriend().GetID();

            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            Task<QuerySnapshot> getMyDoc =
                db.collection("users")
                    .document(friendId)
                    .collection("friends")
                    .whereEqualTo("userid", SessionData.GetInstance().GetUserID())
                    .get();

            Task<QuerySnapshot> getFriendDoc =
                db.collection("users")
                    .document(SessionData.GetInstance().GetUserID())
                    .collection("friends")
                    .whereEqualTo("userid",friendId)
                    .get();

            List<Task<QuerySnapshot>> queryList = new ArrayList<>();
            queryList.add(getMyDoc);
            queryList.add(getFriendDoc);

            Tasks.whenAllSuccess(queryList)
                    .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Object>> task) {
                            if (task.isSuccessful())
                            {
                                QuerySnapshot friendQuery = (QuerySnapshot)task.getResult().get(0);
                                DocumentSnapshot friendDoc = friendQuery.getDocuments().get(0);
                                QuerySnapshot myQuery = (QuerySnapshot)task.getResult().get(1);
                                DocumentSnapshot myDoc = myQuery.getDocuments().get(0);

                                List<Task<Void>> deleteTasks = new ArrayList<>();

                                deleteTasks.add(
                                    db.collection("users")
                                        .document(SessionData.GetInstance().GetUserID())
                                        .collection("friends")
                                        .document(myDoc.getId())
                                        .delete()
                                );

                                deleteTasks.add(
                                    db.collection("users")
                                        .document(friendId)
                                        .collection("friends")
                                        .document(friendDoc.getId())
                                        .delete()
                                );

                                Tasks.whenAllSuccess(deleteTasks)
                                        .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                                            @Override
                                            public void onComplete(@NonNull Task<List<Object>> task) {
                                                if (task.isSuccessful())
                                                {
                                                    NotifyMessage("Successfully removed friend!");
                                                    _SwipeRefreshLayout.setRefreshing(true);
                                                    onRefresh();
                                                }
                                                else
                                                {
                                                    NotifyMessage("Failed to remove friend");
                                                }
                                            }
                                        });
                            }
                        }
                    });
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

        //task to get entire collection of user friends
        Task<QuerySnapshot> getUserFriends =
            db.collection("users")
                .document(SessionData.GetInstance().GetUserID())
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot friend : task.getResult())
                            {
                                Friend newfriend = new Friend(
                                        friend.get("firstname").toString(),
                                        friend.get("lastname").toString(),
                                        friend.get("userid").toString()
                                );

                                FriendHolder holder = new FriendHolder(newfriend);

                                _friendsList.add(holder);
                            }
                            NotifyMessage("Retrieved all friends");
                            _adapter.notifyDataSetChanged();
                        }
                        else
                        {
                            NotifyMessage("Unable to retrieve friends");
                        }
                        _SwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }
}
