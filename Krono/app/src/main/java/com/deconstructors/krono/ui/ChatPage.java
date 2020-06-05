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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class ChatPage extends AppCompatActivity
{
    // Error Log
    private static final String TAG = "ChatPage";

    // XML Widgets
    private Toolbar Toolbar;
    private RecyclerView RecyclerView;
    LinearLayoutManager layoutManager;

    // Database
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;
    private Query ChatQuery;
    private FirestoreRecyclerOptions<Message> ChatOptions;
    private MessageAdapter MessageAdapter;

    private EditText messageText;
    private User friend;
    private String people;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        this.getFriendIntent();
        this.CreatePeople();
        this.setToolbar();
        this.setDatabase();
        this.setContents();
    }

    /************************************************************************
     * Purpose:         Parcelable Friend Interaction
     * Precondition:    .
     * Postcondition:   Get Plan Intent from MainActivity
     ************************************************************************/
    private void getFriendIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_friend)))
        {
            this.friend = getIntent().getParcelableExtra(getString(R.string.intent_friend));
        }
        else
        {
            finish();
        }
    }

    private void CreatePeople()
    {
        this.AuthInstance = FirebaseAuth.getInstance();

        if (AuthInstance.getUid().compareTo(friend.getUid()) < 0)
        {
            people = AuthInstance.getUid() + ' ' + friend.getUid();
        }
        else
        {
            people = friend.getUid() + ' ' + AuthInstance.getUid();
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
        this.Toolbar.setTitle(this.friend.getDisplayName());
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
        this.DBInstance.getFirestoreSettings();
        this.ChatQuery = this.DBInstance
                .collection("chats")
                .whereEqualTo("people", people)
                .orderBy("time", Query.Direction.ASCENDING);
        this.ChatOptions = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(ChatQuery, Message.class)
                .build();
        this.MessageAdapter = new MessageAdapter(this.ChatOptions, this.friend);
        this.MessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                RecyclerView.smoothScrollToPosition(MessageAdapter.getItemCount());
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.MessageAdapter != null) { this.MessageAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.MessageAdapter != null) { this.MessageAdapter.stopListening(); }
    }

    public void SendMessageClick(View view)
    {
        String message = this.messageText.getText().toString();

        if (!Helper.isEmpty(message))
        {
            message = message.replaceAll("\\r\\n|\\r|\\n", " ");

            Map<String, Object> document = new HashMap<>();
            document.put("sender", this.AuthInstance.getUid());
            document.put("recipient", this.friend.getUid());
            document.put("text", this.messageText.getText().toString());
            document.put("time", Timestamp.now());
            document.put("people", people);

            DBInstance.collection("chats")
                      .add(document)
                      .addOnFailureListener(new OnFailureListener()
                      {
                          @Override
                          public void onFailure(@NonNull Exception e)
                          {
                              makeBottomSheetMessage(getString(R.string.message_error));
                          }
                      });

            this.messageText.setText("");
            Helper.hideKeyboard(this);
        }
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
        this.RecyclerView.setHasFixedSize(false);
        this.layoutManager = new LinearLayoutManager(this);
        this.layoutManager.setStackFromEnd(true);
        this.RecyclerView.setLayoutManager(layoutManager);
        this.RecyclerView.setAdapter(this.MessageAdapter);

        this.messageText = findViewById(R.id.ChatPage_Message);

    }

    private void makeBottomSheetMessage(String text)
    {
        Snackbar snackbar = Snackbar.make(this.RecyclerView,
                                          text,
                                          Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
        snackbar.show();
    }

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