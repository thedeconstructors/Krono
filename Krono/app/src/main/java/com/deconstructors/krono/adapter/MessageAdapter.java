package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Message;
import com.deconstructors.krono.module.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder>
{
    private int count = 0;
    private FirebaseAuth Auth;
    private User friend;
    private List<Message> messages;
    private String fid;
    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    //public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, List<Message> messages, User friend)
    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, User friend)
    {
        super(options);
        this.friend = friend;
        this.messages = new ArrayList<>(getSnapshots());
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the ui_drawer_listitem.XML File
     ************************************************************************/

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = 0;
        if (getViewHolder(parent.getChildCount()) == 1)
            res = R.layout.chat_listitem;
        else
            res = R.layout.chat_listitem_self;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        return new MessageHolder(view);
    }

    private int getViewHolder(int index)
    {
        int type = 0;
        if (this.getItem(index).getSender().contains(this.friend.getUid()))
        {
            type = 1;
        }

        return type;
        //if (FirebaseAuth.getInstance().getUid().contains())
        /*if (count == 0)
            ++count;
        else
            --count;
        return count;*/
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder,
                                    int position,
                                    @NonNull Message model)
    {
        //holder.nameText.setText(model.getSender());
        //holder.recipientText.setText(model.getRecipient());
        holder.messageText.setText(model.getText());
        holder.timeText.setText(model.getTime());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class MessageHolder extends RecyclerView.ViewHolder
    {
        TextView nameText;
        TextView messageText;
        TextView timeText;
        TextView recipientText;

        public MessageHolder(@NonNull View itemView)
        {
            super(itemView);

            //this.nameText = (TextView) itemView.findViewById(R.id.chatlist_nameText);
            this.messageText = (TextView) itemView.findViewById(R.id.chatlist_messageText);
            this.timeText = (TextView) itemView.findViewById(R.id.chatlist_time);
        }
    }
}