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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        this.setToolbar();
        //this.setDatabase();
        //this.setContents();
        messageText = findViewById(R.id.ChatPage_Message);

        DBInstance = FirebaseFirestore.getInstance();
        RecyclerView = findViewById(R.id.ChatPage_recyclerview);

        ChatQuery = DBInstance.collection("chats");
        ChatOptions = new FirestoreRecyclerOptions.Builder<Message>().setQuery(ChatQuery, Message.class)
        .build();

        adapter = new FirestoreRecyclerAdapter<Message, MessageViewHolder>(ChatOptions) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_listitem, parent, false);
                return new MessageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
                holder.displayName.setText(model.GetSender());
                holder.message.setText(model.GetText());
                holder.time.setText(Long.toString(model.GetTime()));
            }
        };

        RecyclerView.setHasFixedSize(true);
        RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.setAdapter(adapter);
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder
    {
        private TextView displayName;
        private TextView message;
        private TextView time;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.chatlist_nameText);
            message = itemView.findViewById(R.id.chatlist_messageText);
            time = itemView.findViewById(R.id.chatlist_time);
        }
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
        this.DBInstance = FirebaseFirestore.getInstance();
        this.ChatQuery = this.DBInstance
                .collection(getString(R.string.collection_plans))
                .document(this.user.getDisplayName())
                .collection(getString(R.string.collection_activities));
        this.ChatOptions = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(this.ChatQuery, Message.class)
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
        //this.RecyclerView = findViewById(R.id.FriendPage_recyclerview);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setAdapter(this.messageAdapter);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Bottom Sheet
        //this.FriendPage_New = new FriendPage_New(this);
    }

    public void SendMessageClick(View view) {
        if (!Helper.isEmpty(this.messageText))
        {
            Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
            Map<String, Object> message = new HashMap<>();

            Message newMessage = new Message();
            message.put("sender", "Me");
            message.put("message", this.messageText.toString());
            message.put("time", Long.toString(newMessage.GetTime()));
            DBInstance.collection("chats").document().update(message);
        }
        else
        {
            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        }
    }
}