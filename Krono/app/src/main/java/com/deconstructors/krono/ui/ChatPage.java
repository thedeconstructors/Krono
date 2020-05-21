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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Context;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        //if (this.AuthInstance.getUid().contains(this.AuthInstance.getUid()))
        this.Toolbar.setTitle("Me");
        //else
        //this.Toolbar.setTitle(this.AuthInstance.getCurrentUser().getDisplayName());
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
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBInstance = FirebaseFirestore.getInstance();
        this.ChatQuery = this.DBInstance
                .collection("chats")
                .whereEqualTo("sender", this.AuthInstance.getUid())
                .whereEqualTo("recipient", this.AuthInstance.getUid())
                .orderBy("time");
        //this.ChatQuery = this.DBInstance
        //        .collection("chats")
        //        .whereEqualTo("recipient", "Me")
        //        .orderBy("time");
        this.ChatOptions = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(ChatQuery, Message.class)
                .build();
        this.messageAdapter = new MessageAdapter(this.ChatOptions);
        //messageAdapter.updateOptions(ChatOptions);
        this.messageAdapter.notifyDataSetChanged();
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.removeAllViews();
        layoutManager.setStackFromEnd(true);
        //layoutManager.setReverseLayout(true);
        //this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //this.RecyclerView.setLayoutManager(null);
        //this.RecyclerView.setAdapter(null);
        this.RecyclerView.setLayoutManager(layoutManager);
        this.RecyclerView.setAdapter(this.messageAdapter);
        //this.RecyclerView.scrollToPosition(this.RecyclerView.getChildCount() - 1);

        this.messageText = findViewById(R.id.ChatPage_Message);

    }

    public void SendMessageClick(View view) {
        //ResetAdapter();
        //messageAdapter.getCount();
        if (!Helper.isEmpty(this.messageText))
        {
            Map<String, Object> message = new HashMap<>();

            Message newMessage = new Message();
            message.put("sender", this.AuthInstance.getUid());
            message.put("recipient", this.AuthInstance.getUid());
            message.put("text", this.messageText.getText().toString());
            message.put("time", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()));
            DBInstance.collection("chats")
                      .add(message)
                      .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentReference.update("messageID", documentReference.getId());
                }
            });


            this.messageText.setText("");
            CloseKeyboard();
            //this.messageAdapter.notifyItemChanged(messageAdapter.getCount() - 1);
            //this.messageAdapter.getItem(0);
            //messageAdapter.notifyDataSetChanged();
            this.messageAdapter.updateOptions(ChatOptions);
            //DeleteMessage();
            //ResetAdapter();
            //messageAdapter.notifyDataSetChanged();
            messageAdapter.updateOptions(ChatOptions);

        }
        else
        {
        }
    }

    private void CloseKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*private void DeleteMessage()
    {
        if (this.messageAdapter.getCount() > 5)
        {
            while (this.messageAdapter.getCount() > 5)
            {
                DocumentReference doc = DBInstance.collection("chats")
                        .document(this.messageAdapter.getItem(0).getMessageID());
                doc.delete();
                messageAdapter.notifyItemRemoved(0);
            }
            messageAdapter.updateOptions(ChatOptions);
        }
    }
    private void ResetAdapter()
    {
        while (messageAdapter.getItem(0).getMessageID() == null)
        {
            messageAdapter.notifyItemRemoved(0);
            this.RecyclerView.removeViewAt(0);
        }
    }*/

    /************************************************************************
     * Purpose:         Toolbar Back Button Animation Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            // Back Button Animation Override
            case android.R.id.home:
            {
                finish();
                break;
            }
        }
        return true;
    }
}