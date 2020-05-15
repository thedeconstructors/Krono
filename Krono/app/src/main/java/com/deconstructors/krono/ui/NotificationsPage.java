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

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notif_main);
        this.DBInstance = FirebaseFirestore.getInstance();
        this.AuthInstance = FirebaseAuth.getInstance();
        this.RecyclerView = findViewById(R.id.notif_recyclerview);
        context = this;

        setToolbar();
        SearchFriendRequestIds();
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
        final ArrayList<String> friendRequestIds = new ArrayList<>();

        this.DBInstance
                .collection("users")
                .whereEqualTo("uid", this.AuthInstance.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess (QuerySnapshot queryDocumentSnapshots)
            {
                if (queryDocumentSnapshots.isEmpty())
                {
                    Context context = getApplicationContext();
                    String text = "queryDocumentSnapshots.isEmpty() returned true";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else
                {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                    {
                        Map<Object, Boolean> friendMap = new HashMap<>();
                        friendMap = (Map<Object, Boolean>)doc.get("friends");

                        for (Map.Entry<Object, Boolean> entry : friendMap.entrySet())
                        {
                            Object key = entry.getKey();
                            Boolean value = entry.getValue();

                            if (value == true)
                            {
                                friendRequestIds.add(key.toString());
                            }
                        }
                    }

                    //ShowData(friendRequestIds);
                    SearchFriendRequestNames(friendRequestIds);
                    /*Adapter = new NotificationAdapter(friendRequestIds, FRIEND_REQUEST);
                    RecyclerView.setAdapter(Adapter);
                    RecyclerView.setLayoutManager(new LinearLayoutManager(context));*/
                }
            }
        });
    }

    private void SearchFriendRequestNames(ArrayList<String> Ids)
    {
        final ArrayList<String> friendRequestNames = new ArrayList<>();

        for (int i = 0; i < Ids.size(); i++)
        {
            this.DBInstance
                    .collection("users")
                    .whereEqualTo("uid", Ids.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    friendRequestNames.add((String)doc.get("displayName"));
                                }

                                ShowData(friendRequestNames);
                                Adapter = new NotificationAdapter(friendRequestNames, FRIEND_REQUEST);
                                RecyclerView.setAdapter(Adapter);
                                RecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            }
                        }
                    });
        }
    }

    private void ShowData(ArrayList<String> Ids)
    {
        System.out.println("Entering ShowData");
        /*Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        int size = Ids.size();
        String text = Integer.toString(size);
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();*/

        System.out.println(Ids.size());
        for (int i = 0; i < Ids.size(); i++)
        {
            /*text = Ids.get(i);
            toast = Toast.makeText(context, text, duration);
            toast.show();*/
            String text = Ids.get(i);
            System.out.println(text);
        }

    }
}
