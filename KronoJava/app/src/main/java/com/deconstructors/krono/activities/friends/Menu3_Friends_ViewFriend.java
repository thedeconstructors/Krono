package com.deconstructors.krono.activities.friends;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Menu3_Friends_ViewFriend extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
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

        _refreshLayout = (SwipeRefreshLayout) findViewById(R.id.ViewFriend_RefreshLayout);
        _refreshLayout.setOnRefreshListener(this);

        PopulateUserInfo();
    }

    @Override
    public void onRefresh()
    {
        NotifyMessage("That's Refreshing :D");
        _refreshLayout.setRefreshing(false);
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

    void PopulateUserInfo()
    {
        _refreshLayout.setRefreshing(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
    }
}
