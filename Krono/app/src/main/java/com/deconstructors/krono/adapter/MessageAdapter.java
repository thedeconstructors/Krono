package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder>
{

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options)
    {
        super(options);
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
        int res = R.layout.chat_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        return new MessageHolder(view);
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
        holder.nameText.setText(model.getSender());
        holder.recipientText.setText(model.getRecipient());
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

            this.nameText = (TextView) itemView.findViewById(R.id.chatlist_nameText);
            this.messageText = (TextView) itemView.findViewById(R.id.chatlist_messageText);
            this.timeText = (TextView) itemView.findViewById(R.id.chatlist_time);
        }
    }
}
