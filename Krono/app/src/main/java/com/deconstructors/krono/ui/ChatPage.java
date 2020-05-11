package com.deconstructors.krono.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.deconstructors.krono.R;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.adapter.MessageAdapter;
import com.deconstructors.krono.module.Message;
import com.deconstructors.krono.module.User;
import com.deconstructors.krono.utility.Helper;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatPage extends AppCompatActivity
{
    // Error Log
    private static final String TAG = "ChatPage";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView RecyclerView;
    private FriendPage_New FriendPage_New;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private Query ChatQuery;
    private FirestoreRecyclerOptions<Message> ChatOptions;
    //private FriendAdapter FriendAdapter;
    private FirestoreRecyclerAdapter adapter;

    private MessageAdapter messageAdapter;

    private EditText messageText;
    private User user;

    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        this.setToolbar();
        this.setDatabase();
        this.setContents();
    }

    /************************************************************************
     * Purpose:         Set Toolbar & Inflate Toolbar Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.chat_toolbar);
        this.Toolbar.setTitle(getString(R.string.menu_chat));
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
        //this.AuthInstance = FirebaseAuth.getInstance();
        this.DBInstance = FirebaseFirestore.getInstance();
        this.ChatQuery = this.DBInstance
                .collection("chats")
                .whereEqualTo("recipient", "Me")
                .orderBy("time");
        this.ChatOptions = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(ChatQuery, Message.class)
                .build();
        this.messageAdapter = new MessageAdapter(this.ChatOptions);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.messageAdapter != null) { this.messageAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.messageAdapter != null) { this.messageAdapter.stopListening(); }
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Recycler View
        this.RecyclerView = findViewById(R.id.ChatPage_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.RecyclerView.setAdapter(this.messageAdapter);

        this.messageText = findViewById(R.id.ChatPage_Message);
    }

    public void SendMessageClick(View view) {
        if (!Helper.isEmpty(this.messageText))
        {
            Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
            Map<String, Object> message = new HashMap<>();

            Message newMessage = new Message();
            message.put("sender", "Me");
            message.put("recipient", "Me");
            message.put("message", this.messageText.getText().toString());
            message.put("time", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
            DBInstance.collection("chats")
                    .document(this.AuthInstance.getUid())
                    .set(message);
        }
        else
        {
            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        }
    }
}