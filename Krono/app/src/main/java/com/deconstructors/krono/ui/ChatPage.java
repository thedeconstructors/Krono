package com.deconstructors.krono.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.deconstructors.krono.R;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.module.Message;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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

    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        message = findViewById(R.id.ChatPage_Message);

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
                holder.displayName.setText(model.GetRecipient());
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

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.adapter != null) { this.adapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.adapter != null) { this.adapter.stopListening(); }
    }

    public void SendMessageClick(View view) {

    }

    public void CreateMessage()
    {

    }
}
