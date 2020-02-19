package com.deconstructors.krono.activities.friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.deconstructors.firestoreinteract.IntegerCounter;
import com.deconstructors.krono.R;
import com.deconstructors.krono.helpers.FriendsListAdapter;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class Menu3_Friends extends AppCompatActivity
{
    //private WebView m_webView;


    private TextView _emailField;

    //friends list management
    private RecyclerView _friendsRecycler;
    private List<Friend> _friendsList;
    private FriendsListAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu3__friends);

        //get all views
        _friendsRecycler = (RecyclerView) findViewById(R.id.MainMenu_Friends_RecyclerView);
        _emailField = (TextView) findViewById(R.id.MainMenu_Friends_Email);

        //set up recycler behavior
        _friendsList = new ArrayList<>();
        _adapter = new FriendsListAdapter(_friendsList);

        _friendsRecycler.setHasFixedSize(true);
        _friendsRecycler.setLayoutManager(new LinearLayoutManager(this));
        _friendsRecycler.setAdapter(_adapter);

        GetFriends();
    }

    private void GetFriends()
    {
        //get all ids of friends
        Query friendCouples = FirebaseFirestore.getInstance().collection("userfriends")
                .whereEqualTo("user1",
                        SessionData.GetInstance().GetUserID());

        final List<String> friendIds = new ArrayList<>();

        friendCouples.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                {
                    friendIds.add(doc.get("user2").toString());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty("Something Went Wrong");
                    }
                });

        //get all friends
        final IntegerCounter friendCounter = new IntegerCounter(friendIds.size());

        CollectionReference users = FirebaseFirestore.getInstance().collection("users");

        for (String id : friendIds)
        {
            users.document(id).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            _friendsList.add(new Friend(documentSnapshot.get("firstname").toString(),
                                    documentSnapshot.get("lastname").toString()));
                            friendCounter.Decrement();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty("Something Went Wrong");
                            friendCounter.SetError(true);
                        }
                    });
        }

        //wait for friends to be found
        while (!friendCounter.HasError() && !friendCounter.Done()) {}

        //notify list adapter of change
        _adapter.notifyDataSetChanged();
    }

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
                                                Toasty("Couldn't add friend");
                                            }
                                        });
                                FirebaseFirestore.getInstance()
                                        .collection("userfriends")
                                        .add(couple2)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toasty("Couldn't add friend");
                                            }
                                        });
                            }
                            else
                            {
                                Toasty("No user with that email");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty("No user with that email");
                        }
                    });
        }
        else
        {
            Toasty("Please enter an email");
        }
    }

    /*****************************************
     * Helper Functions
     */

    //Spit out a message
    void Toasty(String message)
    {
        Toast.makeText(Menu3_Friends.this, message, Toast.LENGTH_SHORT);
    }
}
