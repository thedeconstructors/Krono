package com.deconstructors.krono.activities.friends;

import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.plans.Plans;
import com.deconstructors.krono.helpers.PlansListAdapter;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Menu3_Friends_ViewFriend extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    //DEBUG TAG
    final String DEBUG_TAG = "DEBUG_VIEWFRIEND";
    //extra constant
    final String FRIENDID_EXTRA = "FRIEND_ID";

    //friend id for database retrieval
    String friendId;

    //ui views
    TextView friend_name;
    TextView friend_email;
    RecyclerView friend_publicPlans;
    RecyclerView friend_sharedPlans;

    SwipeRefreshLayout _refreshLayout;

    //plan list members
    List<Plans> _friend_publicPlans;
    List<Plans> _friend_sharedPlans;
    PlansListAdapter _friend_publicPlansAdapter;
    PlansListAdapter _friend_sharedPlansAdapter;

    /**************************
     * OVERRIDES
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_viewfriend);

        friendId = getIntent().getExtras().getString(FRIENDID_EXTRA);

        friend_name = (TextView) findViewById(R.id.ViewFriend_FriendName);
        friend_email = (TextView) findViewById(R.id.ViewFriend_email);
        friend_publicPlans = (RecyclerView) findViewById(R.id.ViewFriend_PublicPlans);
        friend_sharedPlans = (RecyclerView) findViewById(R.id.ViewFriend_SharedPlans);

        friend_publicPlans.setLayoutManager(new LinearLayoutManager(this));
        friend_publicPlans.setHasFixedSize(true);
        friend_sharedPlans.setLayoutManager(new LinearLayoutManager(this));
        friend_sharedPlans.setHasFixedSize(true);

        _friend_publicPlans = new ArrayList<>();
        _friend_sharedPlans = new ArrayList<>();

        _friend_publicPlansAdapter = new PlansListAdapter(_friend_publicPlans);
        _friend_sharedPlansAdapter = new PlansListAdapter(_friend_sharedPlans);
        friend_publicPlans.setAdapter(_friend_publicPlansAdapter);
        friend_sharedPlans.setAdapter(_friend_sharedPlansAdapter);

        _refreshLayout = (SwipeRefreshLayout) findViewById(R.id.ViewFriend_RefreshLayout);
        _refreshLayout.setOnRefreshListener(this);

        PopulateFriendInfo();
    }

    @Override
    public void onRefresh()
    {
        PopulateFriendInfo();
    }

    /*********************************
     * HELPER METHODS
     */
    private void NotifyMessage(String message)
    {
        Toast.makeText(Menu3_Friends_ViewFriend.this,
                message,
                Toast.LENGTH_SHORT)
                .show();
    }

    void PopulateFriendInfo()
    {
        _refreshLayout.setRefreshing(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Task<DocumentSnapshot> getFriendInfo =
            db.collection("users")
                .document(friendId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot friendDoc = task.getResult();

                            //basic user info
                            friend_name.setText(friendDoc.get("firstname").toString() + " "
                                        + friendDoc.get("lastname").toString());
                            friend_email.setText(friendDoc.get("loginEmail").toString());

                            //populate public plans

                            //populate shared plans

                            _refreshLayout.setRefreshing(false);
                        }
                        else
                        {
                            NotifyMessage("Failed to retrieve user info");
                            finish();
                        }
                    }
                });

        _friend_publicPlans.clear();

        Task<QuerySnapshot> getPublicPlans =
            db.collection("users")
                .document(friendId)
                .collection("plans")
                .whereEqualTo("public",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<DocumentSnapshot> publicPlans = task.getResult().getDocuments();
                            for (DocumentSnapshot doc : publicPlans)
                            {
                                Plans plan = doc.toObject(Plans.class);
                                _friend_publicPlans.add(plan);
                            }
                            _friend_publicPlansAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            NotifyMessage("Unable to retrieve friend's public plans");
                        }
                    }
                });

        Task<QuerySnapshot> getSharedPlans =
            db.collection("users")
                .document(friendId)
                .collection("plans")
                .whereArrayContains("collaborators",
                        SessionData.GetInstance().GetUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<DocumentSnapshot> docList = task.getResult().getDocuments();

                            for (DocumentSnapshot doc : docList)
                            {
                                Plans plan = doc.toObject(Plans.class);
                                _friend_sharedPlans.add(plan);
                            }

                            _friend_sharedPlansAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            NotifyMessage("Unable to retrieve friend's shared plans");
                        }
                    }
                });

        List<Task<?>> taskList = new ArrayList<>();

        taskList.add(getFriendInfo);
        taskList.add(getPublicPlans);
        taskList.add(getSharedPlans);

        Tasks.whenAllSuccess(taskList)
                .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        if (task.isSuccessful())
                        {
                            NotifyMessage("Successfully retrieved friend info");
                            _refreshLayout.setRefreshing(false);
                        }
                    }
                });
    }
}
