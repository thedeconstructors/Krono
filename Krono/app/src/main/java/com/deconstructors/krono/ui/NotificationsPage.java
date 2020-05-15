package com.deconstructors.krono.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.NotificationAdapter;
import com.deconstructors.krono.module.Friend;
import com.deconstructors.krono.module.Notification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsPage extends AppCompatActivity
{
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

    //Data Members
    Context context;
    ArrayList<String> FriendRequestIds;
    ArrayList<String> FriendRequestNames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notif_main);
        this.DBInstance = FirebaseFirestore.getInstance();
        this.AuthInstance = FirebaseAuth.getInstance();
        this.RecyclerView = findViewById(R.id.notif_recyclerview);
        context = this;

        FriendRequestIds = new ArrayList<>();
        FriendRequestNames = new ArrayList<>();

        setToolbar();
        SearchFriendRequestIds();
        ShowData(FriendRequestIds);
        SearchFriendRequestNames();
        ShowData(FriendRequestNames);

        Adapter = new NotificationAdapter(FriendRequestNames, FRIEND_REQUEST);
        RecyclerView.setAdapter(Adapter);
        RecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.notif_toolbar);
        this.Toolbar.setTitle("Notifications");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void SearchFriendRequestIds()
    {
        this.DBInstance
                .collection("users")
                .whereEqualTo("uid", this.AuthInstance.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess (QuerySnapshot queryDocumentSnapshots)
            {
                if (!queryDocumentSnapshots.isEmpty())
                {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                    {
                        Map<Object, Boolean> friendMap = (Map<Object, Boolean>)doc.get("friends");

                        for (Map.Entry<Object, Boolean> entry : friendMap.entrySet())
                        {
                            Object key = entry.getKey();
                            Boolean value = entry.getValue();

                            if (value == true)
                            {
                                FriendRequestIds.add(key.toString());
                            }
                        }
                    }
                }
            }
        });
    }

    private void SearchFriendRequestNames()
    {
        for (int i = 0; i < FriendRequestIds.size(); i++)
        {
            this.DBInstance
                    .collection("users")
                    .whereEqualTo("uid", FriendRequestIds.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    FriendRequestNames.add((String)doc.get("displayName"));
                                }
                            }
                        }
                    });
        }
    }

    private void ShowData(ArrayList<String> list)
    {
        System.out.println("Entering ShowData");
        System.out.println(list.size());

        for (int i = 0; i < list.size(); i++)
        {
            System.out.println(list.get(i));
        }

    }
}
